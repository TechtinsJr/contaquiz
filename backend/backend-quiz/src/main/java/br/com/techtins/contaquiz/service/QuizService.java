package br.com.techtins.contaquiz.service;

import br.com.techtins.contaquiz.dto.quiz.QuizRequest;
import br.com.techtins.contaquiz.exception.BusinessException;
import br.com.techtins.contaquiz.exception.ResourceNotFoundException;
import br.com.techtins.contaquiz.mapper.QuizMapper;
import br.com.techtins.contaquiz.model.Discipline;
import br.com.techtins.contaquiz.model.Question;
import br.com.techtins.contaquiz.model.Quiz;
import br.com.techtins.contaquiz.model.User;
import br.com.techtins.contaquiz.repository.DisciplineRepository;
import br.com.techtins.contaquiz.repository.QuestionRepository;
import br.com.techtins.contaquiz.repository.QuizRepository;
import br.com.techtins.contaquiz.repository.UserRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.hibernate.Hibernate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class QuizService {

    @Inject
    QuizRepository quizRepository;

    @Inject
    DisciplineRepository disciplineRepository;

    @Inject
    QuestionRepository questionRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    QuizMapper quizMapper;

    public PanacheQuery<Quiz> findAll(String title, Long disciplineId, Long currentUserId) {
        StringBuilder jpql = new StringBuilder("SELECT q FROM Quiz q WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        if (title != null && !title.isBlank()) {
            jpql.append(" AND LOWER(q.title) LIKE LOWER(:title)");
            params.put("title", "%" + title + "%");
        }

        if (disciplineId != null) {
            jpql.append(" AND q.discipline.id = :disciplineId");
            params.put("disciplineId", disciplineId);
        }

        if (currentUserId != null) {
            jpql.append(" AND (q.visibility = 'PUBLIC' OR (q.visibility = 'PRIVATE' AND q.createdByUser.id = :currentUserId))");
            params.put("currentUserId", currentUserId);
        } else {
            jpql.append(" AND q.visibility = 'PUBLIC'");
        }

        return quizRepository.find(jpql.toString(), params);
    }

    public Quiz findById(Long id) {
        Quiz quiz = quizRepository.findById(id);
        if (quiz == null) {
            throw new ResourceNotFoundException("Quiz não encontrado: " + id);
        }
        return quiz;
    }

    @Transactional
    public Quiz create(QuizRequest dto, Long adminUserId) {
        if (quizRepository.findByTitle(dto.title()) != null) {
            throw new BusinessException("Já existe um quiz com este título");
        }

        User admin = userRepository.findById(adminUserId);
        if (admin == null) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + adminUserId);
        }

        Discipline discipline = null;
        if (dto.disciplineId() != null) {
            discipline = disciplineRepository.findById(dto.disciplineId());
            if (discipline == null) {
                throw new ResourceNotFoundException("Disciplina não encontrada: " + dto.disciplineId());
            }
        }

        Set<Question> questions = new HashSet<>();
        if (dto.questionIds() != null && !dto.questionIds().isEmpty()) {
            for (Long questionId : dto.questionIds()) {
                Question question = questionRepository.findById(questionId);
                if (question == null) {
                    throw new ResourceNotFoundException("Questão não encontrada: " + questionId);
                }
                Hibernate.initialize(question.getOptions());
                Hibernate.initialize(question.getTopics());
                questions.add(question);
            }
        }

        Quiz quiz = quizMapper.toEntity(dto, admin, discipline, questions);
        quizRepository.persist(quiz);
        return quiz;
    }

    @Transactional
    public Quiz update(Long id, QuizRequest dto) {
        Quiz quiz = findById(id);

        Quiz existing = quizRepository.findByTitle(dto.title());
        if (existing != null && !existing.getId().equals(id)) {
            throw new BusinessException("Já existe um quiz com este título");
        }

        Discipline discipline = null;
        if (dto.disciplineId() != null) {
            discipline = disciplineRepository.findById(dto.disciplineId());
            if (discipline == null) {
                throw new ResourceNotFoundException("Disciplina não encontrada: " + dto.disciplineId());
            }
        }

        Set<Question> questions = null;
        if (dto.questionIds() != null) {
            questions = new HashSet<>();
            for (Long questionId : dto.questionIds()) {
                Question question = questionRepository.findById(questionId);
                if (question == null) {
                    throw new ResourceNotFoundException("Questão não encontrada: " + questionId);
                }
                Hibernate.initialize(question.getOptions());
                Hibernate.initialize(question.getTopics());
                questions.add(question);
            }
        }

        quizMapper.updateEntity(quiz, dto, discipline, questions);
        return quiz;
    }

    @Transactional
    public void delete(Long id) {
        Quiz quiz = findById(id);
        quiz.setActive(false);
    }
}

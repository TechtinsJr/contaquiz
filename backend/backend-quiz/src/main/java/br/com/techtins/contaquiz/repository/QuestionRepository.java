package br.com.techtins.contaquiz.repository;

import br.com.techtins.contaquiz.model.Question;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuestionRepository implements PanacheRepositoryBase<Question, Long> {
}

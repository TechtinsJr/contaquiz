package br.com.techtins.contaquiz.repository;

import br.com.techtins.contaquiz.model.QuizResult;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuizResultRepository implements PanacheRepositoryBase<QuizResult, Long> {
}

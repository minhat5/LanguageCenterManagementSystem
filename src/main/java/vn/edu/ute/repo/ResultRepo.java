package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Result;

import java.util.List;

public interface ResultRepo {
    void save(EntityManager em, Result result);
    void update(EntityManager em, Result result);
    void delete(EntityManager em, Long resultId);
    List<Result> findAll(EntityManager em);
}

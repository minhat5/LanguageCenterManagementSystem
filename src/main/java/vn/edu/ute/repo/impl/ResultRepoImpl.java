package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Result;
import vn.edu.ute.repo.ResultRepo;

import java.util.List;

public class ResultRepoImpl implements ResultRepo {
    @Override
    public void save(EntityManager em, Result result) {
        em.persist(result);
    }

    @Override
    public void update(EntityManager em, Result result) {
        em.merge(result);
    }

    @Override
    public void delete(EntityManager em, Long resultId) {
        Result result = em.find(Result.class, resultId);
        if (result != null) em.remove(result);
    }

    @Override
    public List<Result> findAll(EntityManager em) {
        return em.createQuery("SELECT r FROM Result r JOIN FETCH r.student JOIN FETCH r.clas ORDER BY r.updatedAt DESC", Result.class).getResultList();
    }
}

package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.repo.PlacementTestRepo;

import java.util.List;

public class PlacementTestRepoImpl implements PlacementTestRepo {

    @Override
    public void save(EntityManager em, PlacementTest test) {
        em.persist(test);
    }

    @Override
    public List<PlacementTest> findByStudentId(EntityManager em, Long studentId) {
        String jpql = "SELECT p FROM PlacementTest p WHERE p.student.studentId = :sid";
        return em.createQuery(jpql, PlacementTest.class)
                .setParameter("sid", studentId)
                .getResultList();
    }
    @Override
    public List<PlacementTest> findAll(EntityManager em) {
        String jpql = "SELECT p FROM PlacementTest p JOIN FETCH p.student ORDER BY p.testDate DESC";
        return em.createQuery(jpql, PlacementTest.class).getResultList();
    }

    @Override
    public PlacementTest findById(EntityManager em, Long testId) {
        return em.find(PlacementTest.class, testId);
    }

    @Override
    public void delete(EntityManager em, Long testId) {
        PlacementTest test = em.find(PlacementTest.class, testId);
        if (test != null) {
            em.remove(test);
        }
    }
}

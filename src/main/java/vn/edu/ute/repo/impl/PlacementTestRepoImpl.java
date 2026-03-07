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
}

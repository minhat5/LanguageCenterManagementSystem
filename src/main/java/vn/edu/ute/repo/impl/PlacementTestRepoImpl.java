package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.repo.PlacementTestRepo;

import java.util.List;

public class PlacementTestRepoImpl implements PlacementTestRepo {
    private final EntityManager em;

    public PlacementTestRepoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(PlacementTest test) {
        try {
            em.getTransaction().begin();
            em.persist(test);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e; // Ném lỗi lên Service xử lý
        }
    }

    @Override
    public List<PlacementTest> findByStudentId(Long studentId) {
        String jpql = "SELECT p FROM PlacementTest p WHERE p.student.studentId = :sid";
        return em.createQuery(jpql, PlacementTest.class)
                .setParameter("sid", studentId)
                .getResultList();
    }
}

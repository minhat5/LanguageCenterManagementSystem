package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.repo.EnrollmentRepo;

import java.util.List;

public class EnrollmentRepoImpl implements EnrollmentRepo {

    @Override
    public void save(EntityManager em, Enrollment enrollment) {
        em.persist(enrollment);
    }

    @Override
    public List<Enrollment> findAll(EntityManager em) {
        String jpql = "SELECT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.clas ORDER BY e.enrollmentDate DESC";
        return em.createQuery(jpql, Enrollment.class).getResultList();
    }
}

package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.repo.EnrollmentRepo;

import java.util.List;

public class EnrollmentRepoImpl implements EnrollmentRepo {
    @Override
    public List<Enrollment> findAll(EntityManager em) {
        String jpql = "SELECT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.clas ORDER BY e.enrollmentDate DESC";
        return em.createQuery(jpql, Enrollment.class).getResultList();
    }

    @Override
    public List<Enrollment> findByClassId(EntityManager em, Long classId) {
        String jpql = "SELECT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.clas c WHERE c.classId = :classId";
        return em.createQuery(jpql, Enrollment.class)
                .setParameter("classId", classId)
                .getResultList();
    }
}

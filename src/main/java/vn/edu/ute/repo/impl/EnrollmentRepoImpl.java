package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.repo.EnrollmentRepo;

import java.util.List;

public class EnrollmentRepoImpl implements EnrollmentRepo {

    public final EntityManager em;

    public EnrollmentRepoImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Enrollment enrollment) {
        em.getTransaction().begin();
        em.persist(enrollment);
        em.getTransaction().commit();
    }

    @Override
    public List<Enrollment> findAll() {
        return em.createQuery("SELECT e FROM Enrollment e", Enrollment.class).getResultList();
    }
}

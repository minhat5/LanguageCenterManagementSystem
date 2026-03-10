package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Payment;
import vn.edu.ute.repo.PaymentRepo;

import java.util.List;

public class PaymentRepoImpl implements PaymentRepo {
    @Override
    public void save(EntityManager em, Payment payment) {
        em.persist(payment);
    }

    @Override
    public List<Payment> findAll(EntityManager em) {
        String jpql = "SELECT p FROM Payment p JOIN FETCH p.student LEFT JOIN FETCH p.enrollment LEFT JOIN FETCH p.invoice ORDER BY p.paymentDate DESC";
        return em.createQuery(jpql, Payment.class).getResultList();
    }
}

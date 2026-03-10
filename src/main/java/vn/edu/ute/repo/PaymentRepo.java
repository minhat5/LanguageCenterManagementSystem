package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Payment;

import java.util.List;

public interface PaymentRepo {
    void save(EntityManager em, Payment payment);
    List<Payment> findAll (EntityManager em);
}

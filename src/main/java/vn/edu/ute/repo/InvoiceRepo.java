package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Invoice;

import java.util.List;

public interface InvoiceRepo {
    void save(EntityManager em, Invoice invoice);
    void update(EntityManager em, Invoice invoice);
    Invoice findById(EntityManager em, Long invoiceId);
    List<Invoice> findAll(EntityManager em);
    List<Invoice> findByStudentId(EntityManager em, Long studentId);
}

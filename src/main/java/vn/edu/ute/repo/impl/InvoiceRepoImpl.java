package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Invoice;
import vn.edu.ute.repo.InvoiceRepo;

import java.util.List;

public class InvoiceRepoImpl implements InvoiceRepo {

    @Override
    public void save(EntityManager em, Invoice invoice) {
        em.persist(invoice);
    }

    @Override
    public void update(EntityManager em, Invoice invoice) {
        em.merge(invoice);
    }

    @Override
    public Invoice findById(EntityManager em, Long invoiceId) {
        return em.find(Invoice.class, invoiceId);
    }

    @Override
    public List<Invoice> findAll(EntityManager em) {
        String jpql = "SELECT i FROM Invoice i JOIN FETCH i.student LEFT JOIN FETCH i.promotion ORDER BY i.issueDate DESC";
        return em.createQuery(jpql, Invoice.class).getResultList();
    }
}

package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.repo.CertificateRepo;

import java.util.List;

public class CertificateRepoImpl implements CertificateRepo {
    @Override
    public void save(EntityManager em, Certificate certificate) {
        em.persist(certificate);
    }

    @Override
    public void delete(EntityManager em, Long certId) {
        Certificate certificate = em.find(Certificate.class, certId);
        if (certificate != null) em.remove(certificate);
    }

    @Override
    public List<Certificate> findAll(EntityManager em) {
        return em.createQuery("SELECT c FROM Certificate c LEFT JOIN FETCH c.student LEFT JOIN FETCH c.clas cl LEFT JOIN FETCH cl.course ORDER BY c.issueDate DESC", Certificate.class).getResultList();
    }
}

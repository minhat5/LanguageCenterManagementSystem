package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Certificate;

import java.util.List;

public interface CertificateRepo {
    void save(EntityManager em, Certificate certificate);
    void delete(EntityManager em, Long certId);
    List<Certificate> findAll(EntityManager em);
}

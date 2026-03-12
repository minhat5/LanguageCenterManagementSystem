package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Enrollment;

import java.util.List;

public interface EnrollmentRepo {
    void save(EntityManager em, Enrollment enrollment);
    List<Enrollment> findAll(EntityManager em);
    List<Enrollment> findByClassId(EntityManager em, Long classId);
}

package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Student;

public interface StudentRepo {
    Student findById(EntityManager em, Long studentId);
}

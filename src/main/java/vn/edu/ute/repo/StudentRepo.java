package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Student;

import java.util.Optional;
import java.util.List;

public interface StudentRepo {
    Student save(EntityManager em, Student student);
    Optional<Student> findById(EntityManager em, Long id);
    boolean existsByEmail(EntityManager em, String email);
    boolean existsByPhone(EntityManager em, String phone);
    List<Student> findAll(EntityManager em);
    void deleteById(EntityManager em, Long id);
}

package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository {
    Teacher save(EntityManager em, Teacher teacher);
    Optional<Teacher> findById(EntityManager em, Long id);
    List<Teacher> findAll(EntityManager em);
    void deleteById(EntityManager em, Long id);
    boolean existsByEmail(EntityManager em, String email);
    boolean existsByPhone(EntityManager em, String phone);
}

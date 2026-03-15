package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherRepo {
    // Tìm tất cả giáo viên
    List<Teacher> findAll(EntityManager em);
    Teacher save(EntityManager em, Teacher teacher);
    Optional<Teacher> findById(EntityManager em, Long id);
    void deleteById(EntityManager em, Long id);
    boolean existsByEmail(EntityManager em, String email);
    boolean existsByPhone(EntityManager em, String phone);
}

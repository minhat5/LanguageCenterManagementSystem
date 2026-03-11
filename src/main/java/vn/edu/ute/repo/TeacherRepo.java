package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;

import java.util.List;

public interface TeacherRepo {
    // Tìm tất cả giáo viên
    List<Teacher> findAll(EntityManager em);
}

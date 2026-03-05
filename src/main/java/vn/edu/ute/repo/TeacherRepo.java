package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Teacher;

import java.util.List;

public interface TeacherRepo {
    List<Teacher> findAll(EntityManager em);
    Teacher findById(EntityManager em, String id);
}

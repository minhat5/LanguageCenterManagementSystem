package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Course;

import java.util.List;

public interface CourseRepository {
    List<Course> findAll(EntityManager em);
    void insert(EntityManager em, Course course);
    void update(EntityManager em, Course course);
    void delete(EntityManager em, Long id);
    Course findById(EntityManager em, Long id);
}

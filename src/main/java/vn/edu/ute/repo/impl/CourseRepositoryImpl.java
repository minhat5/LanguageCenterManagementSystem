package vn.edu.ute.repo.impl;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Course;
import vn.edu.ute.repo.CourseRepository;

import java.util.List;

public class CourseRepositoryImpl implements CourseRepository {
    @Override
    public List<Course> findAll(EntityManager em) {
        return em.createQuery("select c from Course c", Course.class).getResultList();
    }

    @Override
    public void insert(EntityManager em, Course course) {
        em.persist(course);
    }

    @Override
    public void update(EntityManager em, Course course) {
        em.merge(course);
    }

    @Override
    public void delete(EntityManager em, Long id) {
        //Kiểm tra xem khoá học có tồn tại không trước khi xoá
        Course course = em.find(Course.class, id);
        if(course == null) {
            throw new IllegalArgumentException("Không tìm thấy khoá học với mã khoá học " + id);
        }
        em.remove(course);
    }

    @Override
    public Course findById(EntityManager em, Long id) {
        return em.find(Course.class, id);
    }
}

package vn.edu.ute.repo;

import jakarta.persistence.EntityManager;
import vn.edu.ute.model.Course;

import java.util.List;

public interface CourseRepo {
    //Lấy tất cả khoá học
    List<Course> findAll(EntityManager em);
    //Thêm khoá học mới
    void insert(EntityManager em, Course course);
    //Cập nhật thông tin khoá học
    void update(EntityManager em, Course course);
    //Xoá khoá học
    void delete(EntityManager em, Long id);
    //Tìm khoá học theo id
    Course findById(EntityManager em, Long id);
}

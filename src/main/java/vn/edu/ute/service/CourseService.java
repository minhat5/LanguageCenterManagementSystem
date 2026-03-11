package vn.edu.ute.service;

import vn.edu.ute.common.enumeration.Level;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.model.Course;

import java.util.List;
import java.util.Map;

public interface CourseService {
    List<Course> getAll() throws Exception;
    void insert(Course course) throws Exception;
    void update(Course course) throws Exception;
    void delete(Long id) throws Exception;
    Map<Boolean, List<Course>> getCoursesByStatus(List<Course> courses);
    List<Course> findByName(List<Course> courses, String name);
    List<Course> getCoursesByLevel(List<Course> courses, Level level);
    Course findById(Long id) throws Exception;
}

package vn.edu.ute.service;

import vn.edu.ute.common.enumeration.Level;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Course;
import vn.edu.ute.repo.CourseRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CourseService {
    private final CourseRepository courseRepository;
    private final TransactionManager tx;

    public CourseService(CourseRepository courseRepository, TransactionManager tx) {
        this.courseRepository = courseRepository;
        this.tx = tx;
    }

    public List<Course> getAll() throws Exception{
        return tx.runInTransaction(courseRepository::findAll);
    }

    public void insert(Course course)  throws Exception{
        tx.runInTransaction(em -> {
            courseRepository.insert(em, course);
            return null;
        });
    }

    public void delete(Long id) throws Exception{
        tx.runInTransaction(em -> {
            Course course = courseRepository.findById(em, id);
            if (course == null) {
                throw new IllegalArgumentException("Course not found with id: " + id);
            }
            courseRepository.delete(em, id);
            return null;
        });
    }

    public void update(Course course) throws Exception {
        tx.runInTransaction(em -> {
            Course existingCourse = courseRepository.findById(em, course.getCourseId());
            if(existingCourse == null) {
                throw new IllegalArgumentException("Course not found with id: " + course.getCourseId());
            }
            courseRepository.update(em, course);
            return null;
        });
    }


    public List<Course> getAllActiveCourses(List<Course> courses) {
        return courses.stream()
                .filter(c -> c.getStatus() == Status.Active)
                .toList();
    }

    public List<Course> findByName(List<Course> courses, String name) {
        String searchName = name.toLowerCase().trim();
        return courses.stream()
                .filter(c -> c.getCourseName().toLowerCase().contains(searchName))
                .toList();
    }

    public Map<Level, List<Course>> groupByLevel(List<Course> courses) {
        return courses.stream()
                .collect(Collectors.groupingBy(Course::getLevel));
    }
}
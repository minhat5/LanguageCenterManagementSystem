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

    // Lấy tất cả khoá học
    public List<Course> getAll() throws Exception{
        return tx.runInTransaction(courseRepository::findAll);
    }

    // Thêm khoá học mới
    public void insert(Course course)  throws Exception{
        tx.runInTransaction(em -> {
            courseRepository.insert(em, course);
            return null;
        });
    }

    // Xoá khoá học
    public void delete(Long id) throws Exception{
        tx.runInTransaction(em -> {
            // Kiểm tra xem khoá học có tồn tại không trước khi xoá
            Course course = courseRepository.findById(em, id);
            if (course == null) {
                throw new IllegalArgumentException("Không tìm thấy khoá học với mã khoá học: " + id);
            }
            courseRepository.delete(em, id);
            return null;
        });
    }

    // Cập nhật thông tin khoá học
    public void update(Course course) throws Exception {
        tx.runInTransaction(em -> {
            // Kiểm tra xem khoá học có tồn tại không trước khi cập nhật
            Course existingCourse = courseRepository.findById(em, course.getCourseId());
            if(existingCourse == null) {
                throw new IllegalArgumentException("Không tìm thấy khoá học với mã khoá học: " + course.getCourseId());
            }
            courseRepository.update(em, course);
            return null;
        });
    }

    // Lọc khoá học theo trạng thái
    public List<Course> getCoursesByStatus(List<Course> courses, Status status) {
        return courses.stream()
                .filter(c -> c.getStatus() == status)
                .toList();
    }

    // Tìm khoá học theo tên (tìm kiếm không phân biệt chữ hoa chữ thường)
    public List<Course> findByName(List<Course> courses, String name) {
        String searchName = name.toLowerCase().trim();
        return courses.stream()
                .filter(c -> c.getCourseName().toLowerCase().contains(searchName))
                .toList();
    }

    // Lọc khoá học theo cấp độ
    public List<Course> getCoursesByLevel(List<Course> courses, Level level) {
        return courses.stream()
                .filter(c -> c.getLevel() == level)
                .toList();
    }
}
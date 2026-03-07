package vn.edu.ute.service.impl;

import vn.edu.ute.common.enumeration.Level;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Course;
import vn.edu.ute.repo.CourseRepo;
import vn.edu.ute.service.CourseService;

import java.util.List;

public class CourseServiceImpl implements CourseService {
    private final CourseRepo courseRepository;
    private final TransactionManager tx;

    public CourseServiceImpl(CourseRepo courseRepository, TransactionManager tx) {
        this.courseRepository = courseRepository;
        this.tx = tx;
    }

    // Lấy tất cả khoá học
    @Override
    public List<Course> getAll() throws Exception{
        return tx.runInTransaction(courseRepository::findAll);
    }

    // Thêm khoá học mới
    @Override
    public void insert(Course course) throws Exception {
        tx.runInTransaction(em -> {
            courseRepository.insert(em, course);
            return null;
        });
    }

    // Xoá khoá học
    @Override
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
    @Override
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
    @Override
    public List<Course> getCoursesByStatus(List<Course> courses, Status status) {
        return courses.stream()
                .filter(c -> c.getStatus() == status)
                .toList();
    }

    // Tìm khoá học theo tên (tìm kiếm không phân biệt chữ hoa chữ thường)
    @Override
    public List<Course> findByName(List<Course> courses, String name) {
        String searchName = name.toLowerCase().trim();
        return courses.stream()
                .filter(c -> c.getCourseName().toLowerCase().contains(searchName))
                .toList();
    }

    // Lọc khoá học theo cấp độ
    @Override
    public List<Course> getCoursesByLevel(List<Course> courses, Level level) {
        return courses.stream()
                .filter(c -> c.getLevel() == level)
                .toList();
    }
}
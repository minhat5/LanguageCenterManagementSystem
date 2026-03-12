package vn.edu.ute.service.impl;

import vn.edu.ute.common.enumeration.ClassStatus;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.dto.ClasView;
import vn.edu.ute.model.Branch;
import vn.edu.ute.model.Clas;
import vn.edu.ute.model.Course;
import vn.edu.ute.repo.ClasRepo;
import vn.edu.ute.service.ClasService;

import java.util.List;

public class ClasServiceImpl implements ClasService {
    private final ClasRepo clasRepo;
    private final TransactionManager tx;

    public ClasServiceImpl(ClasRepo clasRepo, TransactionManager tx) {
        this.clasRepo = clasRepo;
        this.tx = tx;
    }

    // Lấy tất cả lớp học
    @Override
    public List<Clas> getAll() throws Exception {
        return tx.runInTransaction(clasRepo::findAll);
    }

    // Thêm lớp học mới
    @Override
    public void insert(Clas clas) throws Exception {
        tx.runInTransaction(em -> {
            clasRepo.insert(em, clas);
            return null;
        });
    }

    // Cập nhật thông tin lớp học
    @Override
    public void update(Clas clas) throws Exception {
        tx.runInTransaction(em -> {
            // Kiểm tra xem lớp học có tồn tại không trước khi cập nhật
            Clas existingClas = clasRepo.findById(em, clas.getClassId());
            if (existingClas == null) {
                throw new IllegalArgumentException("Không tìm thấy lớp học với mã lớp học: " + clas.getClassId());
            }
            clasRepo.update(em, clas);
            return null;
        });
    }

    // Xoá lớp học
    @Override
    public void delete(Long id) throws Exception {
        tx.runInTransaction(em -> {
            // Kiểm tra xem lớp học có tồn tại không trước khi xoá
            Clas existingClas = clasRepo.findById(em, id);
            if (existingClas == null) {
                throw new IllegalArgumentException("Không tìm thấy lớp học với mã lớp học: " + id);
            }
            clasRepo.delete(em, id);
            return null;
        });
    }

    // Tìm lớp học theo id
    @Override
    public Clas findById(Long id) throws Exception {
        return tx.runInTransaction(em -> {
            Clas clas = clasRepo.findById(em, id);
            if (clas == null) {
                throw new IllegalArgumentException("Không tìm thấy lớp học với mã lớp học: " + id);
            }
            return clas;
        });
    }

    // Chuyển đổi danh sách lớp học sang danh sách ClasView để hiển thị thông tin
    @Override
    public List<ClasView> toClasView(List<Clas> classes) {
        // Mở stream luồng dữ liệu từ danh sách lớp học
        return classes.stream()
                // Map từng dữ liệu Lớp (Clas) thành dữ liệu View DTO (ClasView)
                .map(c -> new ClasView(
                        c.getClassId(),
                        c.getClassName(),
                        c.getCourse().getCourseName(),
                        c.getTeacher() != null ? c.getTeacher().getFullName() : "Chưa có giáo viên",
                        c.getRoom() != null ? c.getRoom().getRoomName() : "Chưa có phòng học",
                        c.getBranch() != null ? c.getBranch().getBranchName() : "Chưa có chi nhánh",
                        c.getStartDate(),
                        c.getEndDate(),
                        c.getMaxStudent(),
                        c.getStatus(),
                        c.getCreatedAt(),
                        c.getUpdatedAt()))
                // Trả về danh sách được biến đổi về kiểu ClasView
                .toList();
    }

    // Các phương thức lọc lớp học theo trạng thái, khoá học, chi nhánh hoặc tìm
    // kiếm theo tên
    @Override
    public List<Clas> getClasViewsByStatus(List<Clas> classes, ClassStatus classStatus) {
        // Xử lý stream lọc theo trạng thái của lớp
        return classes.stream()
                .filter(c -> c.getStatus() == classStatus) // Bộ lọc đánh giá điều kiện trạng thái
                .toList();
    }

    @Override
    public List<Clas> getClasViewsByCourse(List<Clas> classes, Course course) {
        // Dùng java stream để lọc thông tin
        return classes.stream()
                // Giữ lại các lớp thuộc một khóa học nhất định
                .filter(c -> c.getCourse() != null && c.getCourse().getCourseId().equals(course.getCourseId()))
                .toList();
    }

    @Override
    public List<Clas> getClasViewsByBranch(List<Clas> classes, Branch branch) {
        // Dùng java stream để lọc thông tin lớp có chi nhánh tương ứng
        return classes.stream()
                .filter(c -> c.getTeacher() != null && c.getBranch().getBranchId().equals(branch.getBranchId()))
                .toList();
    }

    @Override
    public List<Clas> findByName(List<Clas> classes, String name) {
        String searchName = name.toLowerCase().trim();
        // Tạo luồng dữ liệu stream
        return classes.stream()
                // Thực thi phép lọc bằng cách xét chứa (contains) chuỗi chữ thường
                .filter(c -> c.getClassName().toLowerCase().contains(searchName))
                .toList();
    }

    // Lấy tất cả lớp học đang hoạt động (không bị huỷ hoặc đã hoàn thành)
    @Override
    public List<Clas> getAllActiveClasses() throws Exception {
        List<Clas> allClasses = getAll();
        // Khởi tạo luồng xử lý
        return allClasses.stream()
                // Lọc loại bỏ lớp đã Hủy (Cancelled) hoặc Hoàn thành (Completed)
                .filter(c -> c.getStatus() != ClassStatus.Cancelled && c.getStatus() != ClassStatus.Completed)
                .toList();
    }
}

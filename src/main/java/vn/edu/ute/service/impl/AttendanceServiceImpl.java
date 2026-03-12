package vn.edu.ute.service.impl;

import vn.edu.ute.common.enumeration.AttendanceStatus;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.dto.AttendanceView;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Clas;
import vn.edu.ute.repo.AttendanceRepo;
import vn.edu.ute.service.AttendanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepo attendanceRepo;
    private final TransactionManager tx;

    public AttendanceServiceImpl(AttendanceRepo attendanceRepo, TransactionManager tx) {
        this.attendanceRepo = attendanceRepo;
        this.tx = tx;
    }

    // Lấy tất cả điểm danh
    @Override
    public List<Attendance> getAll() throws Exception {
        return tx.runInTransaction(attendanceRepo::findAll);
    }

    // Cập nhật điểm danh
    @Override
    public void update(Attendance attendance) throws Exception {
        tx.runInTransaction(em -> {
            Attendance existingAttendance = attendanceRepo.findById(em, attendance.getAttendanceId());
            if (existingAttendance == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy điểm danh với mã điểm danh: " + attendance.getAttendanceId());
            }
            attendanceRepo.update(em, attendance);
            return null;
        });
    }

    // Tìm kiếm điểm danh theo ID
    @Override
    public Attendance findById(Long id) throws Exception {
        return tx.runInTransaction(em -> {
            Attendance attendance = attendanceRepo.findById(em, id);
            if (attendance == null) {
                throw new IllegalArgumentException("Không tìm thấy điểm danh với mã điểm danh: " + id);
            }
            return attendance;
        });
    }

    // Chuyển đổi danh sách điểm danh sang dạng hiển thị
    @Override
    public List<AttendanceView> toAttendanceView(List<Attendance> attendances) {
        // Mở cơ chế stream để duyệt danh sách điểm danh
        return attendances.stream()
                // Ánh xạ (map) từ đối tượng Entity gốc (Attendance) sang đối tượng DTO hiển thị
                // UI (AttendanceView)
                .map(a -> new AttendanceView(
                        a.getAttendanceId(),
                        a.getStudent().getFullName(),
                        a.getClas().getClassName(),
                        a.getAttendDate(),
                        a.getStatus(),
                        a.getNote(),
                        a.getCreatedAt()))
                // Gói kết quả vào cấu trúc List
                .toList();
    }

    // Lọc điểm danh theo lớp học
    @Override
    public List<Attendance> getByClass(List<Attendance> attendances, Clas clas) {
        // Tạo stream dữ liệu từ danh sách
        return attendances.stream()
                // Loại bỏ những record không nằm trong lớp học cần tìm để trả về list kết quả
                .filter(a -> a.getClas().getClassId().equals(clas.getClassId()))
                .toList();
    }

    // Lọc điểm danh theo ngày điểm danh
    @Override
    public List<Attendance> getByAttendDate(List<Attendance> attendances, LocalDate attendDate) {
        // Chạy stream thao tác trên Collection
        return attendances.stream()
                // Bộ lọc chỉ lấy các điểm danh của duy nhất ngày truyền vào
                .filter(a -> a.getAttendDate().equals(attendDate))
                .toList();
    }

    // Đếm số lượng điểm danh theo trạng thái
    @Override
    public Map<AttendanceStatus, Long> countAttendanceByStatus(List<Attendance> attendances) {
        // Mở stream thao tác nhóm dữ liệu
        Map<AttendanceStatus, Long> result = attendances.stream()
                // Nhóm kết quả (groupingBy) theo trạng thái và đếm (counting) số lượng từng
                // trạng thái
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));
        for (AttendanceStatus status : AttendanceStatus.values()) {
            result.putIfAbsent(status, 0L);
        }
        return result;
    }
}

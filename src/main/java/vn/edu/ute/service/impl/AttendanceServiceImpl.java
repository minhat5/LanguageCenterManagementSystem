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

    //Lấy tất cả điểm danh
    @Override
    public List<Attendance> getAll() throws Exception {
        return tx.runInTransaction(attendanceRepo::findAll);
    }

    //Cập nhật điểm danh
    @Override
    public void update(Attendance attendance) throws Exception {
        tx.runInTransaction(em -> {
            Attendance existingAttendance = attendanceRepo.findById(em, attendance.getAttendanceId());
            if(existingAttendance == null) {
                throw new IllegalArgumentException("Không tìm thấy điểm danh với mã điểm danh: " + attendance.getAttendanceId());
            }
            attendanceRepo.update(em, attendance);
            return null;
        });
    }

    //Tìm kiếm điểm danh theo ID
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
        return attendances.stream()
                .map(a -> new AttendanceView(
                        a.getAttendanceId(),
                        a.getStudent().getFullName(),
                        a.getClas().getClassName(),
                        a.getAttendDate(),
                        a.getStatus(),
                        a.getNote(),
                        a.getCreatedAt()
                ))
                .toList();
    }

    // Lọc điểm danh theo lớp học
    @Override
    public List<Attendance> getByClass(List<Attendance> attendances, Clas clas) {
        return attendances.stream()
                .filter(a -> a.getClas().getClassId().equals(clas.getClassId()))
                .toList();
    }

    // Lọc điểm danh theo ngày điểm danh
    @Override
    public List<Attendance> getByAttendDate(List<Attendance> attendances, LocalDate attendDate) {
        return attendances.stream()
                .filter(a -> a.getAttendDate().equals(attendDate))
                .toList();
    }

    // Đếm số lượng điểm danh theo trạng thái
    @Override
    public Map<AttendanceStatus, Long> countAttendanceByStatus(List<Attendance> attendances) {
        Map<AttendanceStatus, Long> result = attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getStatus, Collectors.counting()));
        for(AttendanceStatus status : AttendanceStatus.values()) {
            result.putIfAbsent(status, 0L);
        }
        return result;
    }
}

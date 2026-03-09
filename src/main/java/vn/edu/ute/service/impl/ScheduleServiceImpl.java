package vn.edu.ute.service.impl;

import vn.edu.ute.common.enumeration.AttendanceStatus;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.dto.ScheduleView;
import vn.edu.ute.model.*;
import vn.edu.ute.repo.AttendanceRepo;
import vn.edu.ute.repo.EnrollmentRepo;
import vn.edu.ute.repo.ScheduleRepo;
import vn.edu.ute.service.ScheduleService;

import java.time.LocalDate;
import java.util.List;

public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepo scheduleRepo;
    private final AttendanceRepo attendanceRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final TransactionManager tx;

    public ScheduleServiceImpl(ScheduleRepo scheduleRepo, AttendanceRepo attendanceRepo, EnrollmentRepo enrollmentRepo, TransactionManager tx) {
        this.scheduleRepo = scheduleRepo;
        this.attendanceRepo = attendanceRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.tx = tx;
    }

    // Lấy tất cả lịch học
    @Override
    public List<Schedule> getAll() throws Exception {
        return tx.runInTransaction(scheduleRepo::findAll);
    }

    // Thêm lịch học mới đồng thời tạo điểm danh vắng mặt cho tất cả học viên của lớp học đó trong ngày học của lịch học mới được thêm vào
    @Override
    public void insert(Schedule schedule) throws Exception {
        tx.runInTransaction(em -> {
            List<Schedule> schedules = scheduleRepo.findAll(em);
            if (isConflict(schedule, schedules)) {
                throw new IllegalArgumentException("Lịch học bị trùng với lịch học khác về phòng hoặc giáo viên trong cùng giờ");
            }
            scheduleRepo.insert(em, schedule);

            Long classId = schedule.getClas().getClassId();
            List<Enrollment> enrollments = enrollmentRepo.findAll(em);
            enrollments.stream()
                    .filter(e -> e.getClas().getClassId().equals(classId))
                    .forEach(e -> {
                        Attendance attendance = new Attendance();
                        attendance.setStudent(e.getStudent());
                        attendance.setClas(e.getClas());
                        attendance.setStatus(AttendanceStatus.Absent);
                        attendance.setAttendDate(schedule.getStudyDate());
                        attendanceRepo.insert(em, attendance);
                    });
            return null;
        });
    }

    // Cập nhật thông tin lịch học đồng thời cập nhật ngày học của tất cả điểm danh của lớp học đó
    // nếu ngày học của lịch học được cập nhật có thay đổi so với ngày học của lịch học trước khi được cập nhật
    @Override
    public void update(Schedule schedule) throws Exception {
        tx.runInTransaction(em -> {
            Schedule existingSchedule = scheduleRepo.findById(em, schedule.getScheduleId());
            if(existingSchedule == null) {
                throw new IllegalArgumentException("Không tìm thấy lịch học với mã lịch học: " + schedule.getScheduleId());
            }
            List<Schedule> schedules = scheduleRepo.findAll(em);
            schedules.removeIf(existingSchedule::equals);
            if (isConflict(schedule, schedules)) {
                throw new IllegalArgumentException("Lịch học bị trùng với lịch học khác về phòng hoặc giáo viên trong cùng giờ");
            }
            scheduleRepo.update(em, schedule);
            if(!existingSchedule.getStudyDate().equals(schedule.getStudyDate())) {
                List<Attendance> attendances = attendanceRepo.findAll(em);
                attendances.stream()
                        .filter(a -> a.getClas().getClassId().equals(schedule.getClas().getClassId())
                                && a.getAttendDate().equals(existingSchedule.getStudyDate()))
                        .forEach(a -> {
                            a.setAttendDate(schedule.getStudyDate());
                            attendanceRepo.update(em, a);
                        });
            }
            return null;
        });
    }

    // Xoá lịch học đồng thời xoá tất cả điểm danh của lớp học đó trong ngày học của lịch học được xoá
    @Override
    public void delete(Long id) throws Exception {
        tx.runInTransaction(em -> {
            Schedule schedule = scheduleRepo.findById(em, id);
            if (schedule == null) {
                throw new IllegalArgumentException("Không tìm thấy lịch học với mã lịch học: " + id);
            }
            scheduleRepo.delete(em, id);
            List<Attendance> attendances = attendanceRepo.findAll(em);
            attendances.stream()
                    .filter(a -> a.getClas().getClassId().equals(schedule.getClas().getClassId())
                            && a.getAttendDate().equals(schedule.getStudyDate()))
                    .forEach(a -> attendanceRepo.delete(em, a.getAttendanceId()));
            return null;
        });
    }

    // Kiểm tra lịch học mới có bị trùng với phòng hoặc giáo viên trong cùng giờ với lịch học nào đó khác không
    public boolean isConflict(Schedule newSchedule, List<Schedule> existingSchedules) {
        return existingSchedules.stream()
                .anyMatch(s -> s.getStudyDate().equals(newSchedule.getStudyDate())
                        && (s.getRoom().getRoomId().equals(newSchedule.getRoom().getRoomId())
                        || s.getClas().getTeacher().getTeacherId().equals(newSchedule.getClas().getTeacher().getTeacherId()))
                        && s.getStartTime().isBefore(newSchedule.getEndTime())
                        && s.getEndTime().isAfter(newSchedule.getStartTime())
                        && s.getClas().getClassId().equals(newSchedule.getClas().getClassId())
                );
    }

    // Thêm lịch học cho đến ngày kết thúc của lớp học mỗi tuần một lịch để tạo được nhiều lịch học cho một lớp học
    @Override
    public void insertUntilEndDate(Schedule schedule, List<Schedule> existingSchedules) throws Exception {
        LocalDate currentDate = schedule.getStudyDate();
        LocalDate endDate = schedule.getClas().getEndDate();
        while(currentDate.isBefore(endDate)) {
            Schedule newSchedule = new Schedule();
            newSchedule.setClas(schedule.getClas());
            newSchedule.setRoom(schedule.getRoom());
            newSchedule.setStartTime(schedule.getStartTime());
            newSchedule.setEndTime(schedule.getEndTime());
            newSchedule.setStudyDate(currentDate);
            if (!isConflict(schedule, existingSchedules)) {
                insert(newSchedule);
            }
            currentDate = currentDate.plusWeeks(1);
        }
    }

    // Lọc lịch học theo ngày
    @Override
    public List<Schedule> getByDate(List<Schedule> schedules, LocalDate date) {
        return schedules.stream()
                .filter(s -> s.getStudyDate().equals(date))
                .toList();
    }

    // Lọc lịch học theo phòng
    @Override
    public List<Schedule> getByClass(List<Schedule> schedules, Clas clas) {
        return schedules.stream()
                .filter(s -> s.getClas().getClassId().equals(clas.getClassId()))
                .toList();
    }

    // Tìm lịch học theo các trường cụ thể (ngày học, giờ bắt đầu, giờ kết thúc, lớp học) để xoá nhiều lịch học cùng lúc khi xoá một lớp học
    public Schedule findBySpecificFields(Schedule schedule, List<Schedule> existingSchedules) {
        return existingSchedules.stream()
                .filter(s -> s.getStudyDate().equals(schedule.getStudyDate())
                        && s.getStartTime().equals(schedule.getStartTime())
                        && s.getEndTime().equals(schedule.getEndTime())
                        && s.getClas().getClassId().equals(schedule.getClas().getClassId()))
                .findFirst()
                .orElse(null);
    }

    // Xoá lịch học cho đến ngày kết thúc của lớp học mỗi tuần một lịch để xoá được nhiều lịch học cho một lớp học
    @Override
    public void deleteUntilEndDate(Schedule schedule, List<Schedule> existingSchedules) throws Exception {
        LocalDate currentDate = schedule.getStudyDate();
        LocalDate endDate = schedule.getClas().getEndDate();
        while(currentDate.isBefore(endDate)) {
            Schedule newSchedule = new Schedule();
            newSchedule.setClas(schedule.getClas());
            newSchedule.setStartTime(schedule.getStartTime());
            newSchedule.setEndTime(schedule.getEndTime());
            newSchedule.setStudyDate(currentDate);
            Schedule existingSchedule = findBySpecificFields(newSchedule, existingSchedules);
            if (existingSchedule != null) {
                delete(existingSchedule.getScheduleId());
            }
            currentDate = currentDate.plusWeeks(1);
        }
    }

    // Chuyển đổi danh sách lịch học sang danh sách ScheduleView để hiển thị thông tin
    @Override
    public List<ScheduleView> toScheduleView(List<Schedule> schedules) {
        return schedules.stream()
                .map(s -> new ScheduleView(
                        s.getScheduleId(),
                        s.getClas().getClassName(),
                        s.getStudyDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getRoom().getBranch().getBranchName(),
                        s.getRoom().getRoomName(),
                        s.getCreatedAt()
                )).toList();
    }

    // Tìm lịch học theo id
    @Override
    public Schedule findById(Long id) throws Exception {
        return tx.runInTransaction(em -> {
            Schedule schedule = scheduleRepo.findById(em, id);
            if (schedule == null) {
                throw new IllegalArgumentException("Không tìm thấy lịch học với mã lịch học: " + id);
            }
            return schedule;
        });
    }
}

package vn.edu.ute.service.impl;

import vn.edu.ute.common.enumeration.AttendanceStatus;
import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.common.security.AuthContext;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.dto.ScheduleView;
import vn.edu.ute.model.*;
import vn.edu.ute.repo.AttendanceRepo;
import vn.edu.ute.repo.EnrollmentRepo;
import vn.edu.ute.repo.ScheduleRepo;
import vn.edu.ute.service.ScheduleService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepo scheduleRepo;
    private final AttendanceRepo attendanceRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final TransactionManager tx;

    public ScheduleServiceImpl(ScheduleRepo scheduleRepo, AttendanceRepo attendanceRepo, EnrollmentRepo enrollmentRepo,
            TransactionManager tx) {
        this.scheduleRepo = scheduleRepo;
        this.attendanceRepo = attendanceRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.tx = tx;
    }

    // Lấy tất cả lịch học
    @Override
    public List<Schedule> getAll() throws Exception {
        // Mở stream từ danh sách lịch học lấy được qua Transaction
        return tx.runInTransaction(scheduleRepo::findAll).stream()
                // Sắp xếp (sorted) lịch học dựa trên tên lớp học
                .sorted(Comparator.comparing(s -> s.getClas().getClassName()))
                // Chuyển kết quả sang List
                .toList();
    }

    //Lấy tất cả lịch học theo quyền truy cập
    @Override
    public List<Schedule> getAccessibleSchedule() throws Exception {
        if(AuthContext.hasRole(Role.Teacher)) {
            Long teacherId = AuthContext.getCurrentUser().getTeacher().getTeacherId();
            return getAll().stream()
                    // Lọc dựa trên lớp học của lịch học trong đó có teacher id tương ứng
                    .filter(s -> s.getClas() != null && s.getClas().getTeacher() != null && s.getClas().getTeacher().getTeacherId().equals(teacherId))
                    .toList();
        }
        return getAll();
    }

    // Thêm lịch học mới đồng thời tạo điểm danh vắng mặt cho tất cả học viên của
    // lớp học đó trong ngày học của lịch học mới được thêm vào
    @Override
    public void insert(Schedule schedule) throws Exception {
        tx.runInTransaction(em -> {
            List<Schedule> schedules = scheduleRepo.findAll(em);
            if (isConflict(schedule, schedules)) {
                throw new IllegalArgumentException(
                        "Lịch học bị trùng với lịch học khác về phòng hoặc giáo viên trong cùng giờ");
            }
            scheduleRepo.insert(em, schedule);

            Long classId = schedule.getClas().getClassId();
            List<Enrollment> enrollments = enrollmentRepo.findByClassId(em, classId);
            enrollments.forEach(e -> {
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

    // Cập nhật lịch học đồng thời cập nhật ngày học của tất cả điểm danh của lớp
    // học đó trong ngày học cũ của lịch học được cập nhật thành ngày học mới nếu
    // ngày học của lịch học được cập nhật bị thay đổi
    @Override
    public void update(Schedule schedule) throws Exception {
        tx.runInTransaction(em -> {
            Schedule existingSchedule = scheduleRepo.findById(em, schedule.getScheduleId());
            if (existingSchedule == null) {
                throw new IllegalArgumentException(
                        "Không tìm thấy lịch học với mã lịch học: " + schedule.getScheduleId());
            }
            LocalDate oldStudyDate = existingSchedule.getStudyDate();
            LocalDate newStudyDate = schedule.getStudyDate();

            List<Schedule> schedules = scheduleRepo.findAll(em);
            schedules.removeIf(existingSchedule::equals);
            if (isConflict(schedule, schedules)) {
                throw new IllegalArgumentException(
                        "Lịch học bị trùng với lịch học khác về phòng hoặc giáo viên trong cùng giờ");
            }

            scheduleRepo.update(em, schedule);
            if (!oldStudyDate.equals(newStudyDate)) {
                List<Attendance> attendances = attendanceRepo.findByClassId(em, schedule.getClas().getClassId());
                // Tạo stream luồng điểm danh
                attendances.stream()
                        // Lọc các điểm danh đúng với ngày học cũ
                        .filter(a -> a.getAttendDate().equals(oldStudyDate))
                        // Với mỗi (forEach) điểm danh thoả mãn, cập nhật ngày điểm danh sang ngày học
                        // mới
                        .forEach(a -> {
                            a.setAttendDate(newStudyDate);
                            attendanceRepo.update(em, a);
                        });
            }
            return null;
        });
    }

    // Xoá lịch học đồng thời xoá tất cả điểm danh của lớp học đó trong ngày học của
    // lịch học được xoá
    @Override
    public void delete(Long id) throws Exception {
        tx.runInTransaction(em -> {
            Schedule schedule = scheduleRepo.findById(em, id);
            if (schedule == null) {
                throw new IllegalArgumentException("Không tìm thấy lịch học với mã lịch học: " + id);
            }
            List<Attendance> attendances = attendanceRepo.findByClassId(em, schedule.getClas().getClassId());
            // Dùng java stream để lọc danh sách
            attendances.stream()
                    // Chọn ra các điểm danh khớp với ngày học của lịch học bị xoá
                    .filter(a -> a.getAttendDate().equals(schedule.getStudyDate()))
                    // Với mỗi (forEach) điểm danh này, thực thi lệnh xoá thông qua kho dữ liệu
                    // (attendanceRepo.delete)
                    .forEach(a -> attendanceRepo.delete(em, a.getAttendanceId()));
            scheduleRepo.delete(em, id);
            return null;
        });
    }

    // Kiểm tra lịch học mới có bị trùng với phòng hoặc giáo viên trong cùng giờ với
    // lịch học nào đó khác không
    public boolean isConflict(Schedule newSchedule, List<Schedule> existingSchedules) {
        // Khởi tạo Stream từ danh sách các lịch học đang có
        return existingSchedules.stream()
                // Bỏ qua lịch học đang xét (để không tự so sánh trùng với chính nó khi đang
                // trong luồng update)
                .filter(s -> newSchedule.getScheduleId() == null
                        || !s.getScheduleId().equals(newSchedule.getScheduleId()))
                // Bất kì yếu tố nào khớp (anyMatch) với điều kiện bên dưới thì tính là trùng
                // lịch học
                .anyMatch(s -> {
                    // Nếu khác ngày thì chắc chắn không bao giờ trùng
                    if (!s.getStudyDate().equals(newSchedule.getStudyDate())) {
                        return false;
                    }

                    // LUẬT 1: Cùng một ngày, cùng một lớp -> Trùng lịch ngay lập tức!
                    boolean isSameClass = s.getClas().getClassId().equals(newSchedule.getClas().getClassId());
                    if (isSameClass) {
                        return true;
                    }

                    // XÉT TRÙNG GIỜ (Chỉ chạy đến đây nếu là 2 lớp khác nhau)
                    boolean isTimeOverlap = s.getStartTime().isBefore(newSchedule.getEndTime())
                            && s.getEndTime().isAfter(newSchedule.getStartTime());

                    if (isTimeOverlap) {
                        // LUẬT 2: Trùng giờ và Cùng phòng -> Trùng lịch!
                        boolean isSameRoom = s.getRoom().getRoomId().equals(newSchedule.getRoom().getRoomId());

                        // LUẬT 3: Trùng giờ và Cùng giáo viên -> Trùng lịch!
                        boolean isSameTeacher = s.getClas().getTeacher().getTeacherId()
                                .equals(newSchedule.getClas().getTeacher().getTeacherId());

                        if (isSameRoom || isSameTeacher) {
                            return true;
                        }
                    }

                    // Vượt qua hết các luật trên thì là lịch hợp lệ
                    return false;
                });
    }

    // Thêm lịch học cho đến ngày kết thúc của lớp học mỗi tuần một lịch để tạo được
    // nhiều lịch học cho một lớp học
    @Override
    public void insertUntilEndDate(Schedule schedule, List<Schedule> existingSchedules) throws Exception {
        LocalDate currentDate = schedule.getStudyDate();
        LocalDate endDate = schedule.getClas().getEndDate();
        while (currentDate.isBefore(endDate)) {
            Schedule newSchedule = new Schedule();
            newSchedule.setClas(schedule.getClas());
            newSchedule.setRoom(schedule.getRoom());
            newSchedule.setStartTime(schedule.getStartTime());
            newSchedule.setEndTime(schedule.getEndTime());
            newSchedule.setStudyDate(currentDate);
            if (!isConflict(newSchedule, existingSchedules)) {
                insert(newSchedule);
            }
            currentDate = currentDate.plusWeeks(1);
        }
    }

    // Lọc lịch học theo ngày
    @Override
    public List<Schedule> getByDate(List<Schedule> schedules, LocalDate date) {
        // Mở Stream trên tập hợp lịch học
        return schedules.stream()
                // Lọc để lấy duy nhất cấu trúc lịch học bằng ngày học chỉ định
                .filter(s -> s.getStudyDate().equals(date))
                .toList();
    }

    // Lọc lịch học theo phòng
    @Override
    public List<Schedule> getByClass(List<Schedule> schedules, Clas clas) {
        // Chạy stream từ đối tượng List ban đầu
        return schedules.stream()
                // Xếp lọc dựa trên id của lớp học
                .filter(s -> s.getClas().getClassId().equals(clas.getClassId()))
                .toList();
    }

    // Tìm lịch học theo các trường cụ thể (ngày học, giờ bắt đầu, giờ kết thúc, lớp
    // học) để xoá nhiều lịch học cùng lúc khi xoá một lớp học
    public Schedule findBySpecificFields(Schedule schedule, List<Schedule> existingSchedules) {
        // Khởi động luồng (Stream) duyệt mảng lịch học
        return existingSchedules.stream()
                // Kiểm tra 4 yếu tố cùng lúc: chung Ngày học, chung Giờ bắt đầu, chung Giờ kết
                // thúc và cùng chung 1 Lớp
                .filter(s -> s.getStudyDate().equals(schedule.getStudyDate())
                        && s.getStartTime().equals(schedule.getStartTime())
                        && s.getEndTime().equals(schedule.getEndTime())
                        && s.getClas().getClassId().equals(schedule.getClas().getClassId()))
                // Tìm kiếm lấy ngay thông tin lớp học thỏa mãn đầu tiên tìm thấy
                .findFirst()
                // Nếu không tìm ra kết quả nào, stream sẽ trả về giá trị vắng mặc định là
                // `null`
                .orElse(null);
    }

    // Xoá lịch học cho đến ngày kết thúc của lớp học mỗi tuần một lịch để xoá được
    // nhiều lịch học cho một lớp học
    @Override
    public void deleteUntilEndDate(Schedule schedule, List<Schedule> existingSchedules) throws Exception {
        LocalDate currentDate = schedule.getStudyDate();
        LocalDate endDate = schedule.getClas().getEndDate();
        while (currentDate.isBefore(endDate)) {
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

    // Chuyển đổi danh sách lịch học sang danh sách ScheduleView để hiển thị thông
    // tin
    @Override
    public List<ScheduleView> toScheduleView(List<Schedule> schedules) {
        // Xử lý bằng Stream để ánh xạ (map)
        return schedules.stream()
                // Chuyển toàn bộ Model Schedule thành DTO ScheduleView cho việc hiển thị ở
                // Client
                .map(s -> new ScheduleView(
                        s.getScheduleId(),
                        s.getClas().getClassName(),
                        s.getStudyDate(),
                        s.getStartTime(),
                        s.getEndTime(),
                        s.getRoom().getBranch().getBranchName(),
                        s.getRoom().getRoomName(),
                        s.getCreatedAt()))
                // Đẩy thông tin thu nhận về lại format danh sách
                .toList();
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

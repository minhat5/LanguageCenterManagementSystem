package vn.edu.ute.ui.student;

import vn.edu.ute.common.session.SessionManager;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.model.Schedule;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Result;
import vn.edu.ute.model.Clas;
import vn.edu.ute.model.Student;
import vn.edu.ute.service.EnrollmentService;
import vn.edu.ute.service.ScheduleService;
import vn.edu.ute.service.AttendanceService;
import vn.edu.ute.service.CertificationService;
import vn.edu.ute.service.StudentPaymentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class StudentPortalPanel extends JPanel {

    private final EnrollmentService enrollmentService;
    private final ScheduleService scheduleService;
    private final AttendanceService attendanceService;
    private final CertificationService certificationService;
    private final StudentPaymentService studentPaymentService;
    // Models
    private DefaultTableModel enrollmentTableModel;
    private DefaultTableModel scheduleTableModel;
    private DefaultTableModel attendanceTableModel;
    private DefaultTableModel resultTableModel;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public StudentPortalPanel(EnrollmentService enrollmentService, ScheduleService scheduleService,
                              AttendanceService attendanceService, CertificationService certificationService,
                              StudentPaymentService studentPaymentService) {
        this.enrollmentService = enrollmentService;
        this.scheduleService = scheduleService;
        this.attendanceService = attendanceService;
        this.certificationService = certificationService;
        this.studentPaymentService = studentPaymentService;

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Khóa học của tôi", createEnrollmentPanel());
        tabbedPane.addTab("Lịch học", createSchedulePanel());
        tabbedPane.addTab("Điểm danh", createAttendancePanel());
        tabbedPane.addTab("Kết quả học tập", createResultPanel());

        StudentPaymentPanel paymentPanel = new StudentPaymentPanel(studentPaymentService);
        tabbedPane.addTab("Thanh Toán Học Phí", paymentPanel);

        add(tabbedPane, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Làm mới dữ liệu");
        btnRefresh.addActionListener(e -> {
            loadAllData();
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);

        loadAllData();
    }

    private JPanel createEnrollmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        enrollmentTableModel = new DefaultTableModel(new Object[]{"ID Ghi danh", "Lớp", "Khóa học", "Giáo viên", "Trạng thái ghi danh"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tbl = new JTable(enrollmentTableModel);
        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scheduleTableModel = new DefaultTableModel(new Object[]{"Ngày học", "Thời gian", "Lớp", "Phòng học", "Khóa học"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tbl = new JTable(scheduleTableModel);
        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        attendanceTableModel = new DefaultTableModel(new Object[]{"ID", "Ngày học", "Lớp", "Trạng thái Code", "Ghi chú"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tbl = new JTable(attendanceTableModel);
        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        resultTableModel = new DefaultTableModel(new Object[]{"ID Kết quả", "Lớp", "Khóa học", "Điểm số", "Xếp loại", "Trạng thái Chứng chỉ"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tbl = new JTable(resultTableModel);
        panel.add(new JScrollPane(tbl), BorderLayout.CENTER);

        return panel;
    }

    private void loadAllData() {
        UserAccount currentUser = SessionManager.getCurrentUser();
        // Return blank if not a student
        if (currentUser == null || currentUser.getStudent() == null) return;

        Long studentId = currentUser.getStudent().getStudentId();

        try {
            // Load Enrollments
            List<Enrollment> myEnrollments = enrollmentService.getAllEnrollments().stream()
                    .filter(e -> e.getStudent().getStudentId().equals(studentId))
                    .collect(Collectors.toList());

            enrollmentTableModel.setRowCount(0);
            for (Enrollment e : myEnrollments) {
                Clas c = e.getClas();
                String teacherName = c.getTeacher() != null ? c.getTeacher().getFullName() : "Cần sắp xếp";
                enrollmentTableModel.addRow(new Object[]{
                        e.getEnrollmentId(), c.getClassName(), c.getCourse().getCourseName(), teacherName, e.getStatus().name()
                });
            }

            // Load Schedules
            // My schedules are the ones bound to classes I am enrolled in
            List<Long> myClassIds = myEnrollments.stream().map(e -> e.getClas().getClassId()).collect(Collectors.toList());
            List<Schedule> mySchedules = scheduleService.getAll().stream()
                    .filter(s -> myClassIds.contains(s.getClas().getClassId()))
                    .sorted((s1, s2) -> s1.getStudyDate().compareTo(s2.getStudyDate()))
                    .collect(Collectors.toList());

            scheduleTableModel.setRowCount(0);
            for (Schedule s : mySchedules) {
                String room = s.getRoom() != null ? s.getRoom().getRoomName() : "N/A";
                scheduleTableModel.addRow(new Object[]{
                        s.getStudyDate().format(dateFormatter),
                        s.getStartTime().format(timeFormatter) + " - " + s.getEndTime().format(timeFormatter),
                        s.getClas().getClassName(), room, s.getClas().getCourse().getCourseName()
                });
            }

            // Load Attendance
            List<Attendance> myAttendances = attendanceService.getAll().stream()
                    .filter(a -> a.getStudent().getStudentId().equals(studentId))
                    .sorted((a1, a2) -> a2.getAttendDate().compareTo(a1.getAttendDate()))
                    .collect(Collectors.toList());

            attendanceTableModel.setRowCount(0);
            for (Attendance a : myAttendances) {
                attendanceTableModel.addRow(new Object[]{
                        a.getAttendanceId(),
                        a.getAttendDate().format(dateFormatter),
                        a.getClas().getClassName(),
                        a.getStatus().name(),
                        a.getNote()
                });
            }

            // Load Results
            List<Result> myResults = certificationService.getAllResults().stream()
                    .filter(r -> r.getStudent().getStudentId().equals(studentId))
                    .collect(Collectors.toList());

            resultTableModel.setRowCount(0);
            for (Result r : myResults) {
                // Check if certificate exists for this student and class
                boolean hasCert = certificationService.getAllCertificates().stream()
                        .anyMatch(cert -> cert.getStudent().getStudentId().equals(studentId)
                                && cert.getClas() != null
                                && cert.getClas().getClassId().equals(r.getClas().getClassId()));

                resultTableModel.addRow(new Object[]{
                        r.getResultId(),
                        r.getClas().getClassName(),
                        r.getClas().getCourse().getCourseName(),
                        r.getScore(),
                        r.getGrade(),
                        hasCert ? "Đã cấp chứng chỉ" : "Chưa cấp"
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu cho tài khoản của bạn: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
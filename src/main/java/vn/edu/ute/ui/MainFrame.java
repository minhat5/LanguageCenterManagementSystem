package vn.edu.ute.ui;

import vn.edu.ute.service.*;
import vn.edu.ute.ui.attendance.AttendancePanel;
import vn.edu.ute.ui.clas.ClasPanel;
import vn.edu.ute.ui.course.CoursePanel;
import vn.edu.ute.ui.schedule.SchedulePanel;
import vn.edu.ute.ui.admin.StaffManagementPanel;
import vn.edu.ute.ui.admin.FacilityManagementPanel;
import vn.edu.ute.ui.admin.ProfileManagementPanel;
import vn.edu.ute.ui.notification.NotificationPanel;
import vn.edu.ute.ui.enrollment.EnrollmentPanel;
import vn.edu.ute.ui.plamentTest.PlacementTestPanel;
import vn.edu.ute.ui.promotion.PromotionPanel;
import vn.edu.ute.ui.certification.CertificationPanel;
import vn.edu.ute.ui.report.ReportPanel;
import vn.edu.ute.ui.student.StudentPortalPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Panel từ nhánh Course & Admin-HR
    private CoursePanel coursePanel;
    private ClasPanel clasPanel;
    private SchedulePanel schedulePanel;
    private AttendancePanel attendancePanel;
    private StaffManagementPanel staffPanel;
    private FacilityManagementPanel facilityPanel;
    private ProfileManagementPanel profilePanel;
    private NotificationPanel notificationPanel;

    // Panel từ nhánh Report
    private PlacementTestPanel testPanel;
    private EnrollmentPanel enrollmentPanel;
    private PromotionPanel promotionPanel;
    private CertificationPanel certificationPanel;
    private ReportPanel reportPanel;
    
    // Panel cho Học Viên
    private StudentPortalPanel studentPortalPanel;

    public MainFrame() {
        // Sử dụng ServiceFactory để lấy tất cả các service (Tránh truyền tham số quá dài)
        vn.edu.ute.common.factory.ServiceFactory factory = vn.edu.ute.common.factory.ServiceFactory.getInstance();
        
        CourseService courseService = factory.getCourseService();
        ClasService classService = factory.getClassService();
        TeacherService teacherService = factory.getTeacherService();
        BranchService branchService = factory.getBranchService();
        RoomService roomService = factory.getRoomService();
        ScheduleService scheduleService = factory.getScheduleService();
        AttendanceService attendanceService = factory.getAttendanceService();
        StaffService staffService = factory.getStaffService();
        StudentService studentService = factory.getStudentService();
        NotificationService notificationService = factory.getNotificationService();
        
        // Các service cho nhánh Report (Đảm bảo ServiceFactory đã có các hàm này)
        EnrollmentService enrollmentService = factory.getEnrollmentService();
        PromotionService promotionService = factory.getPromotionService();
        CertificationService certificationService = factory.getCertificationService();
        ReportService reportService = factory.getReportService();

        setTitle("Hệ thống Quản lý Trung tâm Ngoại ngữ Toàn diện");
        setSize(1600, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // --- KHỞI TẠO CÁC PANEL ---
        
        // Nhóm Course & HR
        clasPanel = new ClasPanel(classService, courseService, teacherService, branchService, roomService);
        coursePanel = new CoursePanel(courseService, selectedCourse -> {
            cardLayout.show(mainContentPanel, "CLASS");
            clasPanel.showAddDialog(selectedCourse);
        });
        schedulePanel = new SchedulePanel(scheduleService, classService, roomService);
        attendancePanel = new AttendancePanel(classService, attendanceService);
        staffPanel = new StaffManagementPanel(staffService);
        facilityPanel = new FacilityManagementPanel(branchService, roomService);
        profilePanel = new ProfileManagementPanel(studentService, teacherService);
        notificationPanel = new NotificationPanel(notificationService);

        // Nhóm Nghiệp vụ & Báo cáo (Report)
        testPanel = new PlacementTestPanel(enrollmentService);
        enrollmentPanel = new EnrollmentPanel(enrollmentService);
        promotionPanel = new PromotionPanel(promotionService);
        certificationPanel = new CertificationPanel(certificationService, classService, studentService);
        reportPanel = new ReportPanel(reportService);
        studentPortalPanel = new StudentPortalPanel(enrollmentService, scheduleService, attendanceService, certificationService);

        // --- THÊM VÀO CARDLAYOUT ---
        mainContentPanel.add(coursePanel, "COURSE");
        mainContentPanel.add(clasPanel, "CLASS");
        mainContentPanel.add(schedulePanel, "SCHEDULE");
        mainContentPanel.add(attendancePanel, "ATTENDANCE");
        mainContentPanel.add(staffPanel, "STAFF");
        mainContentPanel.add(facilityPanel, "FACILITY");
        mainContentPanel.add(profilePanel, "PROFILE");
        mainContentPanel.add(notificationPanel, "NOTIFICATION");
        mainContentPanel.add(testPanel, "TEST");
        mainContentPanel.add(enrollmentPanel, "ENROLLMENT");
        mainContentPanel.add(promotionPanel, "PROMOTION");
        mainContentPanel.add(certificationPanel, "CERTIFICATION");
        mainContentPanel.add(reportPanel, "REPORT");
        mainContentPanel.add(studentPortalPanel, "STUDENT_PORTAL");

        // --- SIDE MENU ---
        JPanel sideMenu = new JPanel(new GridLayout(0, 1, 5, 5));
        sideMenu.setPreferredSize(new Dimension(230, 0));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Nút bấm với icon (Emoji)
        JButton btnStudentPortal = new JButton("🎒 Góc Học viên");
        JButton btnCourse = new JButton("📚 Khóa học");
        JButton btnClass = new JButton("🏫 Lớp học");
        JButton btnSchedule = new JButton("📅 Lịch học");
        JButton btnAttendance = new JButton("✅ Điểm danh");
        JButton btnTest = new JButton("📝 Đánh giá NL");
        JButton btnEnrollment = new JButton("✍️ Ghi danh");
        JButton btnCertification = new JButton("🎓 Điểm & CC");
        JButton btnNotification = new JButton("🔔 Thông báo");
        JButton btnStaff = new JButton("👥 Nhân viên");
        JButton btnFacility = new JButton("🏗️ Cơ sở vật chất");
        JButton btnProfile = new JButton("👤 Hồ sơ");
        JButton btnPromotion = new JButton("🎁 Khuyến mãi");
        JButton btnReport = new JButton("📊 Báo cáo");

        // Action Listeners
        btnStudentPortal.addActionListener(e -> cardLayout.show(mainContentPanel, "STUDENT_PORTAL"));
        btnCourse.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE"));
        btnClass.addActionListener(e -> cardLayout.show(mainContentPanel, "CLASS"));
        btnSchedule.addActionListener(e -> cardLayout.show(mainContentPanel, "SCHEDULE"));
        btnAttendance.addActionListener(e -> cardLayout.show(mainContentPanel, "ATTENDANCE"));
        btnTest.addActionListener(e -> cardLayout.show(mainContentPanel, "TEST"));
        btnEnrollment.addActionListener(e -> cardLayout.show(mainContentPanel, "ENROLLMENT"));
        btnCertification.addActionListener(e -> cardLayout.show(mainContentPanel, "CERTIFICATION"));
        btnNotification.addActionListener(e -> cardLayout.show(mainContentPanel, "NOTIFICATION"));
        btnStaff.addActionListener(e -> cardLayout.show(mainContentPanel, "STAFF"));
        btnFacility.addActionListener(e -> cardLayout.show(mainContentPanel, "FACILITY"));
        btnProfile.addActionListener(e -> cardLayout.show(mainContentPanel, "PROFILE"));
        btnPromotion.addActionListener(e -> cardLayout.show(mainContentPanel, "PROMOTION"));
        btnReport.addActionListener(e -> cardLayout.show(mainContentPanel, "REPORT"));

        // Áp dụng Phân quyền để ẩn hiện Menu
        btnStudentPortal.setVisible(vn.edu.ute.common.policy.RolePolicy.canAccessStudentPortal());
        btnCourse.setVisible(vn.edu.ute.common.policy.RolePolicy.canEditCourseAndClass());
        btnClass.setVisible(vn.edu.ute.common.policy.RolePolicy.canAccessCourseAndClass());
        btnSchedule.setVisible(vn.edu.ute.common.policy.RolePolicy.canAccessSchedule());
        btnAttendance.setVisible(vn.edu.ute.common.policy.RolePolicy.canAccessAttendance());
        btnTest.setVisible(vn.edu.ute.common.policy.RolePolicy.canAccessEnrollment());
        btnEnrollment.setVisible(vn.edu.ute.common.policy.RolePolicy.canAccessEnrollment());
        btnCertification.setVisible(vn.edu.ute.common.policy.RolePolicy.canAccessCertification());
        btnNotification.setVisible(vn.edu.ute.common.policy.RolePolicy.canAccessNotification());
        btnStaff.setVisible(vn.edu.ute.common.policy.RolePolicy.canManageStaff());
        btnFacility.setVisible(vn.edu.ute.common.policy.RolePolicy.canManageSystem());
        btnProfile.setVisible(vn.edu.ute.common.policy.RolePolicy.canManageProfile());
        btnPromotion.setVisible(vn.edu.ute.common.policy.RolePolicy.canAccessPromotion());
        btnReport.setVisible(vn.edu.ute.common.policy.RolePolicy.canViewReport());

        // Add to Menu
        sideMenu.add(btnStudentPortal);
        sideMenu.add(btnCourse);
        sideMenu.add(btnClass);
        sideMenu.add(btnSchedule);
        sideMenu.add(btnAttendance);
        sideMenu.add(btnTest);
        sideMenu.add(btnEnrollment);
        sideMenu.add(btnCertification);
        sideMenu.add(new JSeparator());
        sideMenu.add(btnStaff);
        sideMenu.add(btnFacility);
        sideMenu.add(btnProfile);
        sideMenu.add(btnNotification);
        sideMenu.add(new JSeparator());
        sideMenu.add(btnPromotion);
        sideMenu.add(btnReport);

        setLayout(new BorderLayout());
        add(sideMenu, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
        
        // --- CHỌN MÀN HÌNH MẶC ĐỊNH DỰA THEO QUYỀN ---
        if (vn.edu.ute.common.policy.RolePolicy.canAccessStudentPortal()) {
            cardLayout.show(mainContentPanel, "STUDENT_PORTAL");
        } else if (vn.edu.ute.common.policy.RolePolicy.canEditCourseAndClass()) {
            cardLayout.show(mainContentPanel, "COURSE");
        } else if (vn.edu.ute.common.policy.RolePolicy.canAccessSchedule()) {
            cardLayout.show(mainContentPanel, "SCHEDULE");
        } else if (vn.edu.ute.common.policy.RolePolicy.canViewReport()){
            cardLayout.show(mainContentPanel, "REPORT");
        } else {
            cardLayout.show(mainContentPanel, "PROFILE");
        }
    }
}
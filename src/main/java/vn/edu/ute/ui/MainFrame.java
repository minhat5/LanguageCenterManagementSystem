package vn.edu.ute.ui;

import vn.edu.ute.service.*;
import vn.edu.ute.ui.attendance.AttendancePanel;
import vn.edu.ute.ui.clas.ClasPanel;
import vn.edu.ute.ui.course.CoursePanel;
import vn.edu.ute.ui.schedule.SchedulePanel;
// Import các panel từ nhánh admin-hr (đảm bảo đúng package của bạn)
import vn.edu.ute.ui.admin.StaffManagementPanel;
import vn.edu.ute.ui.admin.FacilityManagementPanel;
import vn.edu.ute.ui.admin.ProfileManagementPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Các panel từ nhánh feature/course
    private CoursePanel coursePanel;
    private ClasPanel clasPanel;
    private SchedulePanel schedulePanel;
    private AttendancePanel attendancePanel;

    // Các panel từ nhánh feature/admin-hr-module
    private StaffManagementPanel staffPanel;
    private FacilityManagementPanel facilityPanel;
    private ProfileManagementPanel profilePanel;

    public MainFrame(
            CourseService courseService, 
            ClasService classService, 
            TeacherService teacherService, 
            BranchService branchService, 
            RoomService roomService, 
            ScheduleService scheduleService, 
            AttendanceService attendanceService,
            StaffService staffService,
            StudentService studentService) {

        super("Hệ thống Quản lý Trung tâm Đào tạo Toàn diện");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 850);
        setLocationRelativeTo(null);

        // 1. Thiết lập Layout chính
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // 2. Khởi tạo các Panel từ nhánh feature/course
        clasPanel = new ClasPanel(classService, courseService, teacherService, branchService, roomService);
        coursePanel = new CoursePanel(courseService, selectedCourse -> {
            cardLayout.show(mainContentPanel, "CLASS");
            clasPanel.showAddDialog(selectedCourse);
        });
        schedulePanel = new SchedulePanel(scheduleService, classService, roomService);
        attendancePanel = new AttendancePanel(classService, attendanceService);

        // 3. Khởi tạo các Panel từ nhánh feature/admin-hr-module
        staffPanel = new StaffManagementPanel(staffService);
        facilityPanel = new FacilityManagementPanel(branchService, roomService);
        profilePanel = new ProfileManagementPanel(studentService, teacherService);

        // 4. Thêm tất cả vào CardLayout
        mainContentPanel.add(coursePanel, "COURSE");
        mainContentPanel.add(clasPanel, "CLASS");
        mainContentPanel.add(schedulePanel, "SCHEDULE");
        mainContentPanel.add(attendancePanel, "ATTENDANCE");
        mainContentPanel.add(staffPanel, "STAFF");
        mainContentPanel.add(facilityPanel, "FACILITY");
        mainContentPanel.add(profilePanel, "PROFILE");

        // 5. Tạo Menu bên trái (Side Menu)
        JPanel sideMenu = new JPanel(new GridLayout(12, 1, 5, 5));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideMenu.setPreferredSize(new Dimension(220, 0));

        // Các nút bấm
        JButton btnCourse = new JButton("📚 Quản lý Khoá học");
        JButton btnClas = new JButton("🏫 Quản lý Lớp học");
        JButton btnSchedule = new JButton("📅 Quản lý Lịch học");
        JButton btnAttendance = new JButton("✅ Điểm danh");
        JButton btnStaff = new JButton("👥 Quản lý Nhân viên");
        JButton btnFacility = new JButton("🏗️ Cơ sở vật chất");
        JButton btnProfile = new JButton("👤 Hồ sơ Giáo viên/HV");

        // Sự kiện chuyển trang
        btnCourse.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE"));
        btnClas.addActionListener(e -> cardLayout.show(mainContentPanel, "CLASS"));
        btnSchedule.addActionListener(e -> cardLayout.show(mainContentPanel, "SCHEDULE"));
        btnAttendance.addActionListener(e -> cardLayout.show(mainContentPanel, "ATTENDANCE"));
        btnStaff.addActionListener(e -> cardLayout.show(mainContentPanel, "STAFF"));
        btnFacility.addActionListener(e -> cardLayout.show(mainContentPanel, "FACILITY"));
        btnProfile.addActionListener(e -> cardLayout.show(mainContentPanel, "PROFILE"));

        sideMenu.add(btnCourse);
        sideMenu.add(btnClas);
        sideMenu.add(btnSchedule);
        sideMenu.add(btnAttendance);
        sideMenu.add(new JSeparator()); // Ngăn cách các nhóm tính năng
        sideMenu.add(btnStaff);
        sideMenu.add(btnFacility);
        sideMenu.add(btnProfile);

        // 6. Lắp ghép vào JFrame
        setLayout(new BorderLayout());
        add(sideMenu, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }
}
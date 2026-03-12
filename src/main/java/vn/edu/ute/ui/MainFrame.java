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

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private CoursePanel coursePanel;
    private ClasPanel clasPanel;
    private SchedulePanel schedulePanel;
    private AttendancePanel attendancePanel;

    private StaffManagementPanel staffPanel;
    private FacilityManagementPanel facilityPanel;
    private ProfileManagementPanel profilePanel;
    private NotificationPanel notificationPanel;

    public MainFrame() {
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

        setTitle("Hệ thống Quản lý Trung tâm");
        setSize(1600, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

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

        mainContentPanel.add(coursePanel, "COURSE");
        mainContentPanel.add(clasPanel, "CLASS");
        mainContentPanel.add(schedulePanel, "SCHEDULE");
        mainContentPanel.add(attendancePanel, "ATTENDANCE");
        mainContentPanel.add(staffPanel, "STAFF");
        mainContentPanel.add(facilityPanel, "FACILITY");
        mainContentPanel.add(profilePanel, "PROFILE");
        mainContentPanel.add(notificationPanel, "NOTIFICATION");

        JPanel sideMenu = new JPanel(new GridLayout(13, 1, 5, 5));
        sideMenu.setPreferredSize(new Dimension(220, 0));

        JButton btnCourse = new JButton("📚 Quản lý Khoá học");
        JButton btnClass = new JButton("🏫 Quản lý Lớp học");
        JButton btnSchedule = new JButton("📅 Quản lý Lịch học");
        JButton btnAttendance = new JButton("✅ Điểm danh");
        JButton btnNotification = new JButton("🔔 Thông báo");
        JButton btnStaff = new JButton("👥 Quản lý Nhân viên");
        JButton btnFacility = new JButton("🏗️ Cơ sở vật chất");
        JButton btnProfile = new JButton("👤 Hồ sơ");

        btnCourse.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE"));
        btnClass.addActionListener(e -> cardLayout.show(mainContentPanel, "CLASS"));
        btnSchedule.addActionListener(e -> cardLayout.show(mainContentPanel, "SCHEDULE"));
        btnAttendance.addActionListener(e -> cardLayout.show(mainContentPanel, "ATTENDANCE"));
        btnNotification.addActionListener(e -> cardLayout.show(mainContentPanel, "NOTIFICATION"));
        btnStaff.addActionListener(e -> cardLayout.show(mainContentPanel, "STAFF"));
        btnFacility.addActionListener(e -> cardLayout.show(mainContentPanel, "FACILITY"));
        btnProfile.addActionListener(e -> cardLayout.show(mainContentPanel, "PROFILE"));

        sideMenu.add(btnCourse);
        sideMenu.add(btnClass);
        sideMenu.add(btnSchedule);
        sideMenu.add(btnAttendance);
        sideMenu.add(btnNotification);
        sideMenu.add(new JSeparator());
        sideMenu.add(btnStaff);
        sideMenu.add(btnFacility);
        sideMenu.add(btnProfile);

        setLayout(new BorderLayout());
        add(sideMenu, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }
}
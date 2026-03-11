package vn.edu.ute.ui;

import javax.swing.*;
import java.awt.*;
import vn.edu.ute.service.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    public MainFrame(StaffService staffService, BranchService branchService,
                     RoomService roomService, StudentService studentService,
                     TeacherService teacherService) {

        super("Hệ thống Quản lý Trung tâm");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);

        // 1. Thiết lập CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // 2. Khởi tạo các Panel của bạn (đã sửa ở Bước 1)
        StaffManagementPanel staffPanel = new StaffManagementPanel(staffService);
        FacilityManagementPanel facilityPanel = new FacilityManagementPanel(branchService, roomService);
        ProfileManagementPanel profilePanel = new ProfileManagementPanel(studentService, teacherService);

        // 3. Thêm vào vùng quản lý
        mainContentPanel.add(staffPanel, "STAFF");
        mainContentPanel.add(facilityPanel, "FACILITY");
        mainContentPanel.add(profilePanel, "PROFILE");

        // 4. Tạo Menu bên trái (Side Menu)
        JPanel sideMenu = new JPanel(new GridLayout(10, 1, 5, 5));
        sideMenu.setPreferredSize(new Dimension(200, 0));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnStaff = new JButton("Quản lý Nhân viên");
        JButton btnFacility = new JButton("Cơ sở vật chất");
        JButton btnProfile = new JButton("Hồ sơ Giáo viên/HV");

        // Sự kiện chuyển trang
        btnStaff.addActionListener(e -> cardLayout.show(mainContentPanel, "STAFF"));
        btnFacility.addActionListener(e -> cardLayout.show(mainContentPanel, "FACILITY"));
        btnProfile.addActionListener(e -> cardLayout.show(mainContentPanel, "PROFILE"));

        sideMenu.add(btnStaff);
        sideMenu.add(btnFacility);
        sideMenu.add(btnProfile);

        // 5. Lắp ghép vào JFrame
        setLayout(new BorderLayout());
        add(sideMenu, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }
}
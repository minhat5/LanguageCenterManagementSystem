package vn.edu.ute.ui;

import vn.edu.ute.service.*;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private CoursePanel coursePanel;
    private ClasPanel clasPanel;

    public MainFrame(CourseService courseService, ClasService clasService, TeacherService teacherService, BranchService branchService, RoomService roomService) {
        super("Hệ thống quản lý trung tâm đào tạo ngoại ngữ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 800);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        clasPanel = new ClasPanel(clasService, courseService, teacherService, branchService, roomService);

        coursePanel = new CoursePanel(courseService, selectedCourse -> {
            cardLayout.show(mainContentPanel, "CLASS");
            clasPanel.showAddDialog(selectedCourse);
        });

        mainContentPanel.add(coursePanel, "COURSE");
        mainContentPanel.add(clasPanel, "CLASS");

        JPanel sideMenu = new JPanel(new GridLayout(10, 1, 5, 5));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideMenu.setPreferredSize(new Dimension(150, 0));

        JButton btnCourse = new JButton("Quản lý Khoá học");
        JButton btnClas = new JButton("Quản lý Lớp học");

        btnCourse.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE"));
        btnClas.addActionListener(e -> cardLayout.show(mainContentPanel, "CLASS"));

        sideMenu.add(btnCourse);
        sideMenu.add(btnClas);

        setLayout(new BorderLayout());
        add(sideMenu, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }
}
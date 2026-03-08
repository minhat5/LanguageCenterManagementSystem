package vn.edu.ute.ui;

import vn.edu.ute.service.*;
import vn.edu.ute.ui.clas.ClasPanel;
import vn.edu.ute.ui.course.CoursePanel;
import vn.edu.ute.ui.schedule.SchedulePanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    private CoursePanel coursePanel;
    private ClasPanel clasPanel;
    private SchedulePanel schedulePanel;

    public MainFrame(CourseService courseService, ClasService clasService, TeacherService teacherService, BranchService branchService, RoomService roomService, ScheduleService scheduleService) {
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

        schedulePanel = new SchedulePanel(scheduleService, clasService, roomService);

        mainContentPanel.add(coursePanel, "COURSE");
        mainContentPanel.add(clasPanel, "CLASS");
        mainContentPanel.add(schedulePanel, "SCHEDULE");

        JPanel sideMenu = new JPanel(new GridLayout(10, 1, 5, 5));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideMenu.setPreferredSize(new Dimension(150, 0));

        JButton btnCourse = new JButton("Quản lý Khoá học");
        JButton btnClas = new JButton("Quản lý Lớp học");
        JButton btnSchedule = new JButton("Quản lý Lịch học");

        btnCourse.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE"));
        btnClas.addActionListener(e -> cardLayout.show(mainContentPanel, "CLASS"));
        btnSchedule.addActionListener(e -> cardLayout.show(mainContentPanel, "SCHEDULE"));

        sideMenu.add(btnCourse);
        sideMenu.add(btnClas);
        sideMenu.add(btnSchedule);

        setLayout(new BorderLayout());
        add(sideMenu, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }
}
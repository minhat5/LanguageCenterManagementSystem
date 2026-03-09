package vn.edu.ute.ui;

import vn.edu.ute.service.*;

import vn.edu.ute.ui.enrollment.EnrollmentPanel;
import vn.edu.ute.ui.plamentTest.PlacementTestPanel;
import vn.edu.ute.ui.promotion.PromotionPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;


    // Khai báo thêm 3 panel mới
    private PlacementTestPanel testPanel;
    private EnrollmentPanel enrollmentPanel;
    private PromotionPanel promotionPanel;

    // THÊM: EnrollmentService và PromotionService vào tham số
    public MainFrame(EnrollmentService enrollmentService, PromotionService promotionService) {

        super("Hệ thống quản lý trung tâm đào tạo ngoại ngữ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 800);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);


        // Khởi tạo 3 panel mới của Tín
        testPanel = new PlacementTestPanel(enrollmentService);
        enrollmentPanel = new EnrollmentPanel(enrollmentService);
        promotionPanel = new PromotionPanel(promotionService);


        // Thêm 3 panel mới vào CardLayout
        mainContentPanel.add(testPanel, "TEST");
        mainContentPanel.add(enrollmentPanel, "ENROLLMENT");
        mainContentPanel.add(promotionPanel, "PROMOTION");

        JPanel sideMenu = new JPanel(new GridLayout(10, 1, 5, 5));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideMenu.setPreferredSize(new Dimension(180, 0)); // Tăng nhẹ chiều ngang menu cho chữ khỏi bị che

        JButton btnCourse = new JButton("Quản lý Khoá học");
        JButton btnClas = new JButton("Quản lý Lớp học");
        JButton btnSchedule = new JButton("Quản lý Lịch học");

        // Thêm 3 nút mới
        JButton btnTest = new JButton("Đánh giá Năng lực");
        JButton btnEnrollment = new JButton("Quản lý Ghi danh");
        JButton btnPromotion = new JButton("Quản lý Khuyến mãi");

        btnCourse.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE"));
        btnClas.addActionListener(e -> cardLayout.show(mainContentPanel, "CLASS"));
        btnSchedule.addActionListener(e -> cardLayout.show(mainContentPanel, "SCHEDULE"));

        // Bắt sự kiện chuyển trang cho 3 nút mới
        btnTest.addActionListener(e -> cardLayout.show(mainContentPanel, "TEST"));
        btnEnrollment.addActionListener(e -> cardLayout.show(mainContentPanel, "ENROLLMENT"));
        btnPromotion.addActionListener(e -> cardLayout.show(mainContentPanel, "PROMOTION"));

        sideMenu.add(btnCourse);
        sideMenu.add(btnClas);
        sideMenu.add(btnSchedule);

        // Thêm 3 nút vào menu
        sideMenu.add(btnTest);
        sideMenu.add(btnEnrollment);
        sideMenu.add(btnPromotion);

        setLayout(new BorderLayout());
        add(sideMenu, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }
}
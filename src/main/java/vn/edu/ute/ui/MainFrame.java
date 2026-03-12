package vn.edu.ute.ui;

import vn.edu.ute.service.*;

import vn.edu.ute.ui.enrollment.EnrollmentPanel;
import vn.edu.ute.ui.plamentTest.PlacementTestPanel;
import vn.edu.ute.ui.promotion.PromotionPanel;
// IMPORT THÊM PANEL CHỨNG CHỈ
import vn.edu.ute.ui.certification.CertificationPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;

    // Khai báo thêm các panel
    private PlacementTestPanel testPanel;
    private EnrollmentPanel enrollmentPanel;
    private PromotionPanel promotionPanel;
    private CertificationPanel certificationPanel; // THÊM DÒNG NÀY

    // THÊM: CertificationService, ClasService, StudentService vào tham số
    public MainFrame(EnrollmentService enrollmentService,
                     PromotionService promotionService,
                     CertificationService certificationService,
                     ClasService clasService,
                     StudentService studentService) {

        super("Hệ thống quản lý trung tâm đào tạo ngoại ngữ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 800);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // Khởi tạo các panel
        testPanel = new PlacementTestPanel(enrollmentService);
        enrollmentPanel = new EnrollmentPanel(enrollmentService);
        promotionPanel = new PromotionPanel(promotionService);

        // KHỞI TẠO PANEL CHỨNG CHỈ
        certificationPanel = new CertificationPanel(certificationService, clasService, studentService);

        // Thêm các panel vào CardLayout
        mainContentPanel.add(testPanel, "TEST");
        mainContentPanel.add(enrollmentPanel, "ENROLLMENT");
        mainContentPanel.add(promotionPanel, "PROMOTION");
        mainContentPanel.add(certificationPanel, "CERTIFICATION"); // THÊM DÒNG NÀY

        // Thiết lập Menu bên trái
        JPanel sideMenu = new JPanel(new GridLayout(10, 1, 5, 5));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sideMenu.setPreferredSize(new Dimension(180, 0));

        // Khởi tạo các nút
        JButton btnCourse = new JButton("Quản lý Khoá học");
        JButton btnClas = new JButton("Quản lý Lớp học");
        JButton btnSchedule = new JButton("Quản lý Lịch học");
        JButton btnTest = new JButton("Đánh giá Năng lực");
        JButton btnEnrollment = new JButton("Quản lý Ghi danh");
        JButton btnPromotion = new JButton("Quản lý Khuyến mãi");
        JButton btnCertification = new JButton("Điểm & Chứng chỉ"); // THÊM NÚT MỚI

        // Bắt sự kiện chuyển trang
        btnCourse.addActionListener(e -> cardLayout.show(mainContentPanel, "COURSE"));
        btnClas.addActionListener(e -> cardLayout.show(mainContentPanel, "CLASS"));
        btnSchedule.addActionListener(e -> cardLayout.show(mainContentPanel, "SCHEDULE"));
        btnTest.addActionListener(e -> cardLayout.show(mainContentPanel, "TEST"));
        btnEnrollment.addActionListener(e -> cardLayout.show(mainContentPanel, "ENROLLMENT"));
        btnPromotion.addActionListener(e -> cardLayout.show(mainContentPanel, "PROMOTION"));

        // SỰ KIỆN CHO NÚT CHỨNG CHỈ
        btnCertification.addActionListener(e -> cardLayout.show(mainContentPanel, "CERTIFICATION"));

        // Thêm các nút vào menu
        sideMenu.add(btnCourse);
        sideMenu.add(btnClas);
        sideMenu.add(btnSchedule);
        sideMenu.add(btnTest);
        sideMenu.add(btnEnrollment);
        sideMenu.add(btnPromotion);
        sideMenu.add(btnCertification); // THÊM NÚT VÀO MENU

        setLayout(new BorderLayout());
        add(sideMenu, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);
    }
}
package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.impl.*;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;
import vn.edu.ute.service.impl.PromotionServiceImpl;
import vn.edu.ute.ui.EnrollmentPanel;
import vn.edu.ute.ui.PlacementTestPanel;
import vn.edu.ute.ui.promotion.PromotionPanel;

import javax.swing.*;

public class    App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Khởi tạo Database Transaction Manager
                TransactionManager txManager = new TransactionManager();

                // 2. Khởi tạo Repositories (Lúc này Repo không cần truyền EntityManager vào nữa)
                var testRepo = new PlacementTestRepoImpl();
                var enrollmentRepo = new EnrollmentRepoImpl();
                var clasRepo = new ClasRepoImpl();
                var studentRepo = new StudentRepoImpl();
                var promotionRepo = new PromotionRepoImpl();

                // 3. Khởi tạo Service (Bơm TxManager và Repositories vào)
                var enrollmentService = new EnrollmentServiceImpl(
                        txManager, testRepo, enrollmentRepo, clasRepo, studentRepo
                );
                var promotionService  = new PromotionServiceImpl(txManager, promotionRepo);

                // 4. Khởi tạo UI và nhúng Panel
                PlacementTestPanel testPanel = new PlacementTestPanel(enrollmentService);
                EnrollmentPanel enrollPanel = new EnrollmentPanel(enrollmentService);
                PromotionPanel promotionPanel = new PromotionPanel(promotionService);

                JFrame frame = new JFrame("Test Module Tuyển Sinh - Mai Hồng Tín");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(900, 600);
                frame.setLocationRelativeTo(null);

// Dùng JTabbedPane để chứa 2 màn hình
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.addTab("1. Quản Lý Đánh Giá Năng Lực", testPanel);
                tabbedPane.addTab("2. Quản Lý Ghi Danh", enrollPanel);
                tabbedPane.addTab("2. Quản Lý  Mã Giảm Giá", promotionPanel);


                frame.add(tabbedPane);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khởi động: " + e.getMessage());
            }
        });
    }
}

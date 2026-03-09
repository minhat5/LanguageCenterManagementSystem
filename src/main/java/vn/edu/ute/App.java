package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.impl.ClasRepoImpl;
import vn.edu.ute.repo.impl.EnrollmentRepoImpl;
import vn.edu.ute.repo.impl.PlacementTestRepoImpl;
import vn.edu.ute.repo.impl.StudentRepoImpl;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;
import vn.edu.ute.ui.EnrollmentPanel;
import vn.edu.ute.ui.PlacementTestPanel;

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

                // 3. Khởi tạo Service (Bơm TxManager và Repositories vào)
                var enrollmentService = new EnrollmentServiceImpl(
                        txManager, testRepo, enrollmentRepo, clasRepo, studentRepo
                );

                // 4. Khởi tạo UI và nhúng Panel
                PlacementTestPanel testPanel = new PlacementTestPanel(enrollmentService);
                EnrollmentPanel enrollPanel = new EnrollmentPanel(enrollmentService);

                JFrame frame = new JFrame("Test Module Tuyển Sinh - Mai Hồng Tín");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(900, 600);
                frame.setLocationRelativeTo(null);

// Dùng JTabbedPane để chứa 2 màn hình
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.addTab("1. Quản Lý Đánh Giá Năng Lực", testPanel);
                tabbedPane.addTab("2. Quản Lý Ghi Danh", enrollPanel);

                frame.add(tabbedPane);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khởi động: " + e.getMessage());
            }
        });
    }
}

package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.impl.ClasRepoImpl;
import vn.edu.ute.repo.impl.EnrollmentRepoImpl;
import vn.edu.ute.repo.impl.PlacementTestRepoImpl;
import vn.edu.ute.repo.impl.StudentRepoImpl;
import vn.edu.ute.service.impl.EnrollmentServiceImpl;
import vn.edu.ute.ui.EnrollmentManagerPanel;

import javax.swing.*;

public class App {
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
                EnrollmentManagerPanel enrollmentPanel = new EnrollmentManagerPanel(enrollmentService);

                JFrame frame = new JFrame("Test Module Tuyển Sinh - Mai Hồng Tín");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(800, 500);
                frame.setLocationRelativeTo(null);
                frame.add(enrollmentPanel);

                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khởi động: " + e.getMessage());
            }
        });
    }
}

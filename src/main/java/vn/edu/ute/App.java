package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.*;
import vn.edu.ute.repo.impl.*;

// Import thêm các RepoImpl mới của bạn (nếu bạn để ở package repo.jpa thì import vn.edu.ute.repo.jpa.*)
import vn.edu.ute.repo.impl.PlacementTestRepoImpl;
import vn.edu.ute.repo.impl.EnrollmentRepoImpl;
import vn.edu.ute.repo.impl.StudentRepoImpl;
import vn.edu.ute.repo.impl.PromotionRepoImpl;

import vn.edu.ute.service.*;
import vn.edu.ute.service.impl.*;
import vn.edu.ute.ui.MainFrame;
import vn.edu.ute.ui.UI;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        UI.initLookAndFeel();
        TransactionManager tx = new TransactionManager();

        // === KHỞI TẠO REPOSITORIES ===
        ClasRepo classRepo = new ClasRepoImpl();

        // Các Repo mới
        PlacementTestRepo testRepo = new PlacementTestRepoImpl();
        EnrollmentRepo enrollmentRepo = new EnrollmentRepoImpl();
        StudentRepo studentRepo = new StudentRepoImpl();
        PromotionRepo promotionRepo = new PromotionRepoImpl();


        // Các Service mới (Truyền đúng thứ tự tham số vào Constructor)
        EnrollmentService enrollmentService = new EnrollmentServiceImpl(tx, testRepo, enrollmentRepo, classRepo, studentRepo);
        PromotionService promotionService = new PromotionServiceImpl(tx, promotionRepo);

        // === KHỞI TẠO GIAO DIỆN CHÍNH ===
        SwingUtilities.invokeLater(() -> {
            // Truyền tất cả các service vào MainFrame
            MainFrame frame = new MainFrame(
                    enrollmentService, promotionService // Bơm thêm 2 service mới
            );
            frame.setVisible(true);
        });
    }
}
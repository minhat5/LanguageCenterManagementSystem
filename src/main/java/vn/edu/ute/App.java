package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.*;
import vn.edu.ute.repo.impl.*;

// Import thêm các RepoImpl mới của bạn
import vn.edu.ute.repo.impl.PlacementTestRepoImpl;
import vn.edu.ute.repo.impl.EnrollmentRepoImpl;
import vn.edu.ute.repo.impl.StudentRepoImpl;
import vn.edu.ute.repo.impl.UserAccountRepoImpl;   // <-- Import thêm cho StudentService
import vn.edu.ute.repo.impl.PromotionRepoImpl;
import vn.edu.ute.repo.impl.ResultRepoImpl;
import vn.edu.ute.repo.impl.CertificateRepoImpl;

import vn.edu.ute.service.*;
import vn.edu.ute.service.impl.*;
import vn.edu.ute.ui.MainFrame;
import vn.edu.ute.ui.UI;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        UI.initLookAndFeel();
        TransactionManager tx = new TransactionManager();

        ClasRepo classRepo = new ClasRepoImpl();

        //Khởi tạo Repo cho Student và UserAccount
        StudentRepo studentRepo = new StudentRepoImpl();
        UserAccountRepo userAccountRepo = new UserAccountRepoImpl(); // <-- Thêm mới

        //Các Repo của luồng Tuyển sinh & Khuyến mãi
        PlacementTestRepo testRepo = new PlacementTestRepoImpl();
        EnrollmentRepo enrollmentRepo = new EnrollmentRepoImpl();
        PromotionRepo promotionRepo = new PromotionRepoImpl();

        //Các Repo của luồng Điểm & Chứng chỉ
        ResultRepo resultRepo = new ResultRepoImpl();
        CertificateRepo certRepo = new CertificateRepoImpl();
        //Repo báo cáo
        PaymentRepo paymentRepo = new PaymentRepoImpl();

        //Service cơ sở
        ClasService classService = new ClasServiceImpl(classRepo, tx);

        // Khởi tạo StudentService truyền ĐÚNG 3 THAM SỐ như bạn đã thiết kế
        StudentService studentService = new StudentServiceImpl(studentRepo, userAccountRepo, tx);

        //Service Tuyển sinh & Khuyến mãi
        EnrollmentService enrollmentService = new EnrollmentServiceImpl(tx, testRepo, enrollmentRepo, classRepo, studentRepo);
        PromotionService promotionService = new PromotionServiceImpl(tx, promotionRepo);

        //Service Điểm & Chứng chỉ
        CertificationService certService = new CertificationServiceImpl(tx, resultRepo, certRepo);

        //Service báo cáo
        ReportService reportService = new ReportServiceImpl(tx, paymentRepo, resultRepo);

        SwingUtilities.invokeLater(() -> {
            // Bơm các service vào MainFrame
            MainFrame frame = new MainFrame(
                    enrollmentService,
                    promotionService,
                    certService,
                    classService,
                    studentService,
                    reportService
            );
            frame.setVisible(true);
        });
    }
}
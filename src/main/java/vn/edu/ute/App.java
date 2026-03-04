package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.CourseRepository;
import vn.edu.ute.repo.impl.CourseRepositoryImpl;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.ui.CourseFrame;
import vn.edu.ute.ui.UI;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        UI.initLookAndFeel();
        TransactionManager tx = new TransactionManager();
        CourseRepository courseRepo = new CourseRepositoryImpl();
        CourseService courseService = new CourseService(courseRepo, tx);

        SwingUtilities.invokeLater(() -> {
            CourseFrame frame = new CourseFrame(courseService);
            frame.setVisible(true);
        });
    }
}

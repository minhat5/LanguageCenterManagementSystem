package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.*;
import vn.edu.ute.repo.impl.*;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.service.impl.*;
import vn.edu.ute.ui.MainFrame;
import vn.edu.ute.ui.UI;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        UI.initLookAndFeel();
        TransactionManager tx = new TransactionManager();
        CourseRepo courseRepo = new CourseRepoImpl();
        ClasRepo classRepo = new ClasRepoImpl();
        TeacherRepo teacherRepo = new TeacherRepoImpl();
        BranchRepo branchRepo = new BranchRepoImpl();
        RoomRepo roomRepo = new RoomRepoImpl();

        CourseServiceImpl courseService = new CourseServiceImpl(courseRepo, tx);
        ClasServiceImpl classService = new ClasServiceImpl(classRepo, tx);
        TeacherServiceImpl teacherService = new TeacherServiceImpl(teacherRepo, tx);
        BranchService branchService = new BranchServiceImpl(branchRepo, tx);
        RoomServiceImpl roomService = new RoomServiceImpl(roomRepo, tx);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(courseService, classService, teacherService, branchService, roomService);
            frame.setVisible(true);
        });
    }
}

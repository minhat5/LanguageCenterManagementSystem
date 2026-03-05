package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.*;
import vn.edu.ute.repo.impl.*;
import vn.edu.ute.service.*;
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

        CourseService courseService = new CourseService(courseRepo, tx);
        ClasService classService = new ClasService(classRepo, tx);
        TeacherService teacherService = new TeacherService(teacherRepo, tx);
        BranchService branchService = new BranchService(branchRepo, tx);
        RoomService roomService = new RoomService(roomRepo, tx);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(courseService, classService, teacherService, branchService, roomService);
            frame.setVisible(true);
        });
    }
}

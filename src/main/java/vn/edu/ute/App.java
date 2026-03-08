package vn.edu.ute;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.repo.*;
import vn.edu.ute.repo.impl.*;
import vn.edu.ute.service.*;
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
        ScheduleRepo scheduleRepo = new ScheduleRepoImpl();

        CourseService courseService = new CourseServiceImpl(courseRepo, tx);
        ClasService classService = new ClasServiceImpl(classRepo, tx);
        TeacherService teacherService = new TeacherServiceImpl(teacherRepo, tx);
        BranchService branchService = new BranchServiceImpl(branchRepo, tx);
        RoomService roomService = new RoomServiceImpl(roomRepo, tx);
        ScheduleService scheduleService = new ScheduleServiceImpl(scheduleRepo, tx);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(courseService, classService, teacherService, branchService, roomService, scheduleService);
            frame.setVisible(true);
        });
    }
}

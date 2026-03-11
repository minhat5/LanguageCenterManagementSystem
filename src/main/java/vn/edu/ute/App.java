package vn.edu.ute;

import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Staff;
import vn.edu.ute.repo.*;
import vn.edu.ute.repo.impl.*;
import vn.edu.ute.service.*;
import vn.edu.ute.service.impl.*;
import vn.edu.ute.ui.UI;
import vn.edu.ute.ui.LoginFrame;

import javax.swing.*;
import java.util.List;

public class App {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        UI.initLookAndFeel();

        try {
            System.out.println(YELLOW + "[*] Khởi tạo hệ thống toàn diện..." + RESET);
            
            // 1. Khởi tạo Manager & Repositories (Gộp cả 2 nhánh)
            TransactionManager tx = new TransactionManager();
            UserAccountRepositoryImpl userRepo = new UserAccountRepositoryImpl();
            StaffRepositoryImpl staffRepo = new StaffRepositoryImpl();
            StudentRepositoryImpl studentRepo = new StudentRepositoryImpl();
            CourseRepo courseRepo = new CourseRepoImpl();
            ClasRepo classRepo = new ClasRepoImpl();
            TeacherRepo teacherRepo = new TeacherRepoImpl();
            BranchRepo branchRepo = new BranchRepoImpl();
            RoomRepo roomRepo = new RoomRepoImpl();
            ScheduleRepo scheduleRepo = new ScheduleRepoImpl();
            AttendanceRepo attendanceRepo = new AttendanceRepoImpl();
            EnrollmentRepo enrollmentRepo = new EnrollmentRepoImpl();

            // 2. Khởi tạo Services (Gộp cả 2 nhánh)
            AuthService authService = new AuthServiceImpl(userRepo, tx);
            StaffService staffService = new StaffServiceImpl(staffRepo, userRepo, tx);
            StudentService studentService = new StudentServiceImpl(studentRepo, userRepo, tx);
            CourseService courseService = new CourseServiceImpl(courseRepo, tx);
            ClasService classService = new ClasServiceImpl(classRepo, tx);
            TeacherService teacherService = new TeacherServiceImpl(teacherRepo, userRepo, tx);
            BranchService branchService = new BranchServiceImpl(branchRepo, tx);
            RoomService roomService = new RoomServiceImpl(roomRepo, tx);
            ScheduleService scheduleService = new ScheduleServiceImpl(scheduleRepo, attendanceRepo, enrollmentRepo, tx);
            AttendanceService attendanceService = new AttendanceServiceImpl(attendanceRepo, tx);

            // 3. Kiểm tra & Tạo Admin mặc định (Duy trì logic của Admin-HR)
            try {
                List<Staff> allStaff = staffService.getAllStaffs();
                if (allStaff.isEmpty()) {
                    System.out.println(CYAN + "[*] Đang khởi tạo tài khoản Admin hệ thống..." + RESET);
                    Staff admin = new Staff();
                    admin.setFullName("Super Admin");
                    admin.setRole(Role.Admin);
                    admin.setStatus(Status.Active);
                    staffService.createStaffAccount(admin, "admin", "123456");
                    System.out.println(GREEN + "[+] Tạo Admin thành công!" + RESET);
                }
            } catch (Exception ex) {
                System.err.println(RED + "[-] Lỗi DB Seed: " + ex.getMessage() + RESET);
            }

            // 4. Khởi chạy UI (Ưu tiên LoginFrame để bảo mật)
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame(
                    authService, branchService, roomService, staffService, studentService, teacherService
                );
                // Lưu ý: Sau khi login thành công, bạn sẽ truyền các CourseService vào MainFrame sau.
                loginFrame.setVisible(true);
            });

        } catch (Exception e) {
            System.out.println(RED + "[!] LỖI HỆ THỐNG: " + e.getMessage() + RESET);
            e.printStackTrace();
        }
    }
}
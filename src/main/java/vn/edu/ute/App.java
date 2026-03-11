package vn.edu.ute;

import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Staff;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.impl.AuthServiceImpl;
import vn.edu.ute.service.impl.BranchServiceImpl;
import vn.edu.ute.service.impl.RoomServiceImpl;
import vn.edu.ute.service.impl.StaffServiceImpl;
import vn.edu.ute.service.impl.StudentServiceImpl;
import vn.edu.ute.service.impl.TeacherServiceImpl;
import vn.edu.ute.repo.BranchRepository;
import vn.edu.ute.repo.impl.BranchRepositoryImpl;
import vn.edu.ute.repo.impl.RoomRepositoryImpl;
import vn.edu.ute.repo.impl.StaffRepositoryImpl;
import vn.edu.ute.repo.impl.TeacherRepositoryImpl;
import vn.edu.ute.repo.impl.UserAccountRepositoryImpl;
import vn.edu.ute.db.Jpa;

import java.util.List;

public class App {
    // ANSI Escape Codes để làm đẹp terminal (Màu sắc)
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BOLD = "\u001B[1m";

    public static void main(String[] args) {
        
        try {
            System.out.println(YELLOW + "[*] Khởi tạo kết nối CSDL và các Services..." + RESET);
            
            // gọi di
            TransactionManager txManager = new TransactionManager();
            UserAccountRepositoryImpl userRepo = new UserAccountRepositoryImpl();
            StaffRepositoryImpl staffRepo = new StaffRepositoryImpl();
            vn.edu.ute.repo.impl.StudentRepositoryImpl studentRepo = new vn.edu.ute.repo.impl.StudentRepositoryImpl();
            
            AuthServiceImpl authService = new AuthServiceImpl(userRepo, txManager);
            StaffServiceImpl staffService = new StaffServiceImpl(staffRepo, userRepo, txManager);
            StudentServiceImpl studentService = new StudentServiceImpl(studentRepo, userRepo, txManager);
            
            BranchRepositoryImpl branchRepo = new BranchRepositoryImpl();
            RoomRepositoryImpl roomRepo = new RoomRepositoryImpl();
            TeacherRepositoryImpl teacherRepo = new TeacherRepositoryImpl();
            
            BranchServiceImpl branchService = new BranchServiceImpl(branchRepo, txManager);
            RoomServiceImpl roomService = new RoomServiceImpl(roomRepo, txManager);
            TeacherServiceImpl teacherService = new TeacherServiceImpl(teacherRepo, userRepo, txManager);

            try {
                List<Staff> allStaff = staffService.getAllStaffs();
                System.out.println(YELLOW + "[*] Hiện có " + allStaff.size() + " nhân viên." + RESET);

                if (allStaff.isEmpty()) {
                    System.out.println(CYAN + "[*] Đang thử tạo Admin..." + RESET);
                    Staff admin = new Staff();
                    admin.setFullName("Super Admin");
                    admin.setRole(Role.Admin); // Kiểm tra xem Role.Admin trong Java có khớp 'Staff' hay 'Admin' trong DB không
                    admin.setStatus(Status.Active);

                    // Hãy đảm bảo hàm này bên trong có gọi txManager.begin() và txManager.commit()
                    staffService.createStaffAccount(admin, "admin", "123456");
                    System.out.println(GREEN + "[+] Đã gọi lệnh tạo xong." + RESET);
                }
            } catch (Exception ex) {
                System.err.println(RED + "[-] Lỗi chi tiết: ");
                ex.printStackTrace(); // Dùng printStackTrace để thấy rõ lỗi SQL gì
            }

            
            // goi giao dien
            javax.swing.SwingUtilities.invokeLater(() -> {
                vn.edu.ute.ui.LoginFrame loginFrame = new vn.edu.ute.ui.LoginFrame(
                        authService, branchService, roomService, staffService, studentService, teacherService
                );
                loginFrame.setVisible(true);
            });

        } catch (Exception e) {
            System.out.println(RED + "\n[!] CÓ LỖI: " + e.getMessage() + RESET);
            e.printStackTrace();
        } 
    }
}
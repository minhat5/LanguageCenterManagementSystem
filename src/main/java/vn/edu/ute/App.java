package vn.edu.ute;

import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.common.enumeration.StaffRole;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.common.factory.ServiceFactory;
import vn.edu.ute.model.Staff;
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

            // 1. Dependency Injection via ServiceFactory
            ServiceFactory factory = ServiceFactory.getInstance();

            // 2. Kiểm tra & Tạo Admin mặc định
            initDefaultAdmin(factory);

            // 3. Khởi chạy Login UI
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });

        } catch (Exception e) {
            System.out.println(RED + "[!] LỖI HỆ THỐNG: " + e.getMessage() + RESET);
            e.printStackTrace();
        }
    }

    private static void initDefaultAdmin(ServiceFactory factory) {
        try {
            List<Staff> allStaff = factory.getStaffService().getAllStaffs();

            if (allStaff.isEmpty()) {
                System.out.println(CYAN + "[*] Đang khởi tạo tài khoản Admin hệ thống..." + RESET);

                Staff admin = new Staff();
                admin.setFullName("Super Admin");
                admin.setStaffRole(StaffRole.Manager);
                admin.setStatus(Status.Active);

                factory.getStaffService().createStaffAccount(
                        admin,
                        "admin",
                        "123456",
                        Role.Admin
                );

                System.out.println(GREEN + "[+] Tạo Admin thành công!" + RESET);
            }

        } catch (Exception ex) {
            System.err.println(RED + "[-] Lỗi DB Seed: " + ex.getMessage() + RESET);
        }
    }
}
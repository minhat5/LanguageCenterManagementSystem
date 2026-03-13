package vn.edu.ute;

import vn.edu.ute.common.factory.ServiceFactory;
import vn.edu.ute.ui.UI;
import vn.edu.ute.ui.LoginFrame;

import javax.swing.*;

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

            // 2. Khởi chạy Login UI
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });

        } catch (Exception e) {
            System.out.println(RED + "[!] LỖI HỆ THỐNG: " + e.getMessage() + RESET);
            e.printStackTrace();
        }
    }
}
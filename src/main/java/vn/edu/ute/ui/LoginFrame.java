package vn.edu.ute.ui;

import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.AuthService;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.service.StaffService;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.service.TeacherService;
import vn.edu.ute.common.security.AuthContext;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegisterStudent;
    
    private final AuthService authService;
    private final BranchService branchService;
    private final RoomService roomService;
    private final StaffService staffService;
    private final StudentService studentService;
    private final TeacherService teacherService;

    public LoginFrame(AuthService authService, BranchService branchService, RoomService roomService, StaffService staffService, StudentService studentService, TeacherService teacherService) {
        this.authService = authService;
        this.branchService = branchService;
        this.roomService = roomService;
        this.staffService = staffService;
        this.studentService = studentService;
        this.teacherService = teacherService;
        
        setTitle("Đăng nhập Hệ thống Quản lý Ngoại ngữ");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Chào mừng
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Tài khoản:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtUsername = new JTextField(15);
        panel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        // Panel chứa các nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        btnLogin = new JButton("Đăng nhập");
        btnRegisterStudent = new JButton("Đăng ký Học viên");
        
        // Cài đặt sự kiện Nút Đăng nhập
        btnLogin.addActionListener(e -> attemptLogin());
        
        // Cài đặt sự kiện đăng ký
        btnRegisterStudent.addActionListener(e -> {
            RegisterStudentFrame registerFrame = new RegisterStudentFrame(studentService, this);
            registerFrame.setVisible(true);
            this.setVisible(false); // Ẩn màn hình đăng nhập
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegisterStudent);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }
    
    private void attemptLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            UserAccount loggedInUser = authService.login(username, password);
            AuthContext.setCurrentUser(loggedInUser);
            
            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!\nVai trò: " + loggedInUser.getRole(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            // Chuyển hướng sang MainFrame chính của ứng dụng
            MainFrame mainFrame = new MainFrame(staffService, branchService, roomService, studentService, teacherService);
            mainFrame.setVisible(true);
            this.dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản, mật khẩu hoặc khóa.\nChi tiết: " + ex.getMessage(), "Thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}

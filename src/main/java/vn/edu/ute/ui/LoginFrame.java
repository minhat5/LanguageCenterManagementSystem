package vn.edu.ute.ui;

import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.*;
import vn.edu.ute.common.security.AuthContext;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegisterStudent;

    private final AuthService authService;
    private final StudentService studentService;

    public LoginFrame() {
        vn.edu.ute.common.factory.ServiceFactory factory = vn.edu.ute.common.factory.ServiceFactory.getInstance();
        this.authService = factory.getAuthService();
        this.studentService = factory.getStudentService();

        setTitle("Đăng nhập Hệ thống Quản lý Ngoại ngữ");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Tài khoản:"), gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        panel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        btnLogin = new JButton("Đăng nhập");
        btnRegisterStudent = new JButton("Đăng ký Học viên");

        btnLogin.addActionListener(e -> attemptLogin());

        btnRegisterStudent.addActionListener(e -> {
            RegisterStudentFrame registerFrame = new RegisterStudentFrame(studentService, this);
            registerFrame.setVisible(true);
            this.setVisible(false);
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegisterStudent);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void attemptLogin() {

        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());

        if (username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu!");
            return;
        }

        try {

            UserAccount user = authService.login(username, password);
            vn.edu.ute.common.session.SessionManager.login(user);

            JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");

            MainFrame mainFrame = new MainFrame();

            mainFrame.setVisible(true);
            this.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
        }
    }
}
package vn.edu.ute.ui;

import vn.edu.ute.common.enumeration.Gender;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.model.Student;
import vn.edu.ute.service.StudentService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class RegisterStudentFrame extends JFrame {

    private JTextField txtFullName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JComboBox<Gender> cbxGender;
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    
    private JButton btnRegister;
    private JButton btnBack;

    private final StudentService studentService;
    private final JFrame parentFrame;

    public RegisterStudentFrame(StudentService studentService, JFrame parentFrame) {
        this.studentService = studentService;
        this.parentFrame = parentFrame;

        setTitle("Đăng ký Thông tin Học viên");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 10, 8, 10);

        // Chào mừng
        JLabel lblTitle = new JLabel("ĐĂNG KÝ HỌC VIÊN CHÍNH THỨC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        // --- SECTION: THÔNG TIN CÁ NHÂN ---
        int row = 1;
        gbc.gridwidth = 1;

        // Họ và Tên
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Họ và Tên (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        txtFullName = new JTextField(20);
        panel.add(txtFullName, gbc);

        // Giới tính
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Giới tính (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        cbxGender = new JComboBox<>(Gender.values());
        panel.add(cbxGender, gbc);

        // Số điện thoại
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Số điện thoại (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        txtPhone = new JTextField(20);
        panel.add(txtPhone, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Email (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        txtEmail = new JTextField(20);
        panel.add(txtEmail, gbc);

        // --- SECTION: THÔNG TIN TÀI KHOẢN ---
        // Separator
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(new JSeparator(), gbc);
        row++;

        gbc.gridwidth = 1;
        // Username
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Tên tài khoản (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        txtUsername = new JTextField(20);
        panel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Mật khẩu (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        txtPassword = new JPasswordField(20);
        panel.add(txtPassword, gbc);
        
        // Confirm Password
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Xác nhận MK (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        txtConfirmPassword = new JPasswordField(20);
        panel.add(txtConfirmPassword, gbc);

        // Panel chứa các nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnRegister = new JButton("Đăng ký");
        btnBack = new JButton("Trở lại");
        
        btnRegister.addActionListener(e -> attemptRegister());
        btnBack.addActionListener(e -> {
            this.dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
        });

        buttonPanel.add(btnRegister);
        buttonPanel.add(btnBack);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }
    
    private void attemptRegister() {
        String fullName = txtFullName.getText().trim();
        Gender gender = (Gender) cbxGender.getSelectedItem();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // Validate cơ bản
        if (fullName.isBlank() || phone.isBlank() || email.isBlank() || username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ các trường bắt buộc (*).", "Lỗi dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Bước 1: Khởi tạo Entity Student
            Student newStudent = new Student();
            newStudent.setFullName(fullName);
            newStudent.setGender(gender);
            newStudent.setPhone(phone);
            newStudent.setEmail(email);
            newStudent.setRegistrationDate(LocalDate.now()); // Date now
            newStudent.setStatus(Status.Active);
            
            // Bước 2: Gọi Service chạy Transaction để lưu
            studentService.registerStudentAccount(newStudent, username, password);
            
            JOptionPane.showMessageDialog(this, 
                "Đăng ký thành công tài khoản Học viên!\n- Tài khoản: " + username + "\n- Chủ tài khoản: " + fullName, 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            this.dispose();
            if (parentFrame != null) {
                parentFrame.setVisible(true);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đăng ký thất bại.\nChi tiết: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}

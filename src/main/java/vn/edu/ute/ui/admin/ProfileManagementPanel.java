package vn.edu.ute.ui.admin;

import vn.edu.ute.common.enumeration.Gender;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.model.Student;
import vn.edu.ute.model.Teacher;
import vn.edu.ute.service.StudentService;
import vn.edu.ute.service.TeacherService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.github.lgooddatepicker.components.DatePicker;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileManagementPanel extends JPanel {

    private final StudentService studentService;
    private final TeacherService teacherService;

    // --- Student Tab Components ---
    private JTable tblStudents;
    private DefaultTableModel studentTableModel;
    private JTextField txtStudentId, txtStudentName, txtStudentPhone, txtStudentEmail, txtStudentAddress;
    private DatePicker studentDobPicker, studentRegDatePicker;
    private JComboBox<Gender> cbStudentGender;
    private JComboBox<Status> cbStudentStatus;
    private JTextField txtStudentUsername;
    private JPasswordField txtStudentPassword;
    private JTextField txtSearchStudent;
    private JComboBox<String> cbStudentFilterStatus;
    private JComboBox<String> cbStudentFilterGender;

    
    // --- Teacher Tab Components ---
    private JTable tblTeachers;
    private DefaultTableModel teacherTableModel;
    private JTextField txtTeacherId, txtTeacherName, txtTeacherPhone, txtTeacherEmail;
    private JComboBox<String> cbTeacherSpecialty;
    private DatePicker teacherHireDatePicker;
    private JComboBox<Status> cbTeacherStatus;
    private JTextField txtTeacherUsername;
    private JPasswordField txtTeacherPassword;
    private JTextField txtSearchTeacher;
    private JComboBox<String> cbTeacherFilterStatus;
    private JComboBox<String> cbTeacherFilterSpecialty;


    private List<Student> allStudents;
    private List<Teacher> allTeachers;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ProfileManagementPanel(StudentService studentService, TeacherService teacherService) {
        this.studentService = studentService;
        this.teacherService = teacherService;

        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Quản lý Giáo viên", createTeacherPanel());
        tabbedPane.addTab("Quản lý Học viên", createStudentPanel());

        add(tabbedPane, BorderLayout.CENTER);

        loadTeacherData();
        loadStudentData();
    }

    // ==========================================
    // TEACHER TAB
    // ==========================================
    private JPanel createTeacherPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Giáo viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID:"), gbc);
        txtTeacherId = new JTextField(); txtTeacherId.setEnabled(false);
        gbc.gridx = 1; formPanel.add(txtTeacherId, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Họ tên:"), gbc);
        txtTeacherName = new JTextField(); gbc.gridx = 3; gbc.weightx = 1.0; formPanel.add(txtTeacherName, gbc); gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Điện thoại:"), gbc);
        txtTeacherPhone = new JTextField(); gbc.gridx = 1; formPanel.add(txtTeacherPhone, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Email:"), gbc);
        txtTeacherEmail = new JTextField(); gbc.gridx = 3; formPanel.add(txtTeacherEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Chuyên môn:"), gbc);
        cbTeacherSpecialty = new JComboBox<>(new String[]{"Tiếng Anh Giao Tiếp", "TOEIC", "IELTS", "Tiếng Nhật (N5-N1)", "Tiếng Hàn (TOPIK)", "Tiếng Trung (HSK)", "Ngữ Pháp Cơ Bản", "Khác"}); 
        cbTeacherSpecialty.setEditable(true); // Allow custom input if they aren't on the list
        gbc.gridx = 1; formPanel.add(cbTeacherSpecialty, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Ngày thuê:"), gbc);
        teacherHireDatePicker = new DatePicker(); gbc.gridx = 3; formPanel.add(teacherHireDatePicker, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Trạng thái:"), gbc);
        cbTeacherStatus = new JComboBox<>(Status.values()); gbc.gridx = 1; formPanel.add(cbTeacherStatus, gbc);

        // Account section
        JPanel accPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        accPanel.setBorder(BorderFactory.createTitledBorder("Tài khoản (Chỉ dùng khi Thêm Mới)"));
        accPanel.add(new JLabel("Username:"));
        txtTeacherUsername = new JTextField(); accPanel.add(txtTeacherUsername);
        accPanel.add(new JLabel("Mật khẩu:"));
        txtTeacherPassword = new JPasswordField(); accPanel.add(txtTeacherPassword);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; formPanel.add(accPanel, gbc);

        // Buttons & Search
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm Mới");
        JButton btnUpdate = new JButton("Cập Nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm Mới Form");
        
        actionPanel.add(btnAdd); actionPanel.add(btnUpdate); actionPanel.add(btnDelete); actionPanel.add(btnClear);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc Giáo viên"));
        txtSearchTeacher = new JTextField(15);
        cbTeacherFilterStatus = new JComboBox<>(new String[]{"Tất cả", "Active", "Inactive", "Suspended"});
        cbTeacherFilterSpecialty = new JComboBox<>(new String[]{"Tất cả", "Tiếng Anh Giao Tiếp", "TOEIC", "IELTS", "Tiếng Nhật (N5-N1)", "Tiếng Hàn (TOPIK)", "Tiếng Trung (HSK)", "Khác"});

        filterPanel.add(new JLabel("Tìm kiếm (Tên/SĐT):")); filterPanel.add(txtSearchTeacher);
        filterPanel.add(new JLabel("Trạng thái:")); filterPanel.add(cbTeacherFilterStatus);
        filterPanel.add(new JLabel("Chuyên môn:")); filterPanel.add(cbTeacherFilterSpecialty);

        txtSearchTeacher.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTeachers(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTeachers(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTeachers(); }
        });
        cbTeacherFilterStatus.addActionListener(e -> filterTeachers());
        cbTeacherFilterSpecialty.addActionListener(e -> filterTeachers());

        JPanel southOfTop = new JPanel(new BorderLayout());
        southOfTop.add(actionPanel, BorderLayout.NORTH);
        southOfTop.add(filterPanel, BorderLayout.SOUTH);
        topPanel.add(southOfTop, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        teacherTableModel = new DefaultTableModel(new Object[]{"ID", "Họ Tên", "Điện thoại", "Email", "Chuyên môn", "Ngày thuê", "Trạng thái"}, 0){
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblTeachers = new JTable(teacherTableModel);
        tblTeachers.getSelectionModel().addListSelectionListener(e -> fillTeacherForm());
        panel.add(new JScrollPane(tblTeachers), BorderLayout.CENTER);

        // Listeners
        btnAdd.addActionListener(e -> addTeacher());
        btnUpdate.addActionListener(e -> updateTeacher());
        btnDelete.addActionListener(e -> deleteTeacher());
        btnClear.addActionListener(e -> clearTeacherForm());

        return panel;
    }
    
    // ==========================================
    // STUDENT TAB
    // ==========================================
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Học viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID:"), gbc);
        txtStudentId = new JTextField(); txtStudentId.setEnabled(false);
        gbc.gridx = 1; formPanel.add(txtStudentId, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Họ tên:"), gbc);
        txtStudentName = new JTextField(); gbc.gridx = 3; gbc.weightx = 1.0; formPanel.add(txtStudentName, gbc); gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Điện thoại:"), gbc);
        txtStudentPhone = new JTextField(); gbc.gridx = 1; formPanel.add(txtStudentPhone, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Email:"), gbc);
        txtStudentEmail = new JTextField(); gbc.gridx = 3; formPanel.add(txtStudentEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Ngày sinh:"), gbc);
        studentDobPicker = new DatePicker(); gbc.gridx = 1; formPanel.add(studentDobPicker, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Giới tính:"), gbc);
        cbStudentGender = new JComboBox<>(Gender.values()); gbc.gridx = 3; formPanel.add(cbStudentGender, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Địa chỉ:"), gbc);
        txtStudentAddress = new JTextField(); gbc.gridx = 1; formPanel.add(txtStudentAddress, gbc);

        gbc.gridx = 2; formPanel.add(new JLabel("Ngày ĐK:"), gbc);
        studentRegDatePicker = new DatePicker();
        studentRegDatePicker.setDateToToday();
        gbc.gridx = 3; formPanel.add(studentRegDatePicker, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Trạng thái:"), gbc);
        cbStudentStatus = new JComboBox<>(Status.values()); gbc.gridx = 1; formPanel.add(cbStudentStatus, gbc);

        // Account section
        JPanel accPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        accPanel.setBorder(BorderFactory.createTitledBorder("Tài khoản (Chỉ dùng khi Thêm Mới)"));
        accPanel.add(new JLabel("Username:"));
        txtStudentUsername = new JTextField(); accPanel.add(txtStudentUsername);
        accPanel.add(new JLabel("Mật khẩu:"));
        txtStudentPassword = new JPasswordField(); accPanel.add(txtStudentPassword);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; formPanel.add(accPanel, gbc);

        // Buttons & Search
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm Mới");
        JButton btnUpdate = new JButton("Cập Nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm Mới Form");
        
        actionPanel.add(btnAdd); actionPanel.add(btnUpdate); actionPanel.add(btnDelete); actionPanel.add(btnClear);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc Học viên"));
        txtSearchStudent = new JTextField(15);
        cbStudentFilterStatus = new JComboBox<>(new String[]{"Tất cả", "Active", "Inactive", "Suspended"});
        cbStudentFilterGender = new JComboBox<>(new String[]{"Tất cả", "MALE", "FEMALE", "OTHER"});

        filterPanel.add(new JLabel("Tìm kiếm (Tên/SĐT):")); filterPanel.add(txtSearchStudent);
        filterPanel.add(new JLabel("Trạng thái:")); filterPanel.add(cbStudentFilterStatus);
        filterPanel.add(new JLabel("Giới tính:")); filterPanel.add(cbStudentFilterGender);

        txtSearchStudent.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterStudents(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterStudents(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterStudents(); }
        });
        cbStudentFilterStatus.addActionListener(e -> filterStudents());
        cbStudentFilterGender.addActionListener(e -> filterStudents());

        JPanel southOfTop = new JPanel(new BorderLayout());
        southOfTop.add(actionPanel, BorderLayout.NORTH);
        southOfTop.add(filterPanel, BorderLayout.SOUTH);
        topPanel.add(southOfTop, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        studentTableModel = new DefaultTableModel(new Object[]{"ID", "Họ Tên", "SĐT", "Email", "Ngày sinh", "Giới tính", "Trạng thái"}, 0){
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblStudents = new JTable(studentTableModel);
        tblStudents.getSelectionModel().addListSelectionListener(e -> fillStudentForm());
        panel.add(new JScrollPane(tblStudents), BorderLayout.CENTER);

        // Listeners
        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnClear.addActionListener(e -> clearStudentForm());

        return panel;
    }

    // ==========================================
    // TEACHER LOGIC
    // ==========================================
    private void loadTeacherData() {
        try {
            allTeachers = teacherService.getAllTeachers();
            renderTeacherTable(allTeachers);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu giáo viên: " + ex.getMessage());
        }
    }

    private void renderTeacherTable(List<Teacher> list) {
        teacherTableModel.setRowCount(0);
        for (Teacher t : list) {
            teacherTableModel.addRow(new Object[]{
                t.getTeacherId(), t.getFullName(), t.getPhone(), t.getEmail(), t.getSpecialty(), 
                t.getHireDate() != null ? t.getHireDate().format(dateFormatter) : "", t.getStatus().name()
            });
        }
    }

    private void filterTeachers() {
        try {
            String kw = txtSearchTeacher != null ? txtSearchTeacher.getText() : "";
            Status st = null;
            if (cbTeacherFilterStatus != null && cbTeacherFilterStatus.getSelectedIndex() > 0) {
                st = Status.valueOf(cbTeacherFilterStatus.getSelectedItem().toString());
            }
            String spec = "Tất cả";
            if (cbTeacherFilterSpecialty != null && cbTeacherFilterSpecialty.getSelectedIndex() > 0) {
                spec = cbTeacherFilterSpecialty.getSelectedItem().toString();
            }
            List<Teacher> filtered = teacherService.filterTeachers(kw, st, spec);
            renderTeacherTable(filtered);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc dữ liệu: " + ex.getMessage());
        }
    }

    private void fillTeacherForm() {
        int r = tblTeachers.getSelectedRow();
        if (r >= 0) {
            txtTeacherId.setText(tblTeachers.getValueAt(r, 0).toString());
            txtTeacherName.setText(tblTeachers.getValueAt(r, 1).toString());
            txtTeacherPhone.setText(tblTeachers.getValueAt(r, 2) != null ? tblTeachers.getValueAt(r, 2).toString() : "");
            txtTeacherEmail.setText(tblTeachers.getValueAt(r, 3) != null ? tblTeachers.getValueAt(r, 3).toString() : "");
            cbTeacherSpecialty.setSelectedItem(tblTeachers.getValueAt(r, 4) != null ? tblTeachers.getValueAt(r, 4).toString() : "Khác");
            String hireDate = tblTeachers.getValueAt(r, 5) != null ? tblTeachers.getValueAt(r, 5).toString() : "";
            teacherHireDatePicker.setDate(hireDate.isEmpty() ? null : LocalDate.parse(hireDate, dateFormatter));
            cbTeacherStatus.setSelectedItem(Status.valueOf(tblTeachers.getValueAt(r, 6).toString()));
            
            txtTeacherUsername.setEnabled(false);
            txtTeacherPassword.setEnabled(false);
        }
    }

    private void clearTeacherForm() {
        txtTeacherId.setText(""); txtTeacherName.setText(""); txtTeacherPhone.setText("");
        txtTeacherEmail.setText(""); cbTeacherSpecialty.setSelectedIndex(0); teacherHireDatePicker.clear();
        cbTeacherStatus.setSelectedIndex(0);
        txtTeacherUsername.setText(""); txtTeacherPassword.setText("");
        txtTeacherUsername.setEnabled(true); txtTeacherPassword.setEnabled(true);
        tblTeachers.clearSelection();
    }

    private void addTeacher() {
        try {
            if (txtTeacherName.getText().isBlank()) throw new Exception("Tên giáo viên trống");
            String uname = txtTeacherUsername.getText();
            String pwd = new String(txtTeacherPassword.getPassword());
            if (uname.isBlank() || pwd.isBlank()) throw new Exception("Vui lòng nhập Username và Password mới");

            Teacher t = new Teacher();
            t.setFullName(txtTeacherName.getText().trim());
            t.setPhone(txtTeacherPhone.getText().trim());
            t.setEmail(txtTeacherEmail.getText().trim());
            t.setSpecialty(cbTeacherSpecialty.getSelectedItem() != null ? cbTeacherSpecialty.getSelectedItem().toString() : "");
            t.setHireDate(teacherHireDatePicker.getDate());
            t.setStatus((Status) cbTeacherStatus.getSelectedItem());

            teacherService.createTeacherAccount(t, uname, pwd);
            JOptionPane.showMessageDialog(this, "Thêm Giáo viên thành công!");
            clearTeacherForm();
            loadTeacherData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTeacher() {
        try {
            if (txtTeacherId.getText().isBlank()) throw new Exception("Vui lòng chọn giáo viên để cập nhật!");
            // Because we don't have getTeacherById, we will just find it from allTeachers list
            Long id = Long.parseLong(txtTeacherId.getText());
            Teacher t = allTeachers.stream().filter(tea -> tea.getTeacherId().equals(id)).findFirst().orElseThrow(() -> new Exception("Không tìm thấy giáo viên"));
            
            t.setFullName(txtTeacherName.getText().trim());
            t.setPhone(txtTeacherPhone.getText().trim());
            t.setEmail(txtTeacherEmail.getText().trim());
            t.setSpecialty(cbTeacherSpecialty.getSelectedItem() != null ? cbTeacherSpecialty.getSelectedItem().toString() : "");
            t.setHireDate(teacherHireDatePicker.getDate());
            t.setStatus((Status) cbTeacherStatus.getSelectedItem());

            teacherService.updateTeacher(t);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            clearTeacherForm();
            loadTeacherData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTeacher() {
        try {
            if (txtTeacherId.getText().isBlank()) throw new Exception("Vui lòng chọn giáo viên để xóa!");
            int confirm = JOptionPane.showConfirmDialog(this, "Chắc chứ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                teacherService.deleteTeacher(Long.parseLong(txtTeacherId.getText()));
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                clearTeacherForm();
                loadTeacherData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    // ==========================================
    // STUDENT LOGIC
    // ==========================================
    private void loadStudentData() {
        try {
            allStudents = studentService.getAllStudents();
            renderStudentTable(allStudents);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu học viên: " + ex.getMessage());
        }
    }

    private void renderStudentTable(List<Student> list) {
        studentTableModel.setRowCount(0);
        for (Student s : list) {
            studentTableModel.addRow(new Object[]{
                s.getStudentId(), s.getFullName(), s.getPhone(), s.getEmail(),
                s.getDateOfBirth() != null ? s.getDateOfBirth().format(dateFormatter) : "",
                s.getGender().name(), s.getStatus().name()
            });
        }
    }

    private void filterStudents() {
        try {
            String kw = txtSearchStudent != null ? txtSearchStudent.getText() : "";
            Status st = null;
            if (cbStudentFilterStatus != null && cbStudentFilterStatus.getSelectedIndex() > 0) {
                st = Status.valueOf(cbStudentFilterStatus.getSelectedItem().toString());
            }
            Gender gen = null;
            if (cbStudentFilterGender != null && cbStudentFilterGender.getSelectedIndex() > 0) {
                gen = Gender.valueOf(cbStudentFilterGender.getSelectedItem().toString());
            }
            List<Student> filtered = studentService.filterStudents(kw, gen, st);
            renderStudentTable(filtered);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc dữ liệu: " + ex.getMessage());
        }
    }

    private void fillStudentForm() {
        int r = tblStudents.getSelectedRow();
        if (r >= 0) {
            Long searchId = Long.parseLong(tblStudents.getValueAt(r, 0).toString());
            Student t = allStudents.stream().filter(s -> s.getStudentId().equals(searchId)).findFirst().orElse(null);
            if(t != null) {
                txtStudentId.setText(t.getStudentId().toString());
                txtStudentName.setText(t.getFullName());
                txtStudentPhone.setText(t.getPhone());
                txtStudentEmail.setText(t.getEmail());
                studentDobPicker.setDate(t.getDateOfBirth());
                cbStudentGender.setSelectedItem(t.getGender());
                txtStudentAddress.setText(t.getAddress());
                studentRegDatePicker.setDate(t.getRegistrationDate());
                cbStudentStatus.setSelectedItem(t.getStatus());
            }

            txtStudentUsername.setEnabled(false);
            txtStudentPassword.setEnabled(false);
        }
    }

    private void clearStudentForm() {
        txtStudentId.setText(""); txtStudentName.setText(""); studentDobPicker.clear();
        txtStudentPhone.setText(""); txtStudentEmail.setText(""); txtStudentAddress.setText("");
        studentRegDatePicker.setDateToToday();
        cbStudentGender.setSelectedIndex(0); cbStudentStatus.setSelectedIndex(0);
        txtStudentUsername.setText(""); txtStudentPassword.setText("");
        txtStudentUsername.setEnabled(true); txtStudentPassword.setEnabled(true);
        tblStudents.clearSelection();
    }

    private void addStudent() {
        try {
            if (txtStudentName.getText().isBlank()) throw new Exception("Tên học viên trống");
            String uname = txtStudentUsername.getText();
            String pwd = new String(txtStudentPassword.getPassword());
            if (uname.isBlank() || pwd.isBlank()) throw new Exception("Vui lòng nhập Username và Password mới");

            Student s = new Student();
            s.setFullName(txtStudentName.getText().trim());
            s.setPhone(txtStudentPhone.getText().trim());
            s.setEmail(txtStudentEmail.getText().trim());
            s.setDateOfBirth(studentDobPicker.getDate());
            s.setGender((Gender) cbStudentGender.getSelectedItem());
            s.setAddress(txtStudentAddress.getText().trim());
            if (studentRegDatePicker.getDate() != null) s.setRegistrationDate(studentRegDatePicker.getDate());
            s.setStatus((Status) cbStudentStatus.getSelectedItem());

            studentService.registerStudentAccount(s, uname, pwd);
            JOptionPane.showMessageDialog(this, "Thêm Học viên thành công!");
            clearStudentForm();
            loadStudentData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        try {
            if (txtStudentId.getText().isBlank()) throw new Exception("Vui lòng chọn học viên để cập nhật!");
            Long id = Long.parseLong(txtStudentId.getText());
            Student s = allStudents.stream().filter(stu -> stu.getStudentId().equals(id)).findFirst().orElseThrow(() -> new Exception("Không tìm thấy"));
            
            s.setFullName(txtStudentName.getText().trim());
            s.setPhone(txtStudentPhone.getText().trim());
            s.setEmail(txtStudentEmail.getText().trim());
            s.setDateOfBirth(studentDobPicker.getDate());
            s.setGender((Gender) cbStudentGender.getSelectedItem());
            s.setAddress(txtStudentAddress.getText().trim());
            if (studentRegDatePicker.getDate() != null) s.setRegistrationDate(studentRegDatePicker.getDate());
            s.setStatus((Status) cbStudentStatus.getSelectedItem());

            studentService.updateStudent(s);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            clearStudentForm();
            loadStudentData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        try {
            if (txtStudentId.getText().isBlank()) throw new Exception("Vui lòng chọn học viên để xóa!");
            int confirm = JOptionPane.showConfirmDialog(this, "Chắc chứ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                studentService.deleteStudent(Long.parseLong(txtStudentId.getText()));
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                clearStudentForm();
                loadStudentData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

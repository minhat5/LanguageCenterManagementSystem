package vn.edu.ute.ui.admin;

import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.common.enumeration.StaffRole;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.model.Staff;
import vn.edu.ute.service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffManagementPanel extends JPanel {

    private final StaffService staffService;

    private JTable tblStaffs;
    private DefaultTableModel staffTableModel;

    private JTextField txtStaffId, txtFullName, txtPhone, txtEmail;
    private JComboBox<Role> cbUserRole;
    private JComboBox<StaffRole> cbStaffRole;
    private JComboBox<Status> cbStatus;

    // Filters
    private JTextField txtSearch;
    private JComboBox<String> cbFilterStaffRole;
    private JComboBox<String> cbFilterStatus;

    // Accounts elements
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public StaffManagementPanel(StaffService staffService) {
        this.staffService = staffService;

        setLayout(new BorderLayout());
        add(createMainPanel(), BorderLayout.CENTER);
        loadStaffData();
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- FORM PANEL ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Nhân viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        txtStaffId = new JTextField();
        txtStaffId.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(txtStaffId, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        txtFullName = new JTextField();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(txtFullName, gbc);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("SĐT:"), gbc);
        txtPhone = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(txtPhone, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        txtEmail = new JTextField();
        gbc.gridx = 3;
        gbc.gridy = 1;
        formPanel.add(txtEmail, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Phân quyền:"), gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;

        cbUserRole = new JComboBox<>(new Role[] { Role.Staff, Role.Admin });
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(cbUserRole, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Vai trò nhân viên:"), gbc);
        cbStaffRole = new JComboBox<>(StaffRole.values());
        gbc.gridx = 3;
        gbc.gridy = 2;
        formPanel.add(cbStaffRole, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        cbStatus = new JComboBox<>(Status.values());
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(cbStatus, gbc);

        // --- ACCOUNT PANEL (Only for creation) ---
        JPanel accPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        accPanel.setBorder(BorderFactory.createTitledBorder("Cấp tài khoản (Dành cho Thêm mới)"));
        accPanel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        accPanel.add(txtUsername);
        accPanel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        accPanel.add(txtPassword);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        formPanel.add(accPanel, gbc);

        // --- BUTTONS ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnDelete = new JButton("Xóa");
        JButton btnClear = new JButton("Làm mới");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        // --- FILTER PANEL ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Lọc & Tìm kiếm"));
        txtSearch = new JTextField(15);
        cbFilterStaffRole = new JComboBox<>(new String[] { "Tất cả", "Consultant", "Accountant", "Manager", "Other" });
        cbFilterStatus = new JComboBox<>(new String[] { "Tất cả", "Active", "Inactive", "Suspended" });

        filterPanel.add(new JLabel("Tìm kiếm (Tên/SĐT):"));
        filterPanel.add(txtSearch);
        filterPanel.add(new JLabel("Vai trò:"));
        filterPanel.add(cbFilterStaffRole);
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(cbFilterStatus);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                loadStaffData();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                loadStaffData();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                loadStaffData();
            }
        });
        cbFilterStaffRole.addActionListener(e -> loadStaffData());
        cbFilterStatus.addActionListener(e -> loadStaffData());

        // Top Wrapper
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);

        JPanel southOfTop = new JPanel(new BorderLayout());
        southOfTop.add(btnPanel, BorderLayout.NORTH);
        southOfTop.add(filterPanel, BorderLayout.SOUTH);
        topPanel.add(southOfTop, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // --- TABLE ---
        staffTableModel = new DefaultTableModel(
                new Object[] { "ID", "Họ tên", "SĐT", "Email", "Q.TK", "Vai trò", "Trạng thái", "Tài khoản" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblStaffs = new JTable(staffTableModel);
        tblStaffs.getSelectionModel().addListSelectionListener(e -> fillForm());
        panel.add(new JScrollPane(tblStaffs), BorderLayout.CENTER);

        // --- LISTENERS ---
        btnAdd.addActionListener(e -> addStaff());
        btnUpdate.addActionListener(e -> updateStaff());
        btnDelete.addActionListener(e -> deleteStaff());
        btnClear.addActionListener(e -> clearForm());

        return panel;
    }

    private void loadStaffData() {
        try {
            staffTableModel.setRowCount(0);
            String keyword = txtSearch != null ? txtSearch.getText() : "";
            StaffRole r = null;
            if (cbFilterStaffRole != null && cbFilterStaffRole.getSelectedIndex() > 0) {
                r = StaffRole.valueOf(cbFilterStaffRole.getSelectedItem().toString());
            }
            Status st = null;
            if (cbFilterStatus != null && cbFilterStatus.getSelectedIndex() > 0) {
                st = Status.valueOf(cbFilterStatus.getSelectedItem().toString());
            }

            List<Staff> list = staffService.filterStaffs(keyword, r, st);
            for (Staff s : list) {
                String accountStr = s.getUserAccount() != null ? s.getUserAccount().getUsername() : "Chưa có";
                String userRole = s.getUserAccount() != null ? s.getUserAccount().getRole().name() : "N/A";
                staffTableModel.addRow(new Object[] {
                        s.getStaffId(), s.getFullName(), s.getPhone(), s.getEmail(),
                        userRole, s.getStaffRole().name(), s.getStatus().name(), accountStr
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu nhân viên: " + ex.getMessage());
        }
    }

    private void fillForm() {
        int r = tblStaffs.getSelectedRow();
        if (r >= 0) {
            txtStaffId.setText(tblStaffs.getValueAt(r, 0).toString());
            txtFullName.setText(tblStaffs.getValueAt(r, 1) != null ? tblStaffs.getValueAt(r, 1).toString() : "");
            txtPhone.setText(tblStaffs.getValueAt(r, 2) != null ? tblStaffs.getValueAt(r, 2).toString() : "");
            txtEmail.setText(tblStaffs.getValueAt(r, 3) != null ? tblStaffs.getValueAt(r, 3).toString() : "");
            // userRole should be matched if possible, but let's just pick from what is in
            // table
            String userRoleStr = tblStaffs.getValueAt(r, 4).toString();
            if (!userRoleStr.equals("N/A")) {
                cbUserRole.setSelectedItem(Role.valueOf(userRoleStr));
            }
            cbStaffRole.setSelectedItem(StaffRole.valueOf(tblStaffs.getValueAt(r, 5).toString()));
            cbStatus.setSelectedItem(Status.valueOf(tblStaffs.getValueAt(r, 6).toString()));

            // Disable username/password input for update
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
        }
    }

    private void clearForm() {
        txtStaffId.setText("");
        txtFullName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        cbUserRole.setSelectedIndex(0);
        cbStaffRole.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);

        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.setEnabled(true);
        txtPassword.setEnabled(true);

        tblStaffs.clearSelection();
    }

    private void addStaff() {
        try {
            if (txtFullName.getText().isBlank())
                throw new Exception("Họ tên không được trống");
            String uname = txtUsername.getText();
            String pwd = new String(txtPassword.getPassword());
            if (uname.isBlank() || pwd.isBlank())
                throw new Exception("Vui lòng nhập Username và Password");

            Staff staff = new Staff();
            staff.setFullName(txtFullName.getText().trim());
            staff.setPhone(txtPhone.getText().trim());
            staff.setEmail(txtEmail.getText().trim());
            staff.setStaffRole((StaffRole) cbStaffRole.getSelectedItem());
            staff.setStatus((Status) cbStatus.getSelectedItem());

            staffService.createStaffAccount(staff, uname, pwd, (Role) cbUserRole.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Thêm nhân viên và cấp tài khoản thành công!");
            clearForm();
            loadStaffData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStaff() {
        try {
            if (txtStaffId.getText().isBlank())
                throw new Exception("Vui lòng chọn nhân viên để cập nhật!");
            Long id = Long.parseLong(txtStaffId.getText());

            // Since we don't have getById right now, just fetch all and filter
            Staff staff = staffService.getAllStaffs().stream()
                    .filter(s -> s.getStaffId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new Exception("Không tìm thấy nhân viên"));

            staff.setFullName(txtFullName.getText().trim());
            staff.setPhone(txtPhone.getText().trim());
            staff.setEmail(txtEmail.getText().trim());
            staff.setStaffRole((StaffRole) cbStaffRole.getSelectedItem());
            staff.setStatus((Status) cbStatus.getSelectedItem());
            // Role update might need separate handling, skipping for update or only update
            // StaffRole.
            staff.setStatus((Status) cbStatus.getSelectedItem());

            staffService.updateStaff(staff);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            clearForm();
            loadStaffData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStaff() {
        try {
            if (txtStaffId.getText().isBlank())
                throw new Exception("Vui lòng chọn nhân viên để xóa!");
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có trê chắn muốn xóa nhân viên này chưa?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Long id = Long.parseLong(txtStaffId.getText());
                staffService.deleteStaff(id);
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                clearForm();
                loadStaffData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

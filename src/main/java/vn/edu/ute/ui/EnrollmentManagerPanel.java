package vn.edu.ute.ui;

import vn.edu.ute.enumeration.EnrollmentStatus;
import vn.edu.ute.model.Clas;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.service.EnrollmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EnrollmentManagerPanel extends JPanel {
    private final EnrollmentService service;

    private JTextField txtStudentId;
    private JButton btnFindClasses, btnEnroll, btnCancelEnrollment, btnRefresh;
    private JComboBox<ClasItem> cbSuggestedClasses;
    private JTable table;
    private DefaultTableModel tableModel;

    public EnrollmentManagerPanel(EnrollmentService service) {
        this.service = service;
        initUI();
        initEvents();
        loadDataToTable(); // Tải danh sách khi mở form
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 1. PANEL FORM (Nhập liệu & Tư vấn)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Tư Vấn & Ghi Danh Mới"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtStudentId = new JTextField(10);
        btnFindClasses = new JButton("Tìm Lớp Phù Hợp");
        cbSuggestedClasses = new JComboBox<>();
        btnEnroll = new JButton("Ghi Danh Vào Lớp Này");
        btnEnroll.setEnabled(false); // Vô hiệu hóa đến khi chọn được lớp

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Mã Học Viên (ID):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtStudentId, gbc);
        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(btnFindClasses, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Gợi Ý Lớp:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(cbSuggestedClasses, gbc);
        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(btnEnroll, gbc);

        // 2. PANEL BUTTONS (Hành động trên JTable)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnCancelEnrollment = new JButton("Chuyển Trạng Thái -> Đã Hủy");
        btnRefresh = new JButton("Làm Mới Bảng");
        buttonPanel.add(btnCancelEnrollment);
        buttonPanel.add(btnRefresh);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 3. PANEL TABLE (Hiển thị danh sách Ghi danh)
        String[] columns = {"ID Ghi Danh", "Mã HV", "Tên Học Viên", "ID Lớp", "Ngày Ghi Danh", "Trạng Thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initEvents() {
        // Nút Gợi ý lớp học
        btnFindClasses.addActionListener(e -> {
            try {
                if (txtStudentId.getText().isEmpty()) throw new Exception("Vui lòng nhập Mã Học Viên!");
                Long studentId = Long.parseLong(txtStudentId.getText().trim());

                List<Clas> classes = service.getSuggestedClasses(studentId);
                cbSuggestedClasses.removeAllItems();

                if (classes.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không có lớp nào phù hợp hoặc còn chỗ!");
                    btnEnroll.setEnabled(false);
                } else {
                    for (Clas c : classes) {
                        cbSuggestedClasses.addItem(new ClasItem(c.getClassId(), c.getClassId() + " - " + c.getCourse().getCourseName()));
                    }
                    btnEnroll.setEnabled(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Tiến hành Ghi danh
        btnEnroll.addActionListener(e -> {
            try {
                Long studentId = Long.parseLong(txtStudentId.getText().trim());
                ClasItem selected = (ClasItem) cbSuggestedClasses.getSelectedItem();

                if (selected != null) {
                    service.enrollStudent(studentId, selected.getId());
                    JOptionPane.showMessageDialog(this, "Ghi danh thành công!");
                    loadDataToTable(); // Cập nhật lại bảng

                    // Xóa form
                    txtStudentId.setText("");
                    cbSuggestedClasses.removeAllItems();
                    btnEnroll.setEnabled(false);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Ghi Danh", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Hủy ghi danh (Drop)
        btnCancelEnrollment.addActionListener(e -> {
            try {
                int row = table.getSelectedRow();
                if (row == -1) throw new Exception("Vui lòng chọn 1 dòng ghi danh trên bảng để hủy!");

                Long enrollmentId = Long.parseLong(tableModel.getValueAt(row, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(this, "Hủy ghi danh học viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Giả sử Enum của bạn có trạng thái DROPPED hoặc CANCELED
                    // Thay đổi Enum này tùy vào thiết kế của bạn nhé
                    service.updateEnrollmentStatus(enrollmentId, EnrollmentStatus.Dropped);
                    JOptionPane.showMessageDialog(this, "Đã cập nhật trạng thái thành công!");
                    loadDataToTable();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Làm mới
        btnRefresh.addActionListener(e -> loadDataToTable());
    }

    private void loadDataToTable() {
        try {
            List<Enrollment> list = service.getAllEnrollments();
            tableModel.setRowCount(0);
            for (Enrollment e : list) {
                Object[] row = {
                        e.getEnrollmentId(),
                        e.getStudent().getStudentId(),
                        e.getStudent().getFullName(), // Giả sử model Student có getFullName()
                        e.getClas().getClassId(),
                        e.getEnrollmentDate(),
                        e.getStatus()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    // Class Wrapper để hiển thị ComboBox đẹp hơn
    private static class ClasItem {
        private final Long id;
        private final String name;
        public ClasItem(Long id, String name) { this.id = id; this.name = name; }
        public Long getId() { return id; }
        @Override public String toString() { return name; }
    }
}
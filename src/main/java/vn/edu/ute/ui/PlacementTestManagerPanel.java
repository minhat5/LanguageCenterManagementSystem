package vn.edu.ute.ui;

import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.service.EnrollmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class PlacementTestManagerPanel extends JPanel {
    private final EnrollmentService service;

    private JTextField txtTestId, txtStudentId, txtScore;
    private JTextArea txtNote;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public PlacementTestManagerPanel(EnrollmentService service) {
        this.service = service;
        initUI();
        initEvents();
        loadDataToTable(); // Tải dữ liệu khi vừa mở form
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 1. PANEL FORM (Nhập liệu)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Bài Thi Đầu Vào"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTestId = new JTextField(10);
        txtTestId.setEditable(false); // ID tự tăng, không cho sửa
        txtStudentId = new JTextField(10);
        txtScore = new JTextField(10);
        txtNote = new JTextArea(2, 20);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID Bài Thi:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtTestId, gbc);

        gbc.gridx = 2; gbc.gridy = 0; formPanel.add(new JLabel("Mã Học Viên:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; formPanel.add(txtStudentId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Điểm Test:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtScore, gbc);

        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Ghi Chú:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(new JScrollPane(txtNote), gbc);

        // 2. PANEL BUTTONS (Hành động)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Thêm Mới");
        btnUpdate = new JButton("Cập Nhật");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm Mới Bảng");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 3. PANEL TABLE (Hiển thị)
        String[] columns = {"ID Bài Thi", "Mã HV", "Ngày Thi", "Điểm Số", "Trình Độ", "Ghi Chú"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Không cho sửa trực tiếp trên bảng
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initEvents() {
        // Bấm vào 1 dòng trên bảng -> đổ dữ liệu lên Form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtTestId.setText(tableModel.getValueAt(row, 0).toString());
                txtStudentId.setText(tableModel.getValueAt(row, 1).toString());
                txtScore.setText(tableModel.getValueAt(row, 3).toString());
                Object note = tableModel.getValueAt(row, 5);
                txtNote.setText(note != null ? note.toString() : "");
            }
        });

        // Nút Thêm Mới
        btnAdd.addActionListener(e -> {
            try {
                Long studentId = Long.parseLong(txtStudentId.getText().trim());
                BigDecimal score = new BigDecimal(txtScore.getText().trim());
                service.submitPlacementTest(studentId, score, txtNote.getText().trim());
                JOptionPane.showMessageDialog(this, "Thêm mới thành công!");
                loadDataToTable();
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Cập Nhật
        btnUpdate.addActionListener(e -> {
            try {
                if (txtTestId.getText().isEmpty()) throw new Exception("Vui lòng chọn bài thi cần sửa!");
                Long testId = Long.parseLong(txtTestId.getText().trim());
                BigDecimal score = new BigDecimal(txtScore.getText().trim());
                service.updatePlacementTest(testId, score, txtNote.getText().trim());
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadDataToTable();
                clearForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Xóa
        btnDelete.addActionListener(e -> {
            try {
                if (txtTestId.getText().isEmpty()) throw new Exception("Vui lòng chọn bài thi cần xóa!");
                int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Long testId = Long.parseLong(txtTestId.getText().trim());
                    service.deletePlacementTest(testId);
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadDataToTable();
                    clearForm();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Làm mới
        btnRefresh.addActionListener(e -> {
            loadDataToTable();
            clearForm();
        });
    }

    private void loadDataToTable() {
        try {
            List<PlacementTest> list = service.getAllPlacementTests();
            tableModel.setRowCount(0); // Xóa dữ liệu cũ
            for (PlacementTest p : list) {
                Object[] row = {
                        p.getTestId(),
                        p.getStudent().getStudentId(),
                        p.getTestDate(),
                        p.getScore(),
                        p.getSuggestedLevel(),
                        p.getNote()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtTestId.setText("");
        txtStudentId.setText("");
        txtScore.setText("");
        txtNote.setText("");
        table.clearSelection();
    }
}
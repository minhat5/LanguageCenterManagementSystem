package vn.edu.ute.ui;

import vn.edu.ute.model.Clas;
import vn.edu.ute.service.EnrollmentService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class EnrollmentManagerPanel extends JPanel {

    private final EnrollmentService service; // Giao tiếp qua Interface

    private JTextField txtStudentId, txtScore;
    private JButton btnSubmitTest, btnFindClasses, btnEnroll;
    private JComboBox<String> cbSuggestedClasses;
    private List<Clas> currentSuggestedClasses;

    public EnrollmentManagerPanel(EnrollmentService service) {
        this.service = service;
        initUI();
        initEvents();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // --- Panel Nhập Điểm Test (Phía Trên) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("1. Đánh Giá Năng Lực"));

        txtStudentId = new JTextField(10);
        txtScore = new JTextField(5);
        btnSubmitTest = new JButton("Lưu Điểm Test");

        topPanel.add(new JLabel("Mã HV (ID):"));
        topPanel.add(txtStudentId);
        topPanel.add(new JLabel("Điểm Test:"));
        topPanel.add(txtScore);
        topPanel.add(btnSubmitTest);

        // --- Panel Ghi Danh (Ở Giữa) ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("2. Tư Vấn & Ghi Danh"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        btnFindClasses = new JButton("Gợi Ý Lớp Học");
        cbSuggestedClasses = new JComboBox<>();
        btnEnroll = new JButton("Ghi Danh Vào Lớp Này");
        btnEnroll.setEnabled(false);

        gbc.gridx = 0; gbc.gridy = 0; centerPanel.add(btnFindClasses, gbc);
        gbc.gridx = 1; gbc.gridy = 0; centerPanel.add(cbSuggestedClasses, gbc);
        gbc.gridx = 2; gbc.gridy = 0; centerPanel.add(btnEnroll, gbc);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void initEvents() {
        // Nút Lưu điểm
        btnSubmitTest.addActionListener(e -> {
            try {
                Long studentId = Long.parseLong(txtStudentId.getText().trim());
                BigDecimal score = new BigDecimal(txtScore.getText().trim());
                service.submitPlacementTest(studentId, score, "Đã test đầu vào");
                JOptionPane.showMessageDialog(this, "Lưu điểm thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Tìm lớp gợi ý
        btnFindClasses.addActionListener(e -> {
            try {
                Long studentId = Long.parseLong(txtStudentId.getText().trim());
                currentSuggestedClasses = service.getSuggestedClasses(studentId);

                cbSuggestedClasses.removeAllItems();
                if(currentSuggestedClasses.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không có lớp nào phù hợp mở lúc này.");
                    btnEnroll.setEnabled(false);
                } else {
                    for (Clas c : currentSuggestedClasses) {
                        cbSuggestedClasses.addItem(c.getClassId() + " - " + c.getClassName());
                    }
                    btnEnroll.setEnabled(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Nút Ghi danh
        btnEnroll.addActionListener(e -> {
            try {
                Long studentId = Long.parseLong(txtStudentId.getText().trim());
                int selectedIndex = cbSuggestedClasses.getSelectedIndex();
                if (selectedIndex >= 0) {
                    Long classId = currentSuggestedClasses.get(selectedIndex).getClassId();
                    service.enrollStudent(studentId, classId);
                    JOptionPane.showMessageDialog(this, "Ghi danh thành công!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Ghi Danh", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
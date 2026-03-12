package vn.edu.ute.ui.enrollment;

import vn.edu.ute.common.enumeration.EnrollmentStatus;
import vn.edu.ute.model.Clas;
import vn.edu.ute.model.Enrollment;
import vn.edu.ute.service.EnrollmentService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnrollmentPanel extends JPanel {
    private final EnrollmentService service;

    private final EnrollmentTableModel tableModel = new EnrollmentTableModel();
    private final JTable table = new JTable(tableModel);

    private final JComboBox<Object> cboFilterStatus = new JComboBox<>();
    private final JLabel lblSelected = new JLabel("Đã chọn: (Không)");
    private Enrollment selectedEnrollment = null;
    private List<Enrollment> allEnrollments = new ArrayList<>();

    public EnrollmentPanel(EnrollmentService service) {
        this.service = service;
        buildUI();
        loadComboBoxes();
        reloadAll();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout(8, 8));

        // --- LEFT: Bộ lọc & Tìm kiếm ---
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(new JLabel("Trạng thái:"));
        left.add(cboFilterStatus);
        cboFilterStatus.addActionListener(e -> refreshTableByCurrentFilter());

        JTextField searchField = new JTextField(25);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshTableByCurrentFilter(searchField.getText()); }
            @Override public void removeUpdate(DocumentEvent e) { refreshTableByCurrentFilter(searchField.getText()); }
            @Override public void changedUpdate(DocumentEvent e) { refreshTableByCurrentFilter(searchField.getText()); }
        });
        left.add(new JLabel("Tìm kiếm (Mã HV):"));
        left.add(searchField);
        top.add(left, BorderLayout.WEST);

        // --- RIGHT: Nút hành động ---
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Ghi danh (Thêm)");
        JButton btnEdit = new JButton("Đổi trạng thái (Sửa)");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnRefresh.addActionListener(e -> reloadAll());

        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnRefresh);
        top.add(right, BorderLayout.EAST);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::onRowSelected);
        JScrollPane scroll = new JScrollPane(table);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);
    }

    private void loadComboBoxes() {
        cboFilterStatus.addItem("Tất cả");
        for (EnrollmentStatus s : EnrollmentStatus.values()) {
            cboFilterStatus.addItem(s);
        }
    }

    private void reloadAll() {
        try {
            allEnrollments = service.getAllEnrollments();
            refreshTableByCurrentFilter("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTableByCurrentFilter(String keyword) {
        Object statusObj = cboFilterStatus.getSelectedItem();
        List<Enrollment> filtered = allEnrollments;

        if (statusObj instanceof EnrollmentStatus) {
            EnrollmentStatus statusFilter = (EnrollmentStatus) statusObj;
            filtered = filtered.stream().filter(e -> e.getStatus() == statusFilter).collect(Collectors.toList());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            filtered = filtered.stream()
                    .filter(e -> String.valueOf(e.getStudent().getStudentId()).contains(keyword.trim()))
                    .collect(Collectors.toList());
        }

        tableModel.setData(filtered);
        lblSelected.setText("Đã chọn: (Không)");
        selectedEnrollment = null;
    }

    private void refreshTableByCurrentFilter() {
        refreshTableByCurrentFilter(null);
    }

    private void onRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedEnrollment = tableModel.getAt(row);
            lblSelected.setText("Đã chọn: Mã GD: " + selectedEnrollment.getEnrollmentId() + " | HV: " + selectedEnrollment.getStudent().getStudentId());
        } else {
            lblSelected.setText("Đã chọn: (Không)");
            selectedEnrollment = null;
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EnrollmentDialog dlg = new EnrollmentDialog(parent, service);
        dlg.setVisible(true);
        if (dlg.isSaved()) reloadAll();
    }

    private void onEdit() {
        if (selectedEnrollment == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng ghi danh để cập nhật.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Gọi Dialog cập nhật
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        EnrollmentEditDialog dlg = new EnrollmentEditDialog(parent, selectedEnrollment, service);
        dlg.setVisible(true);

        if (dlg.isSaved()) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật Ghi danh thành công!");
            reloadAll();
        }
    }

    // ==========================================
    // INNER CLASSES TÁCH THEO CHUẨN CỦA BẠN
    // ==========================================

    static class EnrollmentTableModel extends AbstractTableModel {
        private final String[] columns = {"Mã GD", "Mã HV", "Lớp (Mã Lớp)", "Ngày GD", "Trạng Thái", "Kết Quả"};
        private List<Enrollment> data = new ArrayList<>();

        public void setData(List<Enrollment> data) { this.data = data; fireTableDataChanged(); }
        public Enrollment getAt(int row) { return data.get(row); }
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Enrollment e = data.get(rowIndex);
            switch (columnIndex) {
                case 0: return e.getEnrollmentId();
                case 1: return e.getStudent().getStudentId();
                case 2: return e.getClas().getClassName() + " (" + e.getClas().getClassId() + ")";
                case 3: return e.getEnrollmentDate();
                case 4: return e.getStatus();
                case 5: return e.getResult();
                default: return "";
            }
        }
    }

    class EnrollmentDialog extends JDialog {
        private final JTextField txtStudentId = new JTextField(10);
        private final JComboBox<ClasItem> cboClasses = new JComboBox<>();
        private final JButton btnFind = new JButton("Tìm Lớp");
        private boolean saved = false;

        public EnrollmentDialog(Frame owner, EnrollmentService service) {
            super(owner, "Tư vấn & Ghi danh mới", true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;

            g.gridx = 0; g.gridy = 0; form.add(new JLabel("Mã Học Viên:"), g);
            g.gridx = 1; form.add(txtStudentId, g);
            g.gridx = 2; form.add(btnFind, g);

            g.gridx = 0; g.gridy = 1; form.add(new JLabel("Lớp Phù Hợp:"), g);
            g.gridx = 1; g.gridwidth = 2; g.fill = GridBagConstraints.HORIZONTAL;
            form.add(cboClasses, g);

            btnFind.addActionListener(e -> {
                try {
                    Long sId = Long.parseLong(txtStudentId.getText().trim());
                    List<Clas> classes = service.getSuggestedClasses(sId);
                    cboClasses.removeAllItems();
                    if (classes.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Không có lớp phù hợp hoặc còn chỗ.");
                    } else {
                        classes.forEach(c -> cboClasses.addItem(new ClasItem(c.getClassId(), c.getClassName())));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });

            JButton btnSave = new JButton("Ghi Danh");
            JButton btnCancel = new JButton("Huỷ");
            btnSave.addActionListener(e -> {
                try {
                    Long sId = Long.parseLong(txtStudentId.getText().trim());
                    ClasItem selected = (ClasItem) cboClasses.getSelectedItem();
                    if (selected == null) throw new Exception("Vui lòng tìm và chọn lớp.");
                    service.enrollStudent(sId, selected.getId());
                    saved = true; dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });
            btnCancel.addActionListener(e -> dispose());

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            actions.add(btnSave); actions.add(btnCancel);

            setLayout(new BorderLayout(10, 10));
            ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(form, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(owner);
        }
        public boolean isSaved() { return saved; }
    }

    // ==========================================
    // DIALOG SỬA GHI DANH
    // ==========================================
    class EnrollmentEditDialog extends JDialog {
        private final JComboBox<ClasItem> cboClasses = new JComboBox<>();
        private final JComboBox<EnrollmentStatus> cboStatus = new JComboBox<>();
        private final JComboBox<vn.edu.ute.common.enumeration.Result> cboResult = new JComboBox<>();
        private boolean saved = false;

        public EnrollmentEditDialog(Frame owner, Enrollment existing, EnrollmentService service) {
            super(owner, "Cập Nhật Ghi Danh", true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;

            g.gridx = 0; g.gridy = 0; form.add(new JLabel("Học viên:"), g);
            g.gridx = 1; form.add(new JLabel(existing.getStudent().getStudentId() + " - " + existing.getStudent().getFullName()), g);

            g.gridx = 0; g.gridy = 1; form.add(new JLabel("Lớp học:"), g);
            g.gridx = 1; form.add(cboClasses, g);

            g.gridx = 0; g.gridy = 2; form.add(new JLabel("Trạng thái:"), g);
            g.gridx = 1; form.add(cboStatus, g);

            g.gridx = 0; g.gridy = 3; form.add(new JLabel("Kết quả:"), g);
            g.gridx = 1; form.add(cboResult, g);

            // Tải dữ liệu vào ComboBox
            try {
                // Tải Lớp học
                List<Clas> classes = service.getAllClasses();
                for (int i = 0; i < classes.size(); i++) {
                    Clas c = classes.get(i);
                    ClasItem item = new ClasItem(c.getClassId(), c.getClassId() + " - " + c.getClassName());
                    cboClasses.addItem(item);

                    // Chọn sẵn lớp hiện tại của Học viên
                    if (existing.getClas().getClassId().equals(c.getClassId())) {
                        cboClasses.setSelectedIndex(i);
                    }
                }

                // Tải Trạng thái
                for (EnrollmentStatus s : EnrollmentStatus.values()) {
                    cboStatus.addItem(s);
                }
                cboStatus.setSelectedItem(existing.getStatus());

                // Tải Kết quả (Result)
                for (vn.edu.ute.common.enumeration.Result r : vn.edu.ute.common.enumeration.Result.values()) {
                    cboResult.addItem(r);
                }
                cboResult.setSelectedItem(existing.getResult());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

            JButton btnSave = new JButton("Lưu Cập Nhật");
            JButton btnCancel = new JButton("Huỷ");

            btnSave.addActionListener(e -> {
                try {
                    ClasItem selectedClass = (ClasItem) cboClasses.getSelectedItem();
                    EnrollmentStatus newStatus = (EnrollmentStatus) cboStatus.getSelectedItem();
                    vn.edu.ute.common.enumeration.Result newResult = (vn.edu.ute.common.enumeration.Result) cboResult.getSelectedItem();

                    if (selectedClass == null) throw new Exception("Vui lòng chọn lớp học!");

                    service.updateEnrollment(existing.getEnrollmentId(), selectedClass.getId(), newStatus, newResult);
                    saved = true;
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnCancel.addActionListener(e -> dispose());

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            actions.add(btnSave); actions.add(btnCancel);

            setLayout(new BorderLayout(10, 10));
            ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            add(form, BorderLayout.CENTER);
            add(actions, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(owner);
        }

        public boolean isSaved() { return saved; }
    }
    static class ClasItem {
        private final Long id; private final String name;
        public ClasItem(Long id, String name) { this.id = id; this.name = name; }
        public Long getId() { return id; }
        @Override public String toString() { return id + " - " + name; }
    }
}
package vn.edu.ute.ui.plamentTest;

import vn.edu.ute.model.PlacementTest;
import vn.edu.ute.service.EnrollmentService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlacementTestPanel extends JPanel {
    private final EnrollmentService service;

    private final PlacementTestTableModel tableModel = new PlacementTestTableModel();
    private final JTable table = new JTable(tableModel);

    private final JLabel lblSelected = new JLabel("Đã chọn: (Không)");
    private PlacementTest selectedTest = null;
    private List<PlacementTest> allTests = new ArrayList<>();

    public PlacementTestPanel(EnrollmentService service) {
        this.service = service;
        buildUI();
        reloadAll();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout(8, 8));

        // --- LEFT: Tìm kiếm ---
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(25);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { performSearch(); }
            @Override public void removeUpdate(DocumentEvent e) { performSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { performSearch(); }

            private void performSearch() {
                String keyword = searchField.getText().trim().toLowerCase();
                if (keyword.isEmpty()) {
                    tableModel.setData(allTests);
                    return;
                }
                List<PlacementTest> filtered = allTests.stream()
                        .filter(p -> String.valueOf(p.getStudent().getStudentId()).contains(keyword)
                                || (p.getNote() != null && p.getNote().toLowerCase().contains(keyword)))
                        .collect(Collectors.toList());
                tableModel.setData(filtered);
                lblSelected.setText("Đã chọn: (Không)");
            }
        });
        left.add(new JLabel("Tìm kiếm (Mã HV/Ghi chú):"));
        left.add(searchField);
        top.add(left, BorderLayout.WEST);

        // --- RIGHT: Các nút hành động ---
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xoá");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> reloadAll());

        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnRefresh);
        top.add(right, BorderLayout.EAST);

        // --- CENTER: Bảng dữ liệu ---
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::onRowSelected);
        JScrollPane scroll = new JScrollPane(table);

        // --- BOTTOM: Trạng thái ---
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(lblSelected, BorderLayout.WEST);

        // --- TỔNG HỢP ---
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(top, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        this.setLayout(new BorderLayout());
        this.add(root, BorderLayout.CENTER);
    }

    private void reloadAll() {
        try {
            allTests = service.getAllPlacementTests();
            tableModel.setData(allTests);
            lblSelected.setText("Đã chọn: (Không)");
            selectedTest = null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedTest = tableModel.getAt(row);
            lblSelected.setText("Đã chọn: Bài thi ID: " + selectedTest.getTestId() +
                    " | Học viên: " + selectedTest.getStudent().getStudentId() +
                    " - " + selectedTest.getStudent().getFullName());
        } else {
            selectedTest = null;
            lblSelected.setText("Đã chọn: (Không)");
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PlacementTestDialog dlg = new PlacementTestDialog(parent, "Thêm bài test đầu vào", null, service);
        dlg.setVisible(true);
        if (dlg.isSaved()) reloadAll();
    }

    private void onEdit() {
        if (selectedTest == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài thi để sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PlacementTestDialog dlg = new PlacementTestDialog(parent, "Sửa bài test đầu vào", selectedTest, service);
        dlg.setVisible(true);
        if (dlg.isSaved()) reloadAll();
    }

    private void onDelete() {
        if (selectedTest == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bài thi để xoá.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xoá bài thi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                service.deletePlacementTest(selectedTest.getTestId());
                JOptionPane.showMessageDialog(this, "Xoá thành công!");
                reloadAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xoá: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // INNER CLASSES TÁCH THEO CHUẨN CỦA BẠN
    // ==========================================

    static class PlacementTestTableModel extends AbstractTableModel {
        private final String[] columns = {"Mã Test", "Mã HV", "Tên Học Viên", "Ngày Thi", "Điểm", "Level Gợi Ý", "Ghi Chú"};
        private List<PlacementTest> data = new ArrayList<>();

        public void setData(List<PlacementTest> data) { this.data = data; fireTableDataChanged(); }
        public PlacementTest getAt(int row) { return data.get(row); }
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int col) { return columns[col]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            PlacementTest p = data.get(rowIndex);
            switch (columnIndex) {
                case 0: return p.getTestId();
                case 1: return p.getStudent().getStudentId();
                case 2: return p.getStudent().getFullName();
                case 3: return p.getTestDate();
                case 4: return p.getScore();
                case 5: return p.getSuggestedLevel();
                case 6: return p.getNote();
                default: return "";
            }
        }
    }

    class PlacementTestDialog extends JDialog {
        private final JTextField txtStudentId = new JTextField(10);
        private final JTextField txtScore = new JTextField(10);
        private final JTextArea txtNote = new JTextArea(3, 20);
        private boolean saved = false;

        public PlacementTestDialog(Frame owner, String title, PlacementTest existing, EnrollmentService service) {
            super(owner, title, true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel form = new JPanel(new GridBagLayout());
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(6, 6, 6, 6); g.anchor = GridBagConstraints.WEST;

            g.gridx = 0; g.gridy = 0; form.add(new JLabel("Mã Học Viên:"), g);
            g.gridx = 1; form.add(txtStudentId, g);

            g.gridx = 0; g.gridy = 1; form.add(new JLabel("Điểm Số:"), g);
            g.gridx = 1; form.add(txtScore, g);

            g.gridx = 0; g.gridy = 2; g.anchor = GridBagConstraints.NORTHWEST; form.add(new JLabel("Ghi Chú:"), g);
            g.gridx = 1; form.add(new JScrollPane(txtNote), g);

            if (existing != null) {
                txtStudentId.setText(String.valueOf(existing.getStudent().getStudentId()));
                txtStudentId.setEditable(false); // Sửa thì ko cho đổi HV
                txtScore.setText(existing.getScore().toPlainString());
                txtNote.setText(existing.getNote());
            }

            JButton btnSave = new JButton("Lưu");
            JButton btnCancel = new JButton("Huỷ");
            btnSave.addActionListener(e -> {
                try {
                    Long sId = Long.parseLong(txtStudentId.getText().trim());
                    BigDecimal score = new BigDecimal(txtScore.getText().trim());
                    if (existing == null) {
                        service.submitPlacementTest(sId, score, txtNote.getText().trim());
                    } else {
                        service.updatePlacementTest(existing.getTestId(), score, txtNote.getText().trim());
                    }
                    saved = true; dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
}
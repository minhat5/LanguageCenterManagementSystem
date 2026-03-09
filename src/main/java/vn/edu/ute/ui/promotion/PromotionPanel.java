package vn.edu.ute.ui.promotion;

import vn.edu.ute.enumeration.Status;
import vn.edu.ute.model.Promotion;
import vn.edu.ute.service.PromotionService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PromotionPanel extends JPanel {
    private final PromotionService promotionService;

    private final PromotionTableModel tableModel = new PromotionTableModel();
    private final JTable table = new JTable(tableModel);

    private final JComboBox<Object> cboFilterStatus = new JComboBox<>();
    private final JLabel lblSelected = new JLabel("Đã chọn: (Không)");

    private Promotion selectedPromotion = null;
    private List<Promotion> allPromotions = new ArrayList<>();
    private List<Promotion> filteredPromotions = new ArrayList<>();

    public PromotionPanel(PromotionService promotionService) {
        this.promotionService = promotionService;
        buildUI();
        loadComboBoxes();
        reloadAll();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout(8, 8));

        // --- LEFT (Lọc & Tìm kiếm) ---
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(new JLabel("Trạng thái:"));
        left.add(cboFilterStatus);

        JTextField searchField = new JTextField(25);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { performSearch(); }
            @Override public void removeUpdate(DocumentEvent e) { performSearch(); }
            @Override public void changedUpdate(DocumentEvent e) { performSearch(); }

            private void performSearch() {
                String keyword = searchField.getText().trim().toLowerCase();
                Object statusObj = cboFilterStatus.getSelectedItem();

                // DÙNG LAMBDA ĐỂ LỌC TRỰC TIẾP
                filteredPromotions = allPromotions.stream()
                        .filter(p -> {
                            boolean matchKeyword = keyword.isEmpty() || p.getPromoName().toLowerCase().contains(keyword);
                            boolean matchStatus = (statusObj instanceof String) || p.getStatus() == statusObj;
                            return matchKeyword && matchStatus;
                        })
                        .collect(Collectors.toList());

                tableModel.setData(filteredPromotions);
                lblSelected.setText("Đã chọn: (Không)");
                selectedPromotion = null;
            }
        });

        cboFilterStatus.addActionListener(e -> {
            // Cập nhật lại list khi đổi combo box bằng cách trigger nhẹ cái search field
            String currentText = searchField.getText();
            searchField.setText(currentText + " ");
            searchField.setText(currentText);
        });

        left.add(new JLabel("Tìm kiếm (Tên KM):"));
        left.add(searchField);
        top.add(left, BorderLayout.WEST);

        // --- RIGHT (Hành động) ---
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xoá");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> reloadAll());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnRefresh);
        top.add(right, BorderLayout.EAST);

        // --- BẢNG & TRẠNG THÁI ---
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

        this.setLayout(new BorderLayout());
        this.add(root, BorderLayout.CENTER);
    }

    private void loadComboBoxes() {
        cboFilterStatus.addItem("Tất cả");
        for (Status s : Status.values()) {
            cboFilterStatus.addItem(s);
        }
    }

    private void reloadAll() {
        try {
            allPromotions = promotionService.getAllPromotions();
            filteredPromotions = new ArrayList<>(allPromotions);
            tableModel.setData(filteredPromotions);
            lblSelected.setText("Đã chọn: (Không)");
            selectedPromotion = null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu KM: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedPromotion = tableModel.getAt(row);
            lblSelected.setText("Đã chọn: Mã KM: " + selectedPromotion.getPromotionId() + " | " + selectedPromotion.getPromoName());
        } else {
            lblSelected.setText("Đã chọn: (Không)");
            selectedPromotion = null;
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PromotionFormDialog dlg = new PromotionFormDialog(parent, "Thêm chương trình khuyến mãi", null);
        dlg.setVisible(true);

        if (!dlg.isSaved()) return;

        try {
            promotionService.addPromotion(dlg.getPromotion());
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        if (selectedPromotion == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi để chỉnh sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        PromotionFormDialog dlg = new PromotionFormDialog(parent, "Cập nhật khuyến mãi", selectedPromotion);
        dlg.setVisible(true);

        if (!dlg.isSaved()) return;

        try {
            promotionService.updatePromotion(dlg.getPromotion());
            JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        if (selectedPromotion == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi để xoá.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xoá khuyến mãi này?", "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            promotionService.deletePromotion(selectedPromotion.getPromotionId());
            JOptionPane.showMessageDialog(this, "Xoá thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xoá: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
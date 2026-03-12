package vn.edu.ute.ui.promotion;

import com.github.lgooddatepicker.components.DatePicker;
import vn.edu.ute.common.enumeration.DiscountType;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.model.Promotion;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class PromotionFormDialog extends JDialog {
    private final JTextField txtPromoName = new JTextField(25);
    private final JComboBox<DiscountType> cboDiscountType = new JComboBox<>();
    private final JTextField txtDiscountValue = new JTextField(15);
    private final DatePicker dateStart = new DatePicker();
    private final DatePicker dateEnd = new DatePicker();
    private final JComboBox<Status> cboStatus = new JComboBox<>();

    private boolean saved = false;
    private Promotion promotion;

    public PromotionFormDialog(Frame owner, String title, Promotion existing) {
        super(owner, title, true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        loadComboBoxes();
        buildUI();

        if (existing != null) {
            this.promotion = existing;
            txtPromoName.setText(existing.getPromoName());
            cboDiscountType.setSelectedItem(existing.getDiscountType());
            txtDiscountValue.setText(existing.getDiscountValue().toPlainString());
            dateStart.setDate(existing.getStartDate());
            dateEnd.setDate(existing.getEndDate());
            cboStatus.setSelectedItem(existing.getStatus());
        } else {
            this.promotion = new Promotion();
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void loadComboBoxes() {
        for (DiscountType type : DiscountType.values()) {
            cboDiscountType.addItem(type);
        }
        for (Status status : Status.values()) {
            cboStatus.addItem(status);
        }
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        int r = 0;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Tên khuyến mãi:"), g);
        g.gridx = 1; form.add(txtPromoName, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Loại giảm giá:"), g);
        g.gridx = 1; form.add(cboDiscountType, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Mức giảm:"), g);
        g.gridx = 1; form.add(txtDiscountValue, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Ngày bắt đầu:"), g);
        g.gridx = 1; form.add(dateStart, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Ngày kết thúc:"), g);
        g.gridx = 1; form.add(dateEnd, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Trạng thái:"), g);
        g.gridx = 1; form.add(cboStatus, g);

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Huỷ");
        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(btnSave);
        actions.add(btnCancel);

        getContentPane().setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    private void onSave() {
        try {
            String name = txtPromoName.getText().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Tên khuyến mãi không được để trống.");

            BigDecimal value;
            try {
                value = new BigDecimal(txtDiscountValue.getText().trim());
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new IllegalArgumentException("Mức giảm phải lớn hơn 0.");
                }
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Mức giảm phải là số hợp lệ.");
            }

            if (dateEnd.getDate() != null && dateStart.getDate() != null && dateEnd.getDate().isBefore(dateStart.getDate())) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
            }

            promotion.setPromoName(name);
            promotion.setDiscountType((DiscountType) cboDiscountType.getSelectedItem());
            promotion.setDiscountValue(value);
            promotion.setStartDate(dateStart.getDate());
            promotion.setEndDate(dateEnd.getDate());
            promotion.setStatus((Status) cboStatus.getSelectedItem());

            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public Promotion getPromotion() { return promotion; }
}
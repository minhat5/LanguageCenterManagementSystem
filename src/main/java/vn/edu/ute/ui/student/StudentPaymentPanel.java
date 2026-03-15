package vn.edu.ute.ui.student;

import vn.edu.ute.common.security.AuthContext;
import vn.edu.ute.common.enumeration.InvoiceStatus;
import vn.edu.ute.common.enumeration.PaymentMethod;
import vn.edu.ute.model.Invoice;
import vn.edu.ute.service.StudentPaymentService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudentPaymentPanel extends JPanel {
    private final StudentPaymentService paymentService;

    private final MyInvoiceTableModel tableModel = new MyInvoiceTableModel();
    private final JTable table = new JTable(tableModel);

    private JTextField txtPromoCode;
    private JComboBox<PaymentMethod> cboMethod;
    private JButton btnApplyPromo, btnPay;

    private Invoice selectedInvoice = null;

    public StudentPaymentPanel(StudentPaymentService paymentService) {
        this.paymentService = paymentService;
        buildUI();
        loadData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                selectedInvoice = tableModel.getAt(table.getSelectedRow());
                boolean isPaid = selectedInvoice.getStatus() == InvoiceStatus.Paid;

                btnApplyPromo.setEnabled(!isPaid && selectedInvoice.getPromotion() == null);
                btnPay.setEnabled(!isPaid);
                txtPromoCode.setEnabled(!isPaid && selectedInvoice.getPromotion() == null);
                cboMethod.setEnabled(!isPaid);
            }
        });

        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createTitledBorder("Công nợ & Hóa đơn của tôi"));
        pnlTable.add(new JScrollPane(table), BorderLayout.CENTER);

        // FORM NHẬP MÃ VÀ THANH TOÁN
        JPanel pnlAction = new JPanel(new GridBagLayout());
        pnlAction.setBorder(BorderFactory.createTitledBorder("Thanh Toán"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; pnlAction.add(new JLabel("Mã giảm giá:"), g);
        txtPromoCode = new JTextField(15);
        g.gridx = 1; pnlAction.add(txtPromoCode, g);

        btnApplyPromo = new JButton("Áp dụng mã");
        btnApplyPromo.setEnabled(false);
        btnApplyPromo.addActionListener(e -> onApplyPromo());
        g.gridx = 2; pnlAction.add(btnApplyPromo, g);

        g.gridx = 0; g.gridy = 1; pnlAction.add(new JLabel("Phương thức:"), g);
        cboMethod = new JComboBox<>(PaymentMethod.values());
        g.gridx = 1; pnlAction.add(cboMethod, g);

        btnPay = new JButton("Thanh Toán Ngay");
        btnPay.setEnabled(false);
        btnPay.setBackground(new Color(46, 204, 113));
        btnPay.setForeground(Color.WHITE);
        btnPay.addActionListener(e -> onPay());
        g.gridx = 2; pnlAction.add(btnPay, g);

        add(pnlTable, BorderLayout.CENTER);
        add(pnlAction, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            if (AuthContext.getCurrentUser() != null && AuthContext.getCurrentUser().getStudent() != null) {
                Long studentId = AuthContext.getCurrentUser().getStudent().getStudentId();
                tableModel.setData(paymentService.getMyInvoices(studentId));

                selectedInvoice = null;
                btnApplyPromo.setEnabled(false);
                btnPay.setEnabled(false);
                table.clearSelection();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void onApplyPromo() {
        try {
            if (selectedInvoice == null) return;
            String code = txtPromoCode.getText().trim();
            if (code.isEmpty()) throw new Exception("Vui lòng nhập mã!");

            paymentService.applyPromotion(selectedInvoice.getInvoiceId(), code);
            JOptionPane.showMessageDialog(this, "Áp dụng thành công! Tổng tiền đã được cập nhật.");
            txtPromoCode.setText("");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onPay() {
        try {
            if (selectedInvoice == null) return;
            PaymentMethod method = (PaymentMethod) cboMethod.getSelectedItem();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Xác nhận thanh toán số tiền " + String.format("%,.0f VNĐ", selectedInvoice.getTotalAmount()) + "?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                paymentService.payInvoice(selectedInvoice.getInvoiceId(), method);
                JOptionPane.showMessageDialog(this, "Thanh toán thành công! Xin cảm ơn.");
                loadData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    static class MyInvoiceTableModel extends AbstractTableModel {
        private final String[] cols = {"Mã HĐ", "Khuyến Mãi", "Tổng Tiền (VNĐ)", "Ngày Lập", "Trạng Thái"};
        private List<Invoice> data = new ArrayList<>();

        public void setData(List<Invoice> data) { this.data = data; fireTableDataChanged(); }
        public Invoice getAt(int row) { return data.get(row); }
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Object getValueAt(int r, int c) {
            Invoice i = data.get(r);
            switch (c) {
                case 0: return i.getInvoiceId();
                case 1: return i.getPromotion() != null ? i.getPromotion().getPromoName() : "-";
                case 2: return String.format("%,.0f", i.getTotalAmount());
                case 3: return i.getIssueDate();
                case 4: return i.getStatus();
                default: return "";
            }
        }
    }
}
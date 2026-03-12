package vn.edu.ute.ui.account;

import vn.edu.ute.common.enumeration.InvoiceStatus;
import vn.edu.ute.common.enumeration.PaymentMethod;
import vn.edu.ute.model.Invoice;
import vn.edu.ute.model.Payment;
import vn.edu.ute.service.AccountingService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountingPanel extends JPanel {
    private final AccountingService service;

    private final InvoiceTableModel invoiceTableModel = new InvoiceTableModel();
    private final PaymentTableModel paymentTableModel = new PaymentTableModel();

    private final JTable tblInvoice = new JTable(invoiceTableModel);
    private final JTable tblPayment = new JTable(paymentTableModel);

    private Invoice selectedInvoice = null;

    public AccountingPanel(AccountingService service) {
        this.service = service;
        buildUI();
        reloadAll();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- TOP: Các nút hành động ---
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCreateInvoice = new JButton("Tạo Hóa Đơn");
        JButton btnPay = new JButton("Thanh Toán Hóa Đơn");
        JButton btnRefresh = new JButton("Refresh");

        btnCreateInvoice.addActionListener(e -> onCreateInvoice());
        btnPay.addActionListener(e -> onPayInvoice());
        btnRefresh.addActionListener(e -> reloadAll());

        top.add(btnCreateInvoice);
        top.add(btnPay);
        top.add(btnRefresh);

        // --- CENTER: JTabbedPane chứa 2 bảng ---
        JTabbedPane tabbedPane = new JTabbedPane();

        tblInvoice.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblInvoice.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblInvoice.getSelectedRow() >= 0) {
                selectedInvoice = invoiceTableModel.getAt(tblInvoice.getSelectedRow());
            }
        });
        tabbedPane.addTab("Danh sách Hóa Đơn", new JScrollPane(tblInvoice));
        tabbedPane.addTab("Lịch sử Giao Dịch (Payments)", new JScrollPane(tblPayment));

        add(top, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void reloadAll() {
        try {
            invoiceTableModel.setData(service.getAllInvoices());
            paymentTableModel.setData(service.getAllPayments());
            selectedInvoice = null;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu kế toán: " + ex.getMessage());
        }
    }

    private void onCreateInvoice() {
        // Giao diện nhập liệu nhanh qua JOptionPane (Bạn có thể tách thành JDialog riêng sau)
        JTextField txtStudentId = new JTextField();
        JTextField txtEnrollmentId = new JTextField();
        JTextField txtPromotionId = new JTextField();
        JTextField txtNote = new JTextField();

        Object[] message = {
                "Mã Học Viên:", txtStudentId,
                "Mã Ghi Danh (Enrollment ID):", txtEnrollmentId,
                "Mã Khuyến Mãi (Để trống nếu ko có):", txtPromotionId,
                "Ghi chú:", txtNote
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Tạo Hóa Đơn Mới", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                Long sId = Long.parseLong(txtStudentId.getText().trim());
                Long eId = Long.parseLong(txtEnrollmentId.getText().trim());
                Long pId = txtPromotionId.getText().trim().isEmpty() ? null : Long.parseLong(txtPromotionId.getText().trim());

                service.generateInvoice(sId, eId, pId, txtNote.getText());
                JOptionPane.showMessageDialog(this, "Tạo hóa đơn thành công!");
                reloadAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onPayInvoice() {
        if (selectedInvoice == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 Hóa đơn để thanh toán!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedInvoice.getStatus() == InvoiceStatus.Paid) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã được thanh toán đủ!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextField txtAmount = new JTextField(selectedInvoice.getTotalAmount().toPlainString());
        JComboBox<PaymentMethod> cboMethod = new JComboBox<>(PaymentMethod.values());
        JTextField txtRefCode = new JTextField();

        Object[] message = {
                "Hóa đơn ID:", selectedInvoice.getInvoiceId().toString(),
                "Tổng tiền cần thu:", selectedInvoice.getTotalAmount().toPlainString() + " VNĐ",
                "Số tiền khách đưa:", txtAmount,
                "Phương thức:", cboMethod,
                "Mã giao dịch (Momo/Bank...):", txtRefCode
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Thanh Toán Hóa Đơn", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                BigDecimal amount = new BigDecimal(txtAmount.getText().trim());
                service.processPayment(selectedInvoice.getInvoiceId(), amount, (PaymentMethod) cboMethod.getSelectedItem(), txtRefCode.getText());
                JOptionPane.showMessageDialog(this, "Ghi nhận thanh toán thành công!");
                reloadAll();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi thanh toán: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- INNER CLASSES: Table Models ---
    static class InvoiceTableModel extends AbstractTableModel {
        private final String[] cols = {"Mã HĐ", "Mã HV", "Tên HV", "Tổng Tiền (VNĐ)", "Khuyến Mãi Áp Dụng", "Ngày Xuất", "Trạng Thái"};
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
                case 1: return i.getStudent().getStudentId();
                case 2: return i.getStudent().getFullName();
                case 3: return String.format("%,.0f", i.getTotalAmount());
                case 4: return i.getPromotion() != null ? i.getPromotion().getPromoName() : "Không";
                case 5: return i.getIssueDate();
                case 6: return i.getStatus();
                default: return "";
            }
        }
    }

    static class PaymentTableModel extends AbstractTableModel {
        private final String[] cols = {"Mã GD", "Mã HĐ", "Số Tiền Thu", "Hình Thức", "Mã Tham Chiếu", "Ngày GD", "Trạng Thái"};
        private List<Payment> data = new ArrayList<>();
        public void setData(List<Payment> data) { this.data = data; fireTableDataChanged(); }
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Object getValueAt(int r, int c) {
            Payment p = data.get(r);
            switch (c) {
                case 0: return p.getPaymentId();
                case 1: return p.getInvoice() != null ? p.getInvoice().getInvoiceId() : "";
                case 2: return String.format("%,.0f", p.getAmount());
                case 3: return p.getPaymentMethod();
                case 4: return p.getReferenceCode();
                case 5: return p.getPaymentDate();
                case 6: return p.getStatus();
                default: return "";
            }
        }
    }
}
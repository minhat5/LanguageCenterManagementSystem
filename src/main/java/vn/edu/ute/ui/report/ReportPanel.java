package vn.edu.ute.ui.report;

import vn.edu.ute.service.ReportService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class ReportPanel extends JPanel {
    private final ReportService reportService;

    // Các nhãn hiển thị số liệu tổng quan
    private JLabel lblTotalRevenue, lblPassRate;

    // Các bảng chi tiết
    private DefaultTableModel monthTableModel, methodTableModel, gradeTableModel;
    private JTable tblMonthRevenue, tblMethodRevenue, tblGradeStats;

    public ReportPanel(ReportService reportService) {
        this.reportService = reportService;
        buildUI();
        loadReportData();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. TOP: Thống kê nhanh (KPI Cards)
        JPanel pnlKPI = new JPanel(new GridLayout(1, 2, 20, 0));

        lblTotalRevenue = createKpiLabel("Tổng Doanh Thu Hệ Thống", "0 VNĐ", new Color(46, 204, 113));
        lblPassRate = createKpiLabel("Tỷ Lệ Học Viên Thi Đạt", "0%", new Color(52, 152, 219));

        pnlKPI.add(lblTotalRevenue);
        pnlKPI.add(lblPassRate);

        // 2. CENTER: Các bảng thống kê chi tiết
        JPanel pnlDetails = new JPanel(new GridLayout(1, 3, 15, 0));

        // Bảng Doanh thu theo tháng
        monthTableModel = new DefaultTableModel(new String[]{"Tháng", "Doanh Thu (VNĐ)"}, 0);
        tblMonthRevenue = new JTable(monthTableModel);
        pnlDetails.add(createTablePanel("Doanh Thu Theo Tháng (Năm nay)", tblMonthRevenue));

        // Bảng Doanh thu theo phương thức
        methodTableModel = new DefaultTableModel(new String[]{"Phương Thức", "Doanh Thu (VNĐ)"}, 0);
        tblMethodRevenue = new JTable(methodTableModel);
        pnlDetails.add(createTablePanel("D.Thu Theo Phương Thức", tblMethodRevenue));

        // Bảng Thống kê học lực
        gradeTableModel = new DefaultTableModel(new String[]{"Xếp Loại", "Số Lượng HV"}, 0);
        tblGradeStats = new JTable(gradeTableModel);
        pnlDetails.add(createTablePanel("Thống Kê Chất Lượng Học Tập", tblGradeStats));

        // 3. BOTTOM: Nút Làm mới
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Cập Nhật Dữ Liệu Báo Cáo");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 14));
        btnRefresh.addActionListener(e -> loadReportData());
        pnlBottom.add(btnRefresh);

        add(pnlKPI, BorderLayout.NORTH);
        add(pnlDetails, BorderLayout.CENTER);
        add(pnlBottom, BorderLayout.SOUTH);
    }

    private JLabel createKpiLabel(String title, String value, Color color) {
        JLabel label = new JLabel("<html><div style='text-align: center;'><span style='font-size:16px; color:gray;'>"
                + title + "</span><br><br><span style='font-size:28px; font-weight:bold; color:"
                + String.format("#%06x", color.getRGB() & 0x00FFFFFF) + ";'>" + value + "</span></div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        label.setPreferredSize(new Dimension(0, 100));
        return label;
    }

    private JPanel createTablePanel(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        table.setRowHeight(25);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void loadReportData() {
        try {
            // Cập nhật KPI
            lblTotalRevenue.setText(createKpiLabel("Tổng Doanh Thu Hệ Thống",
                    String.format("%,.0f VNĐ", reportService.getTotalRevenue()), new Color(46, 204, 113)).getText());
            lblPassRate.setText(createKpiLabel("Tỷ Lệ Học Viên Thi Đạt",
                    String.format("%.1f %%", reportService.getPassRate()), new Color(52, 152, 219)).getText());

            // Load Bảng Tháng
            monthTableModel.setRowCount(0);
            Map<String, BigDecimal> monthData = reportService.getRevenueByMonth(LocalDate.now().getYear());
            monthData.forEach((k, v) -> monthTableModel.addRow(new Object[]{k, String.format("%,.0f", v)}));

            // Load Bảng Phương thức thanh toán
            methodTableModel.setRowCount(0);
            reportService.getRevenueByPaymentMethod().forEach((k, v) ->
                    methodTableModel.addRow(new Object[]{k.name(), String.format("%,.0f", v)}));

            // Load Bảng Xếp loại
            gradeTableModel.setRowCount(0);
            reportService.getAcademicPerformanceStats().forEach((k, v) ->
                    gradeTableModel.addRow(new Object[]{k, v + " học viên"}));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi trích xuất báo cáo: " + ex.getMessage());
        }
    }
}
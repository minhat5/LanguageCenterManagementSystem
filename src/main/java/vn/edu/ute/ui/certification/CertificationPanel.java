package vn.edu.ute.ui.certification;

import com.github.lgooddatepicker.components.DatePicker;
import vn.edu.ute.model.Certificate;
import vn.edu.ute.model.Clas;
import vn.edu.ute.model.Result;
import vn.edu.ute.model.Student;
import vn.edu.ute.service.CertificationService;
import vn.edu.ute.service.ClasService;
import vn.edu.ute.service.StudentService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CertificationPanel extends JPanel {
    private final CertificationService certService;
    private final ClasService clasService;
    private final StudentService studentService;

    private final ResultTableModel resultTableModel = new ResultTableModel();
    private final CertTableModel certTableModel = new CertTableModel();

    private final JTable tblResult = new JTable(resultTableModel);
    private final JTable tblCert = new JTable(certTableModel);

    public CertificationPanel(CertificationService certService, ClasService clasService, StudentService studentService) {
        this.certService = certService;
        this.clasService = clasService;
        this.studentService = studentService;
        buildUI();
        reloadAll();
    }

    private void buildUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Nút hành động
        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAddResult = new JButton("Nhập Điểm Mới");
        JButton btnIssueCert = new JButton("Cấp Phát Chứng Chỉ");
        JButton btnRefresh = new JButton("Làm mới dữ liệu");

        btnAddResult.addActionListener(e -> onAddResult());
        btnIssueCert.addActionListener(e -> onIssueCert());
        btnRefresh.addActionListener(e -> reloadAll());

        top.add(btnAddResult);
        top.add(btnIssueCert);
        top.add(btnRefresh);

        // TabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("1. Quản lý Điểm số (Results)", new JScrollPane(tblResult));
        tabbedPane.addTab("2. Chứng chỉ đã cấp (Certificates)", new JScrollPane(tblCert));

        add(top, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void reloadAll() {
        try {
            resultTableModel.setData(certService.getAllResults());
            certTableModel.setData(certService.getAllCertificates());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void onAddResult() {
        try {
            // Load list for ComboBox
            List<Student> students = studentService.getAllStudents();
            List<Clas> classes = clasService.getAll();

            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            ResultFormDialog dlg = new ResultFormDialog(parent, students, classes);
            dlg.setVisible(true);

            if (dlg.isSaved()) {
                certService.saveResult(dlg.getResult());
                JOptionPane.showMessageDialog(this, "Lưu điểm thành công!");
                reloadAll();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onIssueCert() {
        try {
            // Tự động gọi hàm Lambda để lấy ra danh sách người Đậu
            List<Result> eligibleList = certService.getEligibleForCertificate();
            if (eligibleList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Hiện tại không có học viên nào đủ điều kiện cấp chứng chỉ mới!");
                return;
            }

            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            CertificateFormDialog dlg = new CertificateFormDialog(parent, eligibleList);
            dlg.setVisible(true);

            if (dlg.isSaved()) {
                certService.issueCertificate(dlg.getCertificate());
                JOptionPane.showMessageDialog(this, "Cấp phát chứng chỉ thành công!");
                reloadAll();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // INNER CLASSES: TABLE MODELS & DIALOGS
    // ==========================================
    static class ResultTableModel extends AbstractTableModel {
        private final String[] cols = {"Mã", "Mã HV", "Tên Học Viên", "Lớp", "Điểm", "Xếp Loại", "Nhận Xét"};
        private List<Result> data = new ArrayList<>();
        public void setData(List<Result> data) { this.data = data; fireTableDataChanged(); }
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Object getValueAt(int r, int c) {
            Result res = data.get(r);
            switch (c) {
                case 0: return res.getResultId();
                case 1: return res.getStudent().getStudentId();
                case 2: return res.getStudent().getFullName();
                case 3: return res.getClas().getClassName();
                case 4: return res.getScore();
                case 5: return res.getGrade();
                case 6: return res.getComment();
                default: return "";
            }
        }
    }

    static class CertTableModel extends AbstractTableModel {
        private final String[] cols = {"Mã CC", "Tên Học Viên", "Lớp", "Tên Chứng Chỉ", "Số Serial", "Ngày Cấp"};
        private List<Certificate> data = new ArrayList<>();
        public void setData(List<Certificate> data) { this.data = data; fireTableDataChanged(); }
        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Object getValueAt(int r, int c) {
            Certificate cert = data.get(r);
            switch (c) {
                case 0: return cert.getCertificateId();
                case 1: return cert.getStudent().getFullName();
                case 2: return cert.getClas().getClassName();
                case 3: return cert.getCertName();
                case 4: return cert.getSerialNo();
                case 5: return cert.getIssueDate();
                default: return "";
            }
        }
    }

    // --- FORM NHẬP ĐIỂM ---
    class ResultFormDialog extends JDialog {
        private final JComboBox<Student> cboStudent = new JComboBox<>();
        private final JComboBox<Clas> cboClas = new JComboBox<>();
        private final JTextField txtScore = new JTextField(10);
        private final JTextField txtComment = new JTextField(20);
        private boolean saved = false;
        private Result result;

        public ResultFormDialog(Frame owner, List<Student> students, List<Clas> classes) {
            super(owner, "Nhập Điểm Học Viên", true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            students.forEach(cboStudent::addItem);
            classes.forEach(cboClas::addItem);

            // Dùng Custom Renderer để hiển thị Tên trong ComboBox thay vì chuỗi Object ID
            cboStudent.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Student) setText(((Student)value).getStudentId() + " - " + ((Student)value).getFullName());
                    return this;
                }
            });
            cboClas.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Clas) setText(((Clas)value).getClassId() + " - " + ((Clas)value).getClassName());
                    return this;
                }
            });

            JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
            form.add(new JLabel("Học Viên:")); form.add(cboStudent);
            form.add(new JLabel("Lớp Học:")); form.add(cboClas);
            form.add(new JLabel("Điểm Số (Hệ 10):")); form.add(txtScore);
            form.add(new JLabel("Nhận Xét:")); form.add(txtComment);

            JButton btnSave = new JButton("Lưu");
            btnSave.addActionListener(e -> {
                try {
                    result = new Result();
                    result.setStudent((Student) cboStudent.getSelectedItem());
                    result.setClas((Clas) cboClas.getSelectedItem());
                    result.setScore(new BigDecimal(txtScore.getText().trim()));
                    result.setComment(txtComment.getText().trim());
                    saved = true; dispose();
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Điểm phải là số hợp lệ!"); }
            });

            setLayout(new BorderLayout());
            add(form, BorderLayout.CENTER);
            add(btnSave, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(owner);
        }
        public boolean isSaved() { return saved; }
        public Result getResult() { return result; }
    }

    // --- FORM CẤP CHỨNG CHỈ (CHỈ HIỂN THỊ NGƯỜI ĐỦ ĐIỀU KIỆN) ---
    class CertificateFormDialog extends JDialog {
        private final JComboBox<Result> cboEligible = new JComboBox<>();
        private final JTextField txtCertName = new JTextField(20);
        private final JTextField txtSerialNo = new JTextField(20);
        private final DatePicker dateIssue = new DatePicker();
        private boolean saved = false;
        private Certificate certificate;

        public CertificateFormDialog(Frame owner, List<Result> eligibleList) {
            super(owner, "Cấp Phát Chứng Chỉ", true);
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            eligibleList.forEach(cboEligible::addItem);
            cboEligible.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Result) {
                        Result r = (Result) value;
                        setText(r.getStudent().getFullName() + " | Lớp: " + r.getClas().getClassName() + " | Điểm: " + r.getScore());
                    }
                    return this;
                }
            });

            JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
            form.add(new JLabel("Học Viên Đạt Yêu Cầu:")); form.add(cboEligible);
            form.add(new JLabel("Tên Chứng Chỉ:")); form.add(txtCertName);
            form.add(new JLabel("Số Serial:")); form.add(txtSerialNo);
            form.add(new JLabel("Ngày Cấp:")); form.add(dateIssue);

            JButton btnSave = new JButton("Lưu");
            btnSave.addActionListener(e -> {
                try {
                    if (txtCertName.getText().isEmpty() || txtSerialNo.getText().isEmpty() || dateIssue.getDate() == null) {
                        throw new Exception("Vui lòng điền đầy đủ thông tin!");
                    }
                    Result r = (Result) cboEligible.getSelectedItem();
                    certificate = new Certificate();
                    certificate.setStudent(r.getStudent());
                    certificate.setClas(r.getClas());
                    certificate.setCertName(txtCertName.getText().trim());
                    certificate.setSerialNo(txtSerialNo.getText().trim());
                    certificate.setIssueDate(dateIssue.getDate());
                    saved = true; dispose();
                } catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage()); }
            });

            setLayout(new BorderLayout());
            add(form, BorderLayout.CENTER);
            add(btnSave, BorderLayout.SOUTH);
            pack(); setLocationRelativeTo(owner);
        }
        public boolean isSaved() { return saved; }
        public Certificate getCertificate() { return certificate; }
    }
}

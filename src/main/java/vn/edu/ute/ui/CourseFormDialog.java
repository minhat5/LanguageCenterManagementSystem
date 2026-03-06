package vn.edu.ute.ui;

import vn.edu.ute.common.enumeration.DurationUnit;
import vn.edu.ute.common.enumeration.Level;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.model.Course;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class CourseFormDialog extends JDialog {
    private final JTextField txtCourseName = new JTextField(25);
    private final JTextPane txtDescription = new JTextPane();
    private final JComboBox<Level> cboLevel = new JComboBox<>();
    private final JTextField txtDuration = new JTextField(10);
    private final JComboBox<DurationUnit> cboDurationUnit = new JComboBox<>();
    private final JTextField txtFee = new JTextField(10);
    private final JComboBox<Status> cboStatus = new JComboBox<>();

    private boolean saved = false;
    private Course course;

    public CourseFormDialog(Frame owner, String title, Course existing) {
        super(owner, title, true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loadComboBoxes();
        buildUI();
        if(existing != null) {
            this.course = existing;
            txtCourseName.setText(existing.getCourseName());
            txtDescription.setText(existing.getDescription());
            cboLevel.setSelectedItem(existing.getLevel());
            txtDuration.setText(String.valueOf(existing.getDuration()));
            cboDurationUnit.setSelectedItem(existing.getDurationUnit());
            txtFee.setText(existing.getFee().toPlainString());
            cboStatus.setSelectedItem(existing.getStatus());
        } else {
            this.course = new Course();
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void loadComboBoxes() {
        for (Level level : Level.values()) {
            cboLevel.addItem(level);
        }
        for (DurationUnit unit : DurationUnit.values()) {
            cboDurationUnit.addItem(unit);
        }
        for (Status status : Status.values()) {
            cboStatus.addItem(status);
        }
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;

        int r = 0;

        g.gridx = 0; g.gridy = r; form.add(new JLabel("Tên khoá học:"), g);
        g.gridx = 1; form.add(txtCourseName, g);

        r++;
        g.gridx = 0; g.gridy = r; g.anchor = GridBagConstraints.NORTHWEST; form.add(new JLabel("Mô tả:"), g);
        g.gridx = 1;
        JScrollPane scrollDesc = new JScrollPane(txtDescription);
        scrollDesc.setPreferredSize(new Dimension(280, 80));
        form.add(scrollDesc, g);
        g.anchor = GridBagConstraints.WEST;

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Cấp độ:"), g);
        g.gridx = 1; form.add(cboLevel, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Thời gian:"), g);
        JPanel durationPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        durationPanel.add(txtDuration);
        durationPanel.add(Box.createHorizontalStrut(5));
        durationPanel.add(cboDurationUnit);
        g.gridx = 1; form.add(durationPanel, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Chi phí:"), g);
        g.gridx = 1; form.add(txtFee, g);

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
        getContentPane().add(form, BorderLayout.CENTER);
        getContentPane().add(actions, BorderLayout.SOUTH);
    }

    private void onSave() {
        try {
            String name = txtCourseName.getText().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Tên khoá học không được để trống.");

            String description = txtDescription.getText().trim();

            Level level = (Level) cboLevel.getSelectedItem();

            int duration;
            try {
                duration = Integer.parseInt(txtDuration.getText().trim());
                if (duration <= 0) throw new IllegalArgumentException("Thời gian phải > 0.");
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Thời gian phải là số nguyên dương hợp lệ.");
            }

            DurationUnit unit = (DurationUnit) cboDurationUnit.getSelectedItem();

            BigDecimal fee;
            try {
                fee = new BigDecimal(txtFee.getText().trim());
                if (fee.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Chi phí phải >= 0.");
                }
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Chi phí phải là số hợp lệ.");
            }

            Status status = (Status) cboStatus.getSelectedItem();

            course.setCourseName(name);
            course.setDescription(description);
            course.setLevel(level);
            course.setDuration(duration);
            course.setDurationUnit(unit);
            course.setFee(fee);
            course.setStatus(status);

            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public Course getCourse() { return course; }
}

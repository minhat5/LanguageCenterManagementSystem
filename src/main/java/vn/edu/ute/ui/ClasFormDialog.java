package vn.edu.ute.ui;

import com.github.lgooddatepicker.components.DatePicker;
import vn.edu.ute.common.enumeration.ClassStatus;
import vn.edu.ute.model.*;
import vn.edu.ute.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ClasFormDialog extends JDialog {
    private final JTextField txtClassName = new JTextField(25);
    private final JComboBox<Course> cboCourse = new JComboBox<>();
    private final JComboBox<Teacher> cboTeacher = new JComboBox<>();
    private final JComboBox<Branch> cboBranch = new JComboBox<>();
    private final JComboBox<Room> cboRoom = new JComboBox<>();
    private final DatePicker dateStart = new DatePicker();
    private final DatePicker dateEnd = new DatePicker();
    private final JTextField txtMaxStudent = new JTextField(10);
    private final JComboBox<ClassStatus> cboStatus = new JComboBox<>();

    private boolean saved = false;
    private Clas clas;

    public ClasFormDialog(Frame owner, String title, Clas existing, List<Course> courses, List<Teacher> teachers, List<Branch> branches, List<Room> rooms) {
        super(owner, title, true);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        loadComboBoxes(courses, teachers, branches, rooms);
        buildUI();
        if(existing != null) {
            this.clas = existing;
            txtClassName.setText(existing.getClassName());
            cboCourse.setSelectedItem(existing.getCourse());
            cboTeacher.setSelectedItem(existing.getTeacher());
            cboBranch.setSelectedItem(existing.getBranch());
            cboRoom.setSelectedItem(existing.getRoom());
            dateStart.setDate(existing.getStartDate());
            dateEnd.setDate(existing.getEndDate());
            txtMaxStudent.setText(String.valueOf(existing.getMaxStudent() != null ? existing.getMaxStudent() : ""));
            cboStatus.setSelectedItem(existing.getStatus());
        } else {
            this.clas = new Clas();
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void loadComboBoxes(List<Course> courses, List<Teacher> teachers, List<Branch> branches, List<Room> rooms) {
        try {
            for (Course course : courses) {
                cboCourse.addItem(course);
            }

            for (Teacher teacher : teachers) {
                cboTeacher.addItem(teacher);
            }

            for (Branch branch : branches) {
                cboBranch.addItem(branch);
            }

            for (Room room : rooms) {
                cboRoom.addItem(room);
            }

            for (ClassStatus status : ClassStatus.values()) {
                cboStatus.addItem(status);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        UIUtils.setComboBoxRenderer(cboCourse, Course::getCourseName);
        UIUtils.setComboBoxRenderer(cboTeacher, Teacher::getFullName);
        UIUtils.setComboBoxRenderer(cboBranch, Branch::getBranchName);
        UIUtils.setComboBoxRenderer(cboRoom, Room::getRoomName);
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        int r = 0;

        g.gridx = 0; g.gridy = r; form.add(new JLabel("Class Name:"), g);
        g.gridx = 1; form.add(txtClassName, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Course:"), g);
        g.gridx = 1; form.add(cboCourse, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Teacher:"), g);
        g.gridx = 1; form.add(cboTeacher, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Branch:"), g);
        g.gridx = 1; form.add(cboBranch, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Room:"), g);
        g.gridx = 1; form.add(cboRoom, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Start Date:"), g);
        g.gridx = 1; form.add(dateStart, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("End Date:"), g);
        g.gridx = 1; form.add(dateEnd, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Max Students:"), g);
        g.gridx = 1; form.add(txtMaxStudent, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Status:"), g);
        g.gridx = 1; form.add(cboStatus, g);

        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");
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
            String className = txtClassName.getText().trim();
            if (className.isEmpty()) {
                throw new IllegalArgumentException("Class Name cannot be empty.");
            }

            if (dateStart.getDate() == null) {
                throw new IllegalArgumentException("Start Date is required.");
            }
            if (dateEnd.getDate() != null && dateEnd.getDate().isBefore(dateStart.getDate())) {
                throw new IllegalArgumentException("End Date must be after Start Date.");
            }

            int maxStudent;
            try {
                maxStudent = Integer.parseInt(txtMaxStudent.getText().trim());
                if (maxStudent <= 0) throw new IllegalArgumentException("Max students must be > 0.");
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Max students must be a valid number.");
            }

            clas.setClassName(className);
            clas.setCourse((Course) cboCourse.getSelectedItem());
            clas.setTeacher((Teacher) cboTeacher.getSelectedItem());
            clas.setBranch((Branch) cboBranch.getSelectedItem());
            clas.setRoom((Room) cboRoom.getSelectedItem());
            clas.setStartDate(dateStart.getDate());
            clas.setEndDate(dateEnd.getDate());
            clas.setMaxStudent(maxStudent);
            clas.setStatus((ClassStatus) cboStatus.getSelectedItem());

            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi nhập liệu: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public Clas getClas() { return clas; }
}

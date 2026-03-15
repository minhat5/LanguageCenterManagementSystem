package vn.edu.ute.ui.clas;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import vn.edu.ute.common.enumeration.ClassStatus;
import vn.edu.ute.model.*;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
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
    private final List<Course> courses;
    private final List<Teacher> teachers;
    private final List<Branch> branches;
    private final List<Room> rooms;
    private final RoomService roomService;

    private boolean saved = false;
    private Clas clas;

    public ClasFormDialog(Frame owner, String title, Clas existing, List<Course> courses, List<Teacher> teachers, List<Branch> branches, List<Room> rooms, RoomService roomService) {
        super(owner, title, true);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.courses = courses;
        this.teachers = teachers;
        this.branches = branches;
        this.rooms = rooms;
        this.roomService = roomService;
        buildUI();
        loadComboBoxes();
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

    private void loadComboBoxes() {
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

    private void loadRoomsByBranch() {
        cboRoom.removeAllItems();
        Branch selectedBranch = (Branch) cboBranch.getSelectedItem();
        if (selectedBranch != null) {
            List<Room> loadedRooms = roomService.getByBranchId(rooms, selectedBranch.getBranchId());
            for (Room room : loadedRooms) {
                cboRoom.addItem(room);
            }
        }
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        int r = 0;

        g.gridx = 0; g.gridy = r; form.add(new JLabel("Tên lớp:"), g);
        g.gridx = 1; form.add(txtClassName, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Khoá học:"), g);
        g.gridx = 1; form.add(cboCourse, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Giáo viên:"), g);
        g.gridx = 1; form.add(cboTeacher, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Chi nhánh:"), g);
        g.gridx = 1; form.add(cboBranch, g);
        cboBranch.addActionListener(e -> loadRoomsByBranch());

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Phòng:"), g);
        g.gridx = 1; form.add(cboRoom, g);

        DatePickerSettings startSettings = dateStart.getSettings();
        startSettings.setAllowKeyboardEditing(false);
        startSettings.setVetoPolicy(date -> !date.isBefore(LocalDate.now()));
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Ngày bắt đầu:"), g);
        g.gridx = 1; form.add(dateStart, g);

        DatePickerSettings endSettings = dateEnd.getSettings();
        endSettings.setAllowKeyboardEditing(false);
        endSettings.setVetoPolicy(date -> {
            if (dateStart.getDate() == null) return date.isAfter(LocalDate.now());
            return date.isAfter(dateStart.getDate());
        });
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Ngày kết thúc:"), g);
        g.gridx = 1; form.add(dateEnd, g);

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Sĩ số tối đa:"), g);
        g.gridx = 1; form.add(txtMaxStudent, g);

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
            String className = txtClassName.getText().trim();
            if (className.isEmpty()) {
                throw new IllegalArgumentException("Tên lớp không được để trống.");
            }

            if (dateStart.getDate() == null) {
                throw new IllegalArgumentException("Ngày bắt đầu không được để trống.");
            }
            if (dateEnd.getDate() != null && dateEnd.getDate().isBefore(dateStart.getDate())) {
                throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
            }

            int maxStudent;
            try {
                maxStudent = Integer.parseInt(txtMaxStudent.getText().trim());
                if (maxStudent <= 0) throw new IllegalArgumentException("Sĩ số tối đa phải > 0.");
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("Sĩ số tối đa phải là số hợp lệ.");
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

package vn.edu.ute.ui.clas;


import vn.edu.ute.common.enumeration.ClassStatus;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.dto.ClasView;
import vn.edu.ute.model.*;
import vn.edu.ute.service.*;
import vn.edu.ute.util.UIUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClasPanel extends JPanel {
    private final ClasService clasService;
    private final CourseService courseService;
    private final TeacherService teacherService;
    private final BranchService branchService;
    private final RoomService roomService;
    private final EnrollmentService enrollmentService;

    private final ClasTableModel tableModel = new ClasTableModel();
    private final JTable table = new JTable(tableModel);

    private final JComboBox<Object> cboFilterStatus = new JComboBox<>();
    private final JComboBox<Course> cboFilterCourse = new JComboBox<>();
    private final JComboBox<Branch> cboFilterBranch = new JComboBox<>();

    private final JLabel lblSelected = new JLabel("Đã chọn: (Không)");
    private ClasView selectedClass = null;
    private List<Clas> classes;

    private List<Course> cacheCourses = new ArrayList<>();
    private List<Branch> cacheBranches = new ArrayList<>();
    private List<Teacher> cacheTeachers = new ArrayList<>();
    private List<Room> cacheRooms = new ArrayList<>();

    public ClasPanel(ClasService clasService, CourseService courseService, TeacherService teacherService, BranchService branchService, RoomService roomService, EnrollmentService enrollmentService) {
        this.clasService = clasService;
        this.courseService = courseService;
        this.teacherService = teacherService;
        this.branchService = branchService;
        this.roomService = roomService;
        this.enrollmentService = enrollmentService;

        buildUI();
        reloadAll();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout(8, 8));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(new JLabel("Khoá học:"));
        left.add(cboFilterCourse);
        left.add(new JLabel("Chi nhánh:"));
        left.add(cboFilterBranch);
        left.add(new JLabel("Trạng thái:"));
        left.add(cboFilterStatus);
        JTextField searchField = new JTextField(25);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }

            private void performSearch() {
                String keyword = searchField.getText().trim();
                if (keyword.isEmpty()) {
                    refreshTableByCurrentFilter();
                    return;
                }

                try {
                    classes = clasService.findByName(classes, keyword);
                    tableModel.setData(clasService.toClasView(classes));
                    lblSelected.setText("Đã chọn: (Không)");
                } catch (Exception ex) {
                    System.err.println("Lỗi khi tìm kiếm: " + ex.getMessage());
                }
            }
        });
        left.add(new JLabel("Tìm kiếm:"));
        left.add(searchField);
        top.add(left, BorderLayout.WEST);

        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xoá");
        JButton btnViewStudents = new JButton("Xem Học Viên");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete());
        btnViewStudents.addActionListener(e -> onViewStudents());
        btnRefresh.addActionListener(e -> reloadAll());

        boolean canEdit = vn.edu.ute.common.policy.RolePolicy.canEditCourseAndClass();
        btnAdd.setVisible(canEdit);
        btnEdit.setVisible(canEdit);
        btnViewStudents.setVisible(canEdit);
        btnDelete.setVisible(canEdit);


        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnViewStudents);
        right.add(btnRefresh);
        top.add(right, BorderLayout.EAST);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::onRowSelected);
        JScrollPane scroll = new JScrollPane(table);

        cboFilterCourse.addActionListener(e -> refreshTableByCurrentFilter());
        cboFilterBranch.addActionListener(e -> refreshTableByCurrentFilter());
        cboFilterStatus.addActionListener(e -> refreshTableByCurrentFilter());

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
        try {
            cboFilterCourse.removeAllItems();
            cboFilterBranch.removeAllItems();
            cboFilterStatus.removeAllItems();

            cboFilterCourse.addItem(null);
            for (Course course : cacheCourses) {
                cboFilterCourse.addItem(course);
            }

            cboFilterBranch.addItem(null);
            for (Branch branch : cacheBranches) {
                cboFilterBranch.addItem(branch);
            }
            cboFilterStatus.addItem("Tất cả");
            for (ClassStatus s : ClassStatus.values()) {
                cboFilterStatus.addItem(s);
            }

            UIUtils.setComboBoxRenderer(cboFilterCourse, Course::getCourseName);
            UIUtils.setComboBoxRenderer(cboFilterBranch, Branch::getBranchName);
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu cho bộ lọc: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onViewStudents() {
        if(selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 lớp học để xem danh sách.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        ClassStudentListDialog dlg = new ClassStudentListDialog(parent, selectedClass, enrollmentService);
        dlg.setVisible(true);
    }

    private void reloadAll() {
        try {
            cacheCourses = courseService.getAll();
            cacheCourses = courseService.getCoursesByStatus(cacheCourses).get(true);
            cacheBranches = branchService.getAll();
            cacheTeachers = teacherService.getAll();
            cacheRooms = roomService.getAll();

            loadComboBoxes();

            cboFilterCourse.setSelectedIndex(0);
            cboFilterBranch.setSelectedIndex(0);
            cboFilterStatus.setSelectedIndex(0);

            refreshTableByCurrentFilter();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTableByCurrentFilter() {
        try {
            Object statusObj = cboFilterStatus.getSelectedItem();
            Object courseObj = cboFilterCourse.getSelectedItem();
            Object branchObj = cboFilterBranch.getSelectedItem();

            Course courseFilter = (courseObj instanceof Course) ? (Course) courseObj : null;
            Branch branchFilter = (branchObj instanceof Branch) ? (Branch) branchObj : null;
            ClassStatus statusFilter = (statusObj instanceof ClassStatus) ? (ClassStatus) statusObj : null;

            classes = clasService.getAll();

            if(courseFilter != null) {
                classes = clasService.getClasViewsByCourse(classes, courseFilter);
            }

            if(branchFilter != null) {
                classes = clasService.getClasViewsByBranch(classes, branchFilter);
            }

            if(statusFilter != null) {
                classes = clasService.getClasViewsByStatus(classes, statusFilter);
            }

            tableModel.setData(clasService.toClasView(classes));
            lblSelected.setText("Đã chọn: (Không)");
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải các lớp học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = table.getSelectedRow();
        selectedClass = tableModel.getAt(row);
        if (selectedClass != null) {
            lblSelected.setText("Đã chọn: Mã khoá học: " + selectedClass.classId() + " | " + selectedClass.className());
        } else {
            lblSelected.setText("Đã chọn: (Không)");
        }
    }

    private void onAdd() {
        showAddDialog(null);
    }

    public void showAddDialog(Course selected) {
        Clas initClas = null;

        if (selected != null) {
            initClas = new Clas();
            initClas.setCourse(selected);
        }

        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        ClasFormDialog dlg = new ClasFormDialog(parent, "Thêm lớp học", initClas, cacheCourses, cacheTeachers, cacheBranches, cacheRooms, roomService);
        dlg.setVisible(true);

        if (!dlg.isSaved()) {
            return;
        }

        try {
            clasService.insert(dlg.getClas());
            JOptionPane.showMessageDialog(this, "Thêm lớp học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm lớp học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        if(selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lớp học để chỉnh sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Clas editClas = clasService.findById(selectedClass.classId());
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            ClasFormDialog dlg = new ClasFormDialog(parent, "Cập nhật lớp học", editClas, cacheCourses, cacheTeachers, cacheBranches, cacheRooms, roomService);
            dlg.setVisible(true);
            if (!dlg.isSaved()) {
                return;
            }

            clasService.update(dlg.getClas());
            JOptionPane.showMessageDialog(this, "Cập nhật khoá học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khoá học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        if(selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoá học để xoá.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xoá khoá học này?", "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
        if(ok != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            clasService.delete(selectedClass.classId());
            JOptionPane.showMessageDialog(this, "Xoá thành công khoá học!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xoá khoá học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    class ClassStudentListDialog extends JDialog {
        public ClassStudentListDialog(Frame owner, ClasView clasInfo, EnrollmentService enrollmentService) {
            super(owner, "Danh sách học viên lớp: " + clasInfo.className(), true);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout(10, 10));
            setSize(800, 500);
            setLocationRelativeTo(owner);

            String[] cols = {"Mã HV", "Tên Học Viên", "Giới Tính", "Số Điện Thoại", "Email", "Ngày Ghi Danh", "Trạng Thái"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
            JTable table = new JTable(model);

            // Đổ dữ liệu
            try {
                List<Enrollment> enrollments = enrollmentService.getEnrollmentsByClassId(clasInfo.classId());
                for (Enrollment e : enrollments) {
                    Student s = e.getStudent();
                    model.addRow(new Object[]{
                            s.getStudentId(), s.getFullName(), s.getGender(), s.getPhone(), s.getEmail(), e.getEnrollmentDate(), e.getStatus()
                    });
                }

                JLabel lblCount = new JLabel("Tổng số: " + enrollments.size() + " / " + clasInfo.maxStudent() + " học viên");
                lblCount.setFont(new Font("Arial", Font.BOLD, 14));
                lblCount.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                add(lblCount, BorderLayout.NORTH);
                add(new JScrollPane(table), BorderLayout.CENTER);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(owner, "Lỗi tải danh sách: " + ex.getMessage());
            }
        }
    }
}

package vn.edu.ute.ui;

import vn.edu.ute.common.enumeration.Level;
import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.model.Course;
import vn.edu.ute.service.CourseService;
import vn.edu.ute.ui.common.ButtonRenderer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;

public class CoursePanel extends JPanel {
    private final CourseService courseService;

    private final CourseTableModel tableModel = new CourseTableModel();
    private final JTable table = new JTable(tableModel);

    private final JComboBox<Object> cboFilterStatus = new JComboBox<>();
    private final JComboBox<Object> cboFilterLevel = new JComboBox<>();

    private final JLabel lblSelected = new JLabel("Đã chọn: (Không)");
    private Course selectedCourse = null;
    private List<Course> courses;

    private final Consumer<Course> onCreateClassClick;

    public CoursePanel(CourseService courseService, Consumer<Course> onCreateClassClick) {
        this.courseService = courseService;
        this.onCreateClassClick = onCreateClassClick;
        buildUI();
        loadComboBoxes();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout(8, 8));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(new JLabel("Trạng thái:"));
        left.add(cboFilterStatus);
        left.add(new JLabel("Level:"));
        left.add(cboFilterLevel);
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
                    courses = courseService.findByName(courses, keyword);
                    tableModel.setData(courses);
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

        table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer(new ButtonRenderer());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == table.getColumnCount() - 1 && row >= 0) {
                    Course selected = tableModel.getAt(row);
                    if(onCreateClassClick != null && selected != null) {
                        onCreateClassClick.accept(selected);
                    }
                }
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::onRowSelected);
        JScrollPane scroll = new JScrollPane(table);

        cboFilterStatus.addActionListener(e -> refreshTableByCurrentFilter());
        cboFilterLevel.addActionListener(e -> refreshTableByCurrentFilter());

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
        for(Status s : Status.values()) {
            cboFilterStatus.addItem(s);
        }

        cboFilterLevel.addItem("Tất cả");
        for(Level l : Level.values()) {
            cboFilterLevel.addItem(l);
        }
    }

    private void reloadAll() {
        cboFilterStatus.setSelectedIndex(0);
        cboFilterLevel.setSelectedIndex(0);
        refreshTableByCurrentFilter();
    }

    private void refreshTableByCurrentFilter() {
        try {
            Object statusObj = cboFilterStatus.getSelectedItem();
            Object levelObj = cboFilterLevel.getSelectedItem();

            Status statusFilter = (statusObj instanceof Status) ? (Status) statusObj : null;
            Level levelFilter = (levelObj instanceof Level) ? (Level) levelObj : null;

            courses = courseService.getAll();
            if(statusFilter != null) {
                courses = courseService.getCoursesByStatus(courses, statusFilter);
            }

            if(levelFilter != null) {
                courses = courseService.getCoursesByLevel(courses, levelFilter);
            }

            tableModel.setData(courses);
            lblSelected.setText("Đã chọn: (Không)");
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải các khoá học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = table.getSelectedRow();
        selectedCourse = tableModel.getAt(row);
        if (selectedCourse != null) {
            lblSelected.setText("Đã chọn: Mã khoá học: " + selectedCourse.getCourseId() + " | " + selectedCourse.getCourseName());
        } else {
            lblSelected.setText("Đã chọn: (Không)");
        }
    }

    private void onAdd() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        CourseFormDialog dlg = new CourseFormDialog(parent, "Thêm khoá học", null);
        dlg.setVisible(true);
        if (!dlg.isSaved()) {
            return;
        }

        try {
            courseService.insert(dlg.getCourse());
            JOptionPane.showMessageDialog(this, "Thêm khoá học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm khoá học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        if(selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoá học để chỉnh sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Course editCourse = new Course(
                selectedCourse.getCourseId(),
                selectedCourse.getCourseName(),
                selectedCourse.getDescription(),
                selectedCourse.getLevel(),
                selectedCourse.getDuration(),
                selectedCourse.getDurationUnit(),
                selectedCourse.getFee(),
                selectedCourse.getStatus()
        );
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        CourseFormDialog dlg = new CourseFormDialog(parent, "Cập nhật khoá học", editCourse);
        dlg.setVisible(true);
        if (!dlg.isSaved()) {
            return;
        }

        try {
            courseService.update(dlg.getCourse());
            JOptionPane.showMessageDialog(this, "Cập nhật khoá học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật khoá học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        if(selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khoá học để xoá.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xoá khoá học này?", "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
        if(ok != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            courseService.delete(selectedCourse.getCourseId());
            JOptionPane.showMessageDialog(this, "Xoá thành công khoá học!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xoá khoá học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

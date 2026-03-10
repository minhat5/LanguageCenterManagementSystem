package vn.edu.ute.ui.attendance;

import com.github.lgooddatepicker.components.DatePicker;
import vn.edu.ute.common.enumeration.AttendanceStatus;
import vn.edu.ute.dto.AttendanceView;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Clas;
import vn.edu.ute.service.AttendanceService;
import vn.edu.ute.service.ClasService;
import vn.edu.ute.util.UIUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendancePanel extends JPanel {
    private final ClasService clasService;
    private final AttendanceService attendanceService;

    private final AttendanceTableModel tableModel = new AttendanceTableModel();
    private final JTable table = new JTable(tableModel);

    private final JComboBox<Object> cboFilterClass = new JComboBox<>();
    private final DatePicker studyDateFilter = new DatePicker();

    private final JLabel lblSelected = new JLabel("Đã chọn: (Không)");
    private AttendanceView selectedAttendance = null;
    private List<Attendance> attendances;

    private List<Clas> cacheClasses = new ArrayList<>();

    public AttendancePanel(ClasService clasService, AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
        this.clasService = clasService;

        buildUI();
        reloadAll();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout(8, 8));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(new JLabel("Lớp học:"));
        left.add(cboFilterClass);
        left.add(new JLabel("Ngày học:"));
        left.add(studyDateFilter);

        top.add(left, BorderLayout.WEST);

        JButton btnUpdate = new JButton("Cập nhật điểm danh");
        JButton btnRefresh = new JButton("Refresh");

        btnRefresh.addActionListener(e -> reloadAll());
        btnUpdate.addActionListener(e -> onUpdate());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        right.add(btnUpdate);
        right.add(btnRefresh);
        top.add(right, BorderLayout.EAST);

        JComboBox<AttendanceStatus> cboStatus = new JComboBox<>(AttendanceStatus.values());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::onRowSelected);
        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(cboStatus));
        JScrollPane scroll = new JScrollPane(table);

        cboFilterClass.addActionListener(e -> refreshTableByCurrentFilter());
        studyDateFilter.addDateChangeListener(e -> refreshTableByCurrentFilter());

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
            cboFilterClass.removeAllItems();

            for (Clas clas : cacheClasses) {
                cboFilterClass.addItem(clas);
            }

            UIUtils.setComboBoxRenderer(cboFilterClass, c -> ((Clas) c).getClassName() + " - " + ((Clas) c).getCourse().getCourseName());
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu cho bộ lọc: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reloadAll() {
        try {
            cacheClasses = clasService.getAll();

            loadComboBoxes();

            cboFilterClass.setSelectedIndex(0);
            studyDateFilter.setDateToToday();
            refreshTableByCurrentFilter();
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTableByCurrentFilter() {
        try {
            Object classObj = cboFilterClass.getSelectedItem();
            LocalDate studyDate = studyDateFilter.getDate();

            Clas clasFilter = (classObj instanceof Clas) ? (Clas) classObj : null;

            attendances = attendanceService.getAll();

            if(clasFilter != null) {
                attendances = attendanceService.getByClass(attendances, clasFilter);
            }

            if(studyDate != null) {
                attendances = attendanceService.getByAttendDate(attendances, studyDate);
            }

            tableModel.setData(attendanceService.toAttendanceView(attendances));
            lblSelected.setText("Đã chọn: (Không)");
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải các lịch học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = table.getSelectedRow();
        selectedAttendance = tableModel.getAt(row);
        if (selectedAttendance != null) {
            lblSelected.setText("Đã chọn: Mã điểm danh: " + selectedAttendance.attendanceId() + " | " + selectedAttendance.studentName());
        } else {
            lblSelected.setText("Đã chọn: (Không)");
        }
    }

    private void onUpdate() {
        if(table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        if(tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để cập nhật", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                AttendanceView view = tableModel.getAt(i);

                AttendanceStatus updatedStatus = (AttendanceStatus) tableModel.getValueAt(i, 4);

                attendances.stream()
                        .filter(a -> a.getAttendanceId().equals(view.attendanceId()))
                        .findFirst()
                        .ifPresent(a -> {
                            a.setStatus(updatedStatus);
                            try {
                                attendanceService.update(a);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
            }

            JOptionPane.showMessageDialog(this, "Cập nhật điểm danh thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll(); // Tải lại bảng

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật điểm danh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

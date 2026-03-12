package vn.edu.ute.ui.schedule;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import vn.edu.ute.dto.ScheduleView;
import vn.edu.ute.model.*;
import vn.edu.ute.service.*;
import vn.edu.ute.util.UIUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SchedulePanel extends JPanel {
    private final ScheduleService scheduleService;
    private final ClasService clasService;
    private final RoomService roomService;

    private final ScheduleTableModel tableModel = new ScheduleTableModel();
    private final JTable table = new JTable(tableModel);

    private final JComboBox<Object> cboFilterClass = new JComboBox<>();
    private final DatePicker studyDateFilter = new DatePicker();

    private final JLabel lblSelected = new JLabel("Đã chọn: (Không)");
    private ScheduleView selectedSchedule = null;
    private List<Schedule> schedules;

    private List<Clas> cacheClasses = new ArrayList<>();
    private List<Room> cacheRooms = new ArrayList<>();

    public SchedulePanel(ScheduleService scheduleService, ClasService clasService, RoomService roomService) {
        this.scheduleService = scheduleService;
        this.clasService = clasService;
        this.roomService = roomService;

        buildUI();
        reloadAll();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout(8, 8));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(new JLabel("Lịch học:"));
        left.add(cboFilterClass);
        DatePickerSettings dateSettings = studyDateFilter.getSettings();
        dateSettings.setAllowKeyboardEditing(false);
        left.add(new JLabel("Ngày học:"));
        left.add(studyDateFilter);

        top.add(left, BorderLayout.WEST);

        JButton btnAdd = new JButton("Thêm");
        JButton btnAddList = new JButton("Thêm tới hết khoá học");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xoá");
        JButton btnDeleteList = new JButton("Xoá tới hết khoá học");
        JButton btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd(false));
        btnAddList.addActionListener(e -> onAdd(true));
        btnEdit.addActionListener(e -> onEdit());
        btnDelete.addActionListener(e -> onDelete(false));
        btnDeleteList.addActionListener(e -> onDelete(true));
        btnRefresh.addActionListener(e -> reloadAll());

        boolean canEdit = vn.edu.ute.common.policy.RolePolicy.canEditCourseAndClass();
        btnAdd.setVisible(canEdit);
        btnAddList.setVisible(canEdit);
        btnEdit.setVisible(canEdit);
        btnDelete.setVisible(canEdit);
        btnDeleteList.setVisible(canEdit);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(btnAdd);
        right.add(btnAddList);
        right.add(btnEdit);
        right.add(btnDelete);
        right.add(btnDeleteList);
        right.add(btnRefresh);
        top.add(right, BorderLayout.EAST);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::onRowSelected);
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

            cboFilterClass.addItem(null);
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
            cacheRooms = roomService.getAll();

            loadComboBoxes();

            cboFilterClass.setSelectedIndex(0);
            studyDateFilter.setText("");
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

            schedules = scheduleService.getAll();

            if(clasFilter != null) {
                schedules = scheduleService.getByClass(schedules, clasFilter);
                cacheRooms.clear();
                cacheRooms.add(clasFilter.getRoom());
            }

            if(studyDate != null) {
                schedules = scheduleService.getByDate(schedules, studyDate);
            }

            tableModel.setData(scheduleService.toScheduleView(schedules));
            lblSelected.setText("Đã chọn: (Không)");
        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải các lịch học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int row = table.getSelectedRow();
        selectedSchedule = tableModel.getAt(row);
        if (selectedSchedule != null) {
            lblSelected.setText("Đã chọn: Mã lịch học: " + selectedSchedule.scheduleId() + " | " + selectedSchedule.className());
        } else {
            lblSelected.setText("Đã chọn: (Không)");
        }
    }

    private void onAdd(boolean isList) {
        try {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            ScheduleFormDialog dlg = new ScheduleFormDialog(parent, "Thêm lịch học", null, clasService.getAllActiveClasses());
            dlg.setVisible(true);

            if (!dlg.isSaved()) {
                return;
            }

            if(!isList) {
                scheduleService.insert(dlg.getSchedule());
            } else {
                scheduleService.insertUntilEndDate(dlg.getSchedule(), scheduleService.getAll());
            }
            JOptionPane.showMessageDialog(this, "Thêm lịch học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm lịch học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEdit() {
        if(selectedSchedule == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch học để chỉnh sửa.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Schedule editSchedule = scheduleService.findById(selectedSchedule.scheduleId());
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            ScheduleFormDialog dlg = new ScheduleFormDialog(parent, "Cập nhật lịch học", editSchedule, clasService.getAllActiveClasses());
            dlg.setVisible(true);
            if (!dlg.isSaved()) {
                return;
            }

            scheduleService.update(dlg.getSchedule());
            JOptionPane.showMessageDialog(this, "Cập nhật lịch học thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật lịch học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete(boolean isList) {
        if(selectedSchedule == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lịch học để xoá.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xoá lịch học này?", "Xác nhận xoá", JOptionPane.YES_NO_OPTION);
        if(ok != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if(!isList) {
                scheduleService.delete(selectedSchedule.scheduleId());
            } else {
                scheduleService.deleteUntilEndDate(scheduleService.findById(selectedSchedule.scheduleId()), scheduleService.getAll());
            }
            JOptionPane.showMessageDialog(this, "Xoá thành công lịch học!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            reloadAll();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xoá lịch học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

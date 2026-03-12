package vn.edu.ute.ui.schedule;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import vn.edu.ute.model.*;
import vn.edu.ute.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ScheduleFormDialog extends JDialog {
    private final JComboBox<Clas> cboClas = new JComboBox<>();
    private final JTextField txtRoom = new JTextField(30);
    private final DatePicker studyDate = new DatePicker();
    private final TimePicker startTime = new TimePicker();
    private final TimePicker endTime = new TimePicker();
    private boolean saved = false;
    private Schedule schedule;

    private final List<Clas> classes;

    public ScheduleFormDialog(Frame owner, String title, Schedule existing, List<Clas> classes) {
        super(owner, title, true);

        this.classes = classes;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        buildUI();
        loadComboBoxes();
        if(existing != null) {
            this.schedule = existing;
            cboClas.setSelectedItem(existing.getClas());
            txtRoom.setText(existing.getRoom().getRoomName() + " - " + existing.getRoom().getBranch().getBranchName());
            studyDate.setDate(existing.getStudyDate());
            startTime.setTime(existing.getStartTime());
            endTime.setTime(existing.getEndTime());
        } else {
            this.schedule = new Schedule();
        }

        pack();
        setLocationRelativeTo(owner);
    }

    private void loadComboBoxes() {
        try {
            for (Clas c : classes) {
                cboClas.addItem(c);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Có lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
        UIUtils.setComboBoxRenderer(cboClas, c -> c.getClassName() + " - " + c.getCourse().getCourseName());
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        int r = 0;

        g.gridx = 0; g.gridy = r; form.add(new JLabel("Lớp học:"), g);
        g.gridx = 1; form.add(cboClas, g);
        cboClas.addActionListener(e -> {
            Clas selected = (Clas) cboClas.getSelectedItem();
            if (selected != null && selected.getRoom() != null) {
                txtRoom.setText(selected.getRoom().getRoomName() + " - " + selected.getRoom().getBranch().getBranchName());
            } else {
                txtRoom.setText("");
            }
        });

        DatePickerSettings dateSettings = studyDate.getSettings();
        dateSettings.setAllowKeyboardEditing(false);
        dateSettings.setVetoPolicy(date -> date.isAfter(LocalDate.now()));
        studyDate.setDate(LocalDate.now().plusDays(1));
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Ngày học:"), g);
        g.gridx = 1; form.add(studyDate, g);


        TimePickerSettings startSettings = startTime.getSettings();
        startSettings.setAllowKeyboardEditing(false);
        startSettings.setVetoPolicy(time -> {
                LocalTime openTime = LocalTime.of(7, 0);
                LocalTime closeTime = LocalTime.of(22, 0);
                return !time.isBefore(openTime) && !time.isAfter(closeTime);
        });

        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Giờ bắt đầu:"), g);
        g.gridx = 1; form.add(startTime, g);


        TimePickerSettings endSettings = endTime.getSettings();
        endSettings.setAllowKeyboardEditing(false);
        endSettings.setVetoPolicy(time -> {
            LocalTime openTime = LocalTime.of(7, 30);
            LocalTime closeTime = LocalTime.of(22, 30);
            return !time.isBefore(openTime) && !time.isAfter(closeTime);
        });
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Giờ kết thúc:"), g);
        g.gridx = 1; form.add(endTime, g);

        startTime.addTimeChangeListener(event -> {
            LocalTime selectedStartTime = event.getNewTime();

            if (selectedStartTime != null) {
                // Cập nhật lại luật: Giờ kết thúc bắt buộc phải SAU Giờ bắt đầu
                endSettings.setVetoPolicy(time -> time.isAfter(selectedStartTime) && !time.isAfter(LocalTime.of(23, 0)));

                // Nếu Giờ kết thúc đang được chọn mà lại NHỎ HƠN hoặc BẰNG Giờ bắt đầu mới
                //Tự động đẩy Giờ kết thúc lên thêm 30 phút
                LocalTime currentEndTime = endTime.getTime();
                if (currentEndTime != null && !currentEndTime.isAfter(selectedStartTime)) {
                    endTime.setTime(selectedStartTime.plusMinutes(30));
                }
            } else {
                // Nếu người dùng xóa Giờ bắt đầu, trả Giờ kết thúc về luật mặc định
                endSettings.setVetoPolicy(time -> !time.isBefore(LocalTime.of(7, 0)) && !time.isAfter(LocalTime.of(22, 0)));
            }
        });
        r++;
        g.gridx = 0; g.gridy = r; form.add(new JLabel("Thông tin phòng:"), g);
        g.gridx = 1;
        txtRoom.setEditable(false);
        form.add(txtRoom, g);

        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
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
            Clas selectedClas = (Clas) cboClas.getSelectedItem();
            if (selectedClas == null) {
                throw new IllegalArgumentException("Vui lòng chọn một lớp học.");
            }

            if (studyDate.getDate() == null) {
                throw new IllegalArgumentException("Vui lòng chọn ngày học.");
            }

            if(studyDate.getDate().isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Ngày học không được là ngày hôm nay hoặc trước đó.");
            }

            if (startTime.getTime() == null || endTime.getTime() == null) {
                throw new IllegalArgumentException("Vui lòng nhập đầy đủ giờ bắt đầu và kết thúc.");
            }

            if (endTime.getTime().isBefore(startTime.getTime())) {
                throw new IllegalArgumentException("Giờ kết thúc phải sau giờ bắt đầu.");
            }

            schedule.setClas(selectedClas);
            schedule.setStudyDate(studyDate.getDate());
            schedule.setStartTime(startTime.getTime());
            schedule.setEndTime(endTime.getTime());
            schedule.setRoom(selectedClas.getRoom());

            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(),
                    "Thông báo", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() { return saved; }
    public Schedule getSchedule() { return schedule; }
}

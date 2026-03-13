package vn.edu.ute.ui.schedule;

import vn.edu.ute.dto.ScheduleView;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ScheduleTableModel extends AbstractTableModel {
    private final String[] columns = {"Mã lịch", "Tên lớp", "Ngày học", "Giờ bắt đầu", "Giờ kết thúc", "Chi nhánh", "Phòng", "Thời điểm tạo"};
    private List<ScheduleView> data = new ArrayList<>();

    public void setData(List<ScheduleView> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public ScheduleView getAt(int row) {
        if(row < 0 || row >= data.size()) {
            return null;
        }
        return data.get(row);
    }
    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ScheduleView schedule = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return schedule.scheduleId();
            case 1: return schedule.className();
            case 2: return schedule.studyDate();
            case 3: return schedule.startTime();
            case 4: return schedule.endTime();
            case 5: return schedule.branchName();
            case 6: return schedule.roomName();
            case 7: return schedule.createdAt();
            default: return "";
        }
    }
}

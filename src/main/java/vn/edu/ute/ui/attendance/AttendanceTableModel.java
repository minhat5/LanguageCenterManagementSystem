package vn.edu.ute.ui.attendance;


import vn.edu.ute.common.enumeration.AttendanceStatus;
import vn.edu.ute.dto.AttendanceView;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AttendanceTableModel extends AbstractTableModel {
    private final String[] columns = {"Mã điểm danh", "Tên học sinh", "Tên lớp", "Ngày học", "Trạng thái", "Ghi chú", "Thời điểm tạo"};
    private List<AttendanceView> data = new ArrayList<>();

    public void setData(List<AttendanceView> data) {
        this.data = new ArrayList<>(data);
        fireTableDataChanged();
    }

    public AttendanceView getAt(int row) {
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
        AttendanceView attendance = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return attendance.attendanceId();
            case 1: return attendance.studentName();
            case 2: return attendance.className();
            case 3: return attendance.attendDate();
            case 4: return attendance.status();
            case 5: return attendance.note();
            case 6: return attendance.createdAt();
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 4 || columnIndex == 5;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        AttendanceView attendance = data.get(rowIndex);
        if(columnIndex == 4) {
            AttendanceStatus newStatus = (AttendanceStatus) aValue;
            AttendanceView updatedAttendance = new AttendanceView(
                    attendance.attendanceId(),
                    attendance.studentName(),
                    attendance.className(),
                    attendance.attendDate(),
                    newStatus,
                    attendance.note(),
                    attendance.createdAt()
            );
            data.set(rowIndex, updatedAttendance);
            fireTableCellUpdated(rowIndex, columnIndex);
        } else if(columnIndex == 5) {
            String newNote = (String) aValue;
            AttendanceView updatedView = new AttendanceView(
                    attendance.attendanceId(),
                    attendance.studentName(),
                    attendance.className(),
                    attendance.attendDate(),
                    attendance.status(), newNote,
                    attendance.createdAt()
            );
            data.set(rowIndex, updatedView);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}

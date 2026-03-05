package vn.edu.ute.ui;

import vn.edu.ute.dto.ClasView;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ClasTableModel extends AbstractTableModel {
    private final String[] columns = {"Mã lớp", "Tên lớp", "Tên khoá học", "Tên giáo viên", "Phòng học", "Chi nhánh", "Ngày bắt đầu", "Ngày kết thúc", "Sĩ số tối đa", "Trạng thái", "Thời điểm tạo", "Thời điểm chỉnh sửa"};
    private List<ClasView> data = new ArrayList<>();

    public void setData(List<ClasView> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public ClasView getAt(int row) {
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
        ClasView clas = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return clas.classId();
            case 1: return clas.className();
            case 2: return clas.courseName();
            case 3: return clas.teacherName() != null ? clas.teacherName() : "Chưa có giáo viên";
            case 4: return clas.roomName() != null ? clas.roomName() : "Chưa có phòng học";
            case 5: return clas.branchName() != null ? clas.branchName() : "Chưa có chi nhánh";
            case 6: return clas.startDate();
            case 7: return clas.endDate();
            case 8: return clas.maxStudent();
            case 9: return clas.status();
            case 10: return clas.createdAt();
            case 11: return clas.updatedAt();
            default: return "";
        }
    }
}

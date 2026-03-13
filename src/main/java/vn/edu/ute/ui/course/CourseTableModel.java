package vn.edu.ute.ui.course;

import vn.edu.ute.model.Course;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CourseTableModel extends AbstractTableModel {
    private final String[] columns = {"Mã khoá học", "Tên khoá học", "Mô tả", "Level", "Thời lượng", "Đơn vị", "Giá", "Trạng thái", "Thời điểm tạo", "Thời điểm chỉnh sửa", ""};
    private List<Course> data = new ArrayList<>();

    public void setData(List<Course> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public Course getAt(int row) {
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
        Course course = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return course.getCourseId();
            case 1: return course.getCourseName();
            case 2: return course.getDescription();
            case 3: return course.getLevel();
            case 4: return course.getDuration();
            case 5: return course.getDurationUnit();
            case 6: return course.getFee();
            case 7: return course.getStatus();
            case 8: return course.getCreatedAt();
            case 9: return course.getUpdatedAt();
            case 10: return "Tạo lớp học";
            default: return "";
        }
    }
}

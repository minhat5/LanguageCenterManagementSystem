package vn.edu.ute.ui.promotion;

import vn.edu.ute.model.Promotion;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class PromotionTableModel extends AbstractTableModel {
    private final String[] columns = {"Mã KM", "Tên khuyến mãi", "Loại giảm giá", "Mức giảm", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"};
    private List<Promotion> data = new ArrayList<>();

    public void setData(List<Promotion> data) {
        this.data = data;
        fireTableDataChanged();
    }

    public Promotion getAt(int row) {
        if (row < 0 || row >= data.size()) {
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
        Promotion p = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return p.getPromotionId();
            case 1: return p.getPromoName();
            case 2: return p.getDiscountType();
            case 3: return p.getDiscountValue();
            case 4: return p.getStartDate();
            case 5: return p.getEndDate();
            case 6: return p.getStatus();
            default: return "";
        }
    }
}
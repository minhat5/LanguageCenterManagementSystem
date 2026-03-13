package vn.edu.ute.util;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class UIUtils {
    public static <T> void setComboBoxRenderer(JComboBox<T> comboBox, Function<T, String> displayFunction) {
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(displayFunction.apply((T) value));
                } else {
                    setText("Tất cả");
                }
                return this;
            }
        });
    }
}

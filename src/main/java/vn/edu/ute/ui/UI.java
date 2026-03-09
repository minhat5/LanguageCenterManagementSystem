package vn.edu.ute.ui;

import javax.swing.*;

public final class UI {
    private UI() {}

    public static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }
}

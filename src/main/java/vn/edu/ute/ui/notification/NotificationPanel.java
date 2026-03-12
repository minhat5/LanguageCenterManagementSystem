package vn.edu.ute.ui.notification;

import vn.edu.ute.common.enumeration.Role;
import vn.edu.ute.common.enumeration.TargetRole;
import vn.edu.ute.common.session.SessionManager;
import vn.edu.ute.model.Notification;
import vn.edu.ute.model.UserAccount;
import vn.edu.ute.service.NotificationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationPanel extends JPanel {

    private final NotificationService notificationService;
    private JTable table;
    private DefaultTableModel tableModel;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Form components (for Admin to create notification)
    private JPanel formPanel;
    private JTextField txtTitle;
    private JTextArea txtContent;
    private JComboBox<TargetRole> cbTargetRole;
    private JButton btnSend;
    private JButton btnDelete;

    public NotificationPanel(NotificationService notificationService) {
        this.notificationService = notificationService;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initUI();
        loadNotifications();
    }

    private void initUI() {
        // Top section: form for sending notifications (only visible to Admin/Managers)
        UserAccount currentUser = SessionManager.getCurrentUser();
        boolean canManage = currentUser != null && currentUser.getRole() == Role.Admin;

        if (canManage) {
            formPanel = new JPanel(new GridBagLayout());
            formPanel.setBorder(BorderFactory.createTitledBorder("Gửi Thông Báo Mới"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(new JLabel("Tiêu đề:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 0;
            txtTitle = new JTextField(20);
            formPanel.add(txtTitle, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            formPanel.add(new JLabel("Nội dung:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            txtContent = new JTextArea(4, 30);
            txtContent.setLineWrap(true);
            txtContent.setWrapStyleWord(true);
            JScrollPane scrollContent = new JScrollPane(txtContent);
            formPanel.add(scrollContent, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            formPanel.add(new JLabel("Gửi tới:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            cbTargetRole = new JComboBox<>(TargetRole.values());
            formPanel.add(cbTargetRole, gbc);

            // Button Panel
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            btnSend = new JButton("Gửi thông báo");
            btnSend.addActionListener(e -> sendNotification());
            btnPanel.add(btnSend);

            btnDelete = new JButton("Xóa đã chọn");
            btnDelete.addActionListener(e -> deleteNotification());
            btnPanel.add(btnDelete);

            gbc.gridx = 1;
            gbc.gridy = 3;
            formPanel.add(btnPanel, gbc);

            add(formPanel, BorderLayout.NORTH);
        }

        // Center section: table of notifications
        String[] columns = { "ID", "Tiêu đề", "Nội dung", "Gửi tới", "Người gửi", "Thời gian" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh Sách Thông Báo"));
        add(scrollPane, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadNotifications());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadNotifications() {
        try {
            UserAccount currentUser = SessionManager.getCurrentUser();
            if (currentUser == null)
                return;

            tableModel.setRowCount(0);
            List<Notification> notifications;

            if (currentUser.getRole() == Role.Admin) {
                notifications = notificationService.getAllNotifications();
            } else {
                TargetRole mappedRole = mapToTargetRole(currentUser.getRole());
                notifications = notificationService.getNotificationsForRole(mappedRole);
            }

            for (Notification n : notifications) {
                tableModel.addRow(new Object[] {
                        n.getNotificationId(),
                        n.getTitle(),
                        n.getContent(),
                        n.getTargetRole(),
                        n.getCreatedByUser() != null ? n.getCreatedByUser().getUsername() : "Hệ thống",
                        n.getCreatedAt() != null ? n.getCreatedAt().format(formatter) : ""
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải thông báo: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // helps in ide debugging
        }
    }

    private TargetRole mapToTargetRole(Role role) {
        if (role == null)
            return TargetRole.All;
        switch (role) {
            case Student:
                return TargetRole.Student;
            case Teacher:
                return TargetRole.Teacher;
            case Staff:
                return TargetRole.Staff;
            default:
                return TargetRole.All;
        }
    }

    private void sendNotification() {
        String title = txtTitle.getText();
        String content = txtContent.getText();
        TargetRole role = (TargetRole) cbTargetRole.getSelectedItem();

        if (title.isBlank() || content.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tiêu đề và nội dung.");
            return;
        }

        try {
            UserAccount currentUser = SessionManager.getCurrentUser();
            notificationService.sendNotification(title, content, role, currentUser);
            JOptionPane.showMessageDialog(this, "Gửi thông báo thành công!");

            // clear form
            txtTitle.setText("");
            txtContent.setText("");
            cbTargetRole.setSelectedIndex(0);

            loadNotifications();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi gửi thông báo: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteNotification() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Long id = (Long) tableModel.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Chắc chắn xóa thông báo này?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    notificationService.deleteNotification(id);
                    JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                    loadNotifications();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi xóa thông báo: " + ex.getMessage(), "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thông báo để xóa!");
        }
    }
}

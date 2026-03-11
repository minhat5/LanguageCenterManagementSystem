package vn.edu.ute.ui;

import vn.edu.ute.common.enumeration.Status;
import vn.edu.ute.model.Branch;
import vn.edu.ute.model.Room;
import vn.edu.ute.service.BranchService;
import vn.edu.ute.service.RoomService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FacilityManagementPanel extends JPanel {

    private final BranchService branchService;
    private final RoomService roomService;

    // --- Branch Components ---
    private JTable tblBranches;
    private DefaultTableModel branchTableModel;
    private JTextField txtBranchId, txtBranchName, txtAddress, txtPhone;
    private JComboBox<Status> cbBranchStatus;
    private JButton btnAddBranch, btnUpdateBranch, btnDeleteBranch, btnClearBranch;

    // Branch Filters
    private JTextField txtSearchBranch;
    private JComboBox<String> cbFilterBranchStatus;
    private JTextField txtFilterBranchAddress;

    // --- Room Components ---
    private JTable tblRooms;
    private DefaultTableModel roomTableModel;
    private JTextField txtRoomId, txtRoomName, txtCapacity, txtLocation;
    private JComboBox<BranchComboItem> cbRoomBranch;
    private JComboBox<Status> cbRoomStatus;
    private JButton btnAddRoom, btnUpdateRoom, btnDeleteRoom, btnClearRoom;

    // Room Filters
    private JTextField txtSearchRoom;
    private JComboBox<String> cbFilterRoomStatus;
    private JComboBox<String> cbFilterRoomCapacity;

    public FacilityManagementPanel(BranchService branchService, RoomService roomService) {
        this.branchService = branchService;
        this.roomService = roomService;

        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Chi Nhánh", createBranchPanel());
        tabbedPane.addTab("Phòng Học", createRoomPanel());

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                refreshRoomBranchComboBox();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);

        loadBranchData();
        loadRoomData();
    }

    // ============================================
    // BRANCH TAB UI & LOGIC
    // ============================================
    private JPanel createBranchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Pannel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Chi nhánh"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID:"), gbc);
        txtBranchId = new JTextField(); txtBranchId.setEnabled(false);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(txtBranchId, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; formPanel.add(new JLabel("Tên:"), gbc);
        txtBranchName = new JTextField();
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(txtBranchName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(new JLabel("Địa chỉ:"), gbc);
        txtAddress = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3; formPanel.add(txtAddress, gbc);
        gbc.gridwidth = 1; // reset

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("SĐT:"), gbc);
        txtPhone = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtPhone, gbc);

        gbc.gridx = 2; gbc.gridy = 2; formPanel.add(new JLabel("Trạng thái:"), gbc);
        cbBranchStatus = new JComboBox<>(Status.values());
        gbc.gridx = 3; gbc.gridy = 2; formPanel.add(cbBranchStatus, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAddBranch = new JButton("Thêm");
        btnUpdateBranch = new JButton("Cập nhật");
        btnDeleteBranch = new JButton("Xóa");
        btnClearBranch = new JButton("Làm mới");
        btnPanel.add(btnAddBranch); btnPanel.add(btnUpdateBranch);
        btnPanel.add(btnDeleteBranch); btnPanel.add(btnClearBranch);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc Chi nhánh"));
        txtSearchBranch = new JTextField(15);
        txtFilterBranchAddress = new JTextField(10);
        cbFilterBranchStatus = new JComboBox<>(new String[]{"Tất cả", "Active", "Inactive", "Suspended"});
        
        filterPanel.add(new JLabel("Tìm kiếm:")); filterPanel.add(txtSearchBranch);
        filterPanel.add(new JLabel("Trạng thái:")); filterPanel.add(cbFilterBranchStatus);
        filterPanel.add(new JLabel("Địa chỉ:")); filterPanel.add(txtFilterBranchAddress);

        javax.swing.event.DocumentListener branchFilterListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadBranchData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadBranchData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadBranchData(); }
        };
        txtSearchBranch.getDocument().addDocumentListener(branchFilterListener);
        txtFilterBranchAddress.getDocument().addDocumentListener(branchFilterListener);
        cbFilterBranchStatus.addActionListener(e -> loadBranchData());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel southOfTop = new JPanel(new BorderLayout());
        southOfTop.add(btnPanel, BorderLayout.NORTH);
        southOfTop.add(filterPanel, BorderLayout.SOUTH);
        topPanel.add(southOfTop, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        branchTableModel = new DefaultTableModel(new Object[]{"ID", "Tên Chi Nhánh", "Địa Chỉ", "SĐT", "Trạng Thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblBranches = new JTable(branchTableModel);
        tblBranches.getSelectionModel().addListSelectionListener(e -> fillBranchForm());
        panel.add(new JScrollPane(tblBranches), BorderLayout.CENTER);

        // Listeners for Buttons
        btnAddBranch.addActionListener(e -> addBranch());
        btnUpdateBranch.addActionListener(e -> updateBranch());
        btnDeleteBranch.addActionListener(e -> deleteBranch());
        btnClearBranch.addActionListener(e -> clearBranchForm());

        return panel;
    }

    private void fillBranchForm() {
        int row = tblBranches.getSelectedRow();
        if (row >= 0) {
            txtBranchId.setText(tblBranches.getValueAt(row, 0).toString());
            txtBranchName.setText(tblBranches.getValueAt(row, 1).toString());
            txtAddress.setText(tblBranches.getValueAt(row, 2).toString());
            txtPhone.setText(tblBranches.getValueAt(row, 3).toString());
            cbBranchStatus.setSelectedItem(Status.valueOf(tblBranches.getValueAt(row, 4).toString()));
        }
    }

    private void clearBranchForm() {
        txtBranchId.setText("");
        txtBranchName.setText("");
        txtAddress.setText("");
        txtPhone.setText("");
        cbBranchStatus.setSelectedIndex(0);
        tblBranches.clearSelection();
    }

    private void loadBranchData() {
        try {
            branchTableModel.setRowCount(0);
            String keyword = txtSearchBranch != null ? txtSearchBranch.getText() : "";
            String address = txtFilterBranchAddress != null ? txtFilterBranchAddress.getText() : "";
            Status st = null;
            if (cbFilterBranchStatus != null && cbFilterBranchStatus.getSelectedIndex() > 0) {
                st = Status.valueOf(cbFilterBranchStatus.getSelectedItem().toString());
            }

            List<Branch> branches = branchService.filterBranches(keyword, st, address);
            for (Branch b : branches) {
                branchTableModel.addRow(new Object[]{
                        b.getBranchId(), b.getBranchName(), b.getAddress(), b.getPhone(), b.getStatus().name()
                });
            }
            refreshRoomBranchComboBox();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải chi nhánh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addBranch() {
        try {
            if (txtBranchName.getText().isBlank()) throw new Exception("Tên chi nhánh không được để trống");
            Branch b = new Branch();
            b.setBranchName(txtBranchName.getText().trim());
            b.setAddress(txtAddress.getText().trim());
            b.setPhone(txtPhone.getText().trim());
            b.setStatus((Status) cbBranchStatus.getSelectedItem());

            branchService.saveBranch(b);
            JOptionPane.showMessageDialog(this, "Thêm chi nhánh thành công!");
            clearBranchForm();
            loadBranchData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm chi nhánh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBranch() {
        try {
            if (txtBranchId.getText().isBlank()) throw new Exception("Vui lòng chọn chi nhánh cần cập nhật!");
            Long id = Long.parseLong(txtBranchId.getText());
            Branch b = branchService.getBranchById(id);
            if (b == null) throw new Exception("Không tìm thấy chi nhánh!");

            b.setBranchName(txtBranchName.getText().trim());
            b.setAddress(txtAddress.getText().trim());
            b.setPhone(txtPhone.getText().trim());
            b.setStatus((Status) cbBranchStatus.getSelectedItem());

            branchService.saveBranch(b);
            JOptionPane.showMessageDialog(this, "Cập nhật chi nhánh thành công!");
            clearBranchForm();
            loadBranchData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật chi nhánh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBranch() {
        try {
            if (txtBranchId.getText().isBlank()) throw new Exception("Vui lòng chọn chi nhánh cần xóa!");
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa chi nhánh này (và các phòng học liên quan sẽ bị lỗi nếu không set NULL hoặc CASCADE)?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                branchService.deleteBranch(Long.parseLong(txtBranchId.getText()));
                JOptionPane.showMessageDialog(this, "Xóa chi nhánh thành công!");
                clearBranchForm();
                loadBranchData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa chi nhánh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }


    // ============================================
    // ROOM TAB UI & LOGIC
    // ============================================
    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Phòng học"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("ID:"), gbc);
        txtRoomId = new JTextField(); txtRoomId.setEnabled(false);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(txtRoomId, gbc);

        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; formPanel.add(new JLabel("Tên Phòng:"), gbc);
        txtRoomName = new JTextField();
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(txtRoomName, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(new JLabel("Sức chứa:"), gbc);
        txtCapacity = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtCapacity, gbc);

        gbc.gridx = 2; gbc.gridy = 1; formPanel.add(new JLabel("Vị trí:"), gbc);
        txtLocation = new JTextField();
        gbc.gridx = 3; gbc.gridy = 1; formPanel.add(txtLocation, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Chi nhánh:"), gbc);
        cbRoomBranch = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(cbRoomBranch, gbc);

        gbc.gridx = 2; gbc.gridy = 2; formPanel.add(new JLabel("Trạng thái:"), gbc);
        cbRoomStatus = new JComboBox<>(Status.values());
        gbc.gridx = 3; gbc.gridy = 2; formPanel.add(cbRoomStatus, gbc);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAddRoom = new JButton("Thêm");
        btnUpdateRoom = new JButton("Cập nhật");
        btnDeleteRoom = new JButton("Xóa");
        btnClearRoom = new JButton("Làm mới");
        btnPanel.add(btnAddRoom); btnPanel.add(btnUpdateRoom);
        btnPanel.add(btnDeleteRoom); btnPanel.add(btnClearRoom);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Bộ lọc Phòng học"));
        txtSearchRoom = new JTextField(15);
        cbFilterRoomStatus = new JComboBox<>(new String[]{"Tất cả", "Active", "Inactive", "Suspended"});
        cbFilterRoomCapacity = new JComboBox<>(new String[]{"Tất cả", "10", "20", "30", "50", "100"});
        
        filterPanel.add(new JLabel("Tìm kiếm:")); filterPanel.add(txtSearchRoom);
        filterPanel.add(new JLabel("Trạng thái:")); filterPanel.add(cbFilterRoomStatus);
        filterPanel.add(new JLabel("Sức chứa tối thiểu:")); filterPanel.add(cbFilterRoomCapacity);

        txtSearchRoom.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { loadRoomData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { loadRoomData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { loadRoomData(); }
        });
        cbFilterRoomStatus.addActionListener(e -> loadRoomData());
        cbFilterRoomCapacity.addActionListener(e -> loadRoomData());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel southOfTop = new JPanel(new BorderLayout());
        southOfTop.add(btnPanel, BorderLayout.NORTH);
        southOfTop.add(filterPanel, BorderLayout.SOUTH);
        topPanel.add(southOfTop, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table
        roomTableModel = new DefaultTableModel(new Object[]{"ID", "Tên Phòng", "Sức chứa", "Vị trí", "Chi nhánh", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblRooms = new JTable(roomTableModel);
        tblRooms.getSelectionModel().addListSelectionListener(e -> fillRoomForm());
        panel.add(new JScrollPane(tblRooms), BorderLayout.CENTER);

        // Listeners for Buttons
        btnAddRoom.addActionListener(e -> addRoom());
        btnUpdateRoom.addActionListener(e -> updateRoom());
        btnDeleteRoom.addActionListener(e -> deleteRoom());
        btnClearRoom.addActionListener(e -> clearRoomForm());

        return panel;
    }

    private void fillRoomForm() {
        int row = tblRooms.getSelectedRow();
        if (row >= 0) {
            txtRoomId.setText(tblRooms.getValueAt(row, 0).toString());
            txtRoomName.setText(tblRooms.getValueAt(row, 1).toString());
            txtCapacity.setText(tblRooms.getValueAt(row, 2).toString());
            txtLocation.setText(tblRooms.getValueAt(row, 3) != null ? tblRooms.getValueAt(row, 3).toString() : "");

            String branchName = tblRooms.getValueAt(row, 4).toString();
            for (int i = 0; i < cbRoomBranch.getItemCount(); i++) {
                if (cbRoomBranch.getItemAt(i).toString().equals(branchName)) {
                    cbRoomBranch.setSelectedIndex(i);
                    break;
                }
            }
            cbRoomStatus.setSelectedItem(Status.valueOf(tblRooms.getValueAt(row, 5).toString()));
        }
    }

    private void clearRoomForm() {
        txtRoomId.setText("");
        txtRoomName.setText("");
        txtCapacity.setText("");
        txtLocation.setText("");
        if (cbRoomBranch.getItemCount() > 0) cbRoomBranch.setSelectedIndex(0);
        cbRoomStatus.setSelectedIndex(0);
        tblRooms.clearSelection();
    }

    private void loadRoomData() {
        try {
            roomTableModel.setRowCount(0);
            String keyword = txtSearchRoom != null ? txtSearchRoom.getText() : "";
            Status st = null;
            if (cbFilterRoomStatus != null && cbFilterRoomStatus.getSelectedIndex() > 0) {
                st = Status.valueOf(cbFilterRoomStatus.getSelectedItem().toString());
            }
            Integer minCap = null;
            if (cbFilterRoomCapacity != null && cbFilterRoomCapacity.getSelectedIndex() > 0) {
                minCap = Integer.parseInt(cbFilterRoomCapacity.getSelectedItem().toString());
            }

            List<Room> rooms = roomService.filterRooms(keyword, st, minCap);
            for (Room r : rooms) {
                String branchName = r.getBranch() != null ? r.getBranch().getBranchName() : "N/A";
                roomTableModel.addRow(new Object[]{
                        r.getRoomId(), r.getRoomName(), r.getCapacity(), r.getLocation(), branchName, r.getStatus().name()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải phòng học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshRoomBranchComboBox() {
        try {
            cbRoomBranch.removeAllItems();
            List<Branch> branches = branchService.getAllBranches();
            for (Branch b : branches) {
                cbRoomBranch.addItem(new BranchComboItem(b.getBranchId(), b.getBranchName()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addRoom() {
        try {
            if (txtRoomName.getText().isBlank()) throw new Exception("Tên phòng không được để trống");
            int capacity = 0;
            try { capacity = Integer.parseInt(txtCapacity.getText().trim()); } catch (NumberFormatException e) { throw new Exception("Sức chứa phải là số"); }
            if (cbRoomBranch.getSelectedItem() == null) throw new Exception("Vui lòng chọn chi nhánh cho phòng học");

            Room r = new Room();
            r.setRoomName(txtRoomName.getText().trim());
            r.setCapacity(capacity);
            r.setLocation(txtLocation.getText().trim());
            
            BranchComboItem selectedBranchItem = (BranchComboItem) cbRoomBranch.getSelectedItem();
            Branch b = branchService.getBranchById(selectedBranchItem.getId());
            r.setBranch(b);
            r.setStatus((Status) cbRoomStatus.getSelectedItem());

            roomService.saveRoom(r);
            JOptionPane.showMessageDialog(this, "Thêm phòng học thành công!");
            clearRoomForm();
            loadRoomData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm phòng học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRoom() {
        try {
            if (txtRoomId.getText().isBlank()) throw new Exception("Vui lòng chọn phòng cần cập nhật!");
            Long id = Long.parseLong(txtRoomId.getText());
            Room r = roomService.getRoomById(id);
            if (r == null) throw new Exception("Không tìm thấy phòng học!");

            int capacity = 0;
            try { capacity = Integer.parseInt(txtCapacity.getText().trim()); } catch (NumberFormatException e) { throw new Exception("Sức chứa phải là số"); }
            if (cbRoomBranch.getSelectedItem() == null) throw new Exception("Vui lòng chọn chi nhánh");

            r.setRoomName(txtRoomName.getText().trim());
            r.setCapacity(capacity);
            r.setLocation(txtLocation.getText().trim());

            BranchComboItem selectedBranchItem = (BranchComboItem) cbRoomBranch.getSelectedItem();
            Branch b = branchService.getBranchById(selectedBranchItem.getId());
            r.setBranch(b);
            r.setStatus((Status) cbRoomStatus.getSelectedItem());

            roomService.saveRoom(r);
            JOptionPane.showMessageDialog(this, "Cập nhật phòng học thành công!");
            clearRoomForm();
            loadRoomData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật phòng học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRoom() {
        try {
            if (txtRoomId.getText().isBlank()) throw new Exception("Vui lòng chọn phòng học cần xóa!");
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phòng học này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                roomService.deleteRoom(Long.parseLong(txtRoomId.getText()));
                JOptionPane.showMessageDialog(this, "Xóa phòng học thành công!");
                clearRoomForm();
                loadRoomData();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi xóa phòng học: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper class for Branch ComboBox
    private static class BranchComboItem {
        private final Long id;
        private final String name;

        public BranchComboItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        
        @Override
        public String toString() { return name; }
    }
}

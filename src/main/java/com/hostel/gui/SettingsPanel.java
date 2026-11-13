package com.hostel.gui;

import com.hostel.dao.AttendanceDAO;
import com.hostel.dao.RoomDAO;
import com.hostel.dao.StudentDAO;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SettingsPanel extends JPanel {
    private AttendanceDAO attendanceDAO;
    private StudentDAO studentDAO;
    private RoomDAO roomDAO;
    private String hostelType;
    private Integer hostelId;
    private JButton undoBtn;
    private Runnable logoutAction;
    private String username;
    private RoomPanel roomPanel; // To refresh cards when rooms change

    // Room Admin components
    private JComboBox<String> roomCombo;
    private Map<Integer, String> roomMap;

    public SettingsPanel(String hostelType, Integer hostelId, String username, Runnable logoutAction, RoomPanel roomPanel) {
        this.hostelType = hostelType;
        this.hostelId = hostelId;
        this.username = username;
        this.logoutAction = logoutAction;
        this.roomPanel = this.roomPanel;

        this.attendanceDAO = new AttendanceDAO();
        this.studentDAO = new StudentDAO();
        this.roomDAO = new RoomDAO();
        this.roomMap = new LinkedHashMap<>();

        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        // ----- Title bar -----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UITheme.PRIMARY);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("System Settings");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.WHITE);
        titleBar.add(title, BorderLayout.WEST);
        add(titleBar, BorderLayout.NORTH);

        // ----- Main Content (Centered) -----
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(UITheme.BACKGROUND);

        JPanel settingsBox = new JPanel();
        settingsBox.setLayout(new BoxLayout(settingsBox, BoxLayout.Y_AXIS));
        settingsBox.setBackground(UITheme.BACKGROUND);
        settingsBox.setPreferredSize(new Dimension(650, 750)); // Taller to fit new section

        // ==========================================
        // SECTION 1: Account Profile
        // ==========================================
        settingsBox.add(createSectionHeader("👤 Account Profile"));

        JPanel accountPanel = createCardPanel();
        accountPanel.setLayout(new BorderLayout(15, 0));

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("SansSerif", Font.PLAIN, 40));
        userIcon.setForeground(UITheme.GRAY);

        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setBackground(UITheme.CARD_BG);
        JLabel nameLbl = new JLabel("Logged in as: " + username);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLbl.setForeground(UITheme.PRIMARY_DARK);
        JLabel roleLbl = new JLabel("Role: " + ("admin".equals(username) ? "Administrator" : "Hostel Warden"));
        roleLbl.setFont(UITheme.BODY);
        roleLbl.setForeground(Color.GRAY);
        userInfo.add(nameLbl);
        userInfo.add(roleLbl);

        JButton logoutBtn = UITheme.createButton("Logout", UITheme.DANGER);
        logoutBtn.setPreferredSize(new Dimension(100, 35));
        logoutBtn.addActionListener(e -> {
            if (logoutAction != null) logoutAction.run();
        });

        JPanel logoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        logoutWrapper.setBackground(UITheme.CARD_BG);
        logoutWrapper.add(logoutBtn);

        accountPanel.add(userIcon, BorderLayout.WEST);
        accountPanel.add(userInfo, BorderLayout.CENTER);
        accountPanel.add(logoutWrapper, BorderLayout.EAST);

        settingsBox.add(accountPanel);
        settingsBox.add(Box.createRigidArea(new Dimension(0, 20)));

        // ==========================================
        // SECTION 2: Room Administration (NEW)
        // ==========================================
        settingsBox.add(createSectionHeader("🚪 Room Administration"));

        JPanel roomAdminPanel = createCardPanel();
        roomAdminPanel.setLayout(new BorderLayout(10, 10));

        JPanel roomSelectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        roomSelectPanel.setBackground(UITheme.CARD_BG);

        roomCombo = new JComboBox<>();
        roomCombo.setPreferredSize(new Dimension(250, 32));
        roomCombo.setFont(UITheme.BODY);
        loadRoomCombo(); // Populate dropdown

        roomSelectPanel.add(new JLabel("Select Room:"));
        roomSelectPanel.add(roomCombo);

        JPanel roomBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        roomBtnPanel.setBackground(UITheme.CARD_BG);

        JButton addRoomBtn = UITheme.createButton("+ Add", UITheme.PRIMARY);
        JButton editRoomBtn = UITheme.createButton("✎ Edit", UITheme.INFO);
        JButton deleteRoomBtn = UITheme.createButton("✕ Delete", UITheme.DANGER);

        addRoomBtn.addActionListener(e -> openAddRoomDialog());
        editRoomBtn.addActionListener(e -> openEditRoomDialog());
        deleteRoomBtn.addActionListener(e -> openDeleteRoomDialog());

        roomBtnPanel.add(addRoomBtn);
        roomBtnPanel.add(editRoomBtn);
        roomBtnPanel.add(deleteRoomBtn);

        roomAdminPanel.add(roomSelectPanel, BorderLayout.WEST);
        roomAdminPanel.add(roomBtnPanel, BorderLayout.EAST);

        settingsBox.add(roomAdminPanel);
        settingsBox.add(Box.createRigidArea(new Dimension(0, 20)));

        // ==========================================
        // SECTION 3: Data Management
        // ==========================================
        settingsBox.add(createSectionHeader("💾 Data Management"));

        JPanel dataPanel = createCardPanel();
        dataPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton exportBtn = UITheme.createButton("📊 Export", UITheme.PRIMARY);
        exportBtn.addActionListener(e -> exportAllData());
        dataPanel.add(createActionRow("Export All Data", "Download a full Excel backup.", exportBtn));

        undoBtn = UITheme.createButton("↩ Undo", UITheme.WARNING);
        undoBtn.addActionListener(e -> undoReset());
        undoBtn.setVisible(false);
        dataPanel.add(createActionRow("Undo Last Reset", "Restore attendance data.", undoBtn));

        JButton resetBtn = UITheme.createButton("⚠ Reset", UITheme.DANGER);
        resetBtn.addActionListener(e -> resetData());
        dataPanel.add(createActionRow("Reset Attendance", "Clear all attendance logs.", resetBtn));

        settingsBox.add(dataPanel);
        settingsBox.add(Box.createRigidArea(new Dimension(0, 20)));

        // ==========================================
        // SECTION 4: About System
        // ==========================================
        settingsBox.add(createSectionHeader("About System"));
        JPanel aboutPanel = createCardPanel();
        aboutPanel.setLayout(new BorderLayout(5, 5));

        JLabel appName = new JLabel("Smart Hostel Management System v2.0");
        appName.setFont(new Font("SansSerif", Font.BOLD, 14));
        appName.setForeground(UITheme.PRIMARY_DARK);

        JLabel appDesc = new JLabel("<html>A comprehensive offline desktop solution for modern hostel wardens.<br>" +
                "Features: Student tracking, Room assignment, Daily roll-call, and Excel reporting. <br><Strong>Developed By Soe Moe Naing<Strong></html>");
        appDesc.setFont(UITheme.BODY);
        appDesc.setForeground(Color.GRAY);

        aboutPanel.add(appName, BorderLayout.NORTH);
        aboutPanel.add(appDesc, BorderLayout.CENTER);
        settingsBox.add(aboutPanel);

        centerWrapper.add(settingsBox);

        // Add scroll pane just in case the screen is small
        JScrollPane scrollPane = new JScrollPane(centerWrapper);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- ROOM ADMIN LOGIC ---
    private void loadRoomCombo() {
        roomCombo.removeAllItems();
        roomMap.clear();
        List<Object[]> rooms = roomDAO.getAllRooms(hostelId);
        for (Object[] r : rooms) {
            int id = (int) r[0];
            String num = (String) r[1];
            String hostel = (String) r[4];
            String display = "Room " + num + " (" + hostel + ")";
            roomMap.put(id, display);
            roomCombo.addItem(display);
        }
    }

    private void openAddRoomDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddRoomDialog dialog = new AddRoomDialog(parent, hostelId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            roomDAO.insertRoom(dialog.getRoomNumber(), dialog.getFloorNumber(),
                    dialog.getCapacity(), dialog.getHostelId());
            loadRoomCombo();
            roomPanel.refreshRoomCards();
        }
    }

    private void openEditRoomDialog() {
        if (roomCombo.getSelectedIndex() == -1) return;
        String selectedDisplay = (String) roomCombo.getSelectedItem();
        int roomId = -1;
        for (Map.Entry<Integer, String> entry : roomMap.entrySet()) {
            if (entry.getValue().equals(selectedDisplay)) { roomId = entry.getKey(); break; }
        }
        if (roomId == -1) return;

        List<Object[]> rooms = roomDAO.getAllRooms(hostelId);
        for (Object[] r : rooms) {
            if ((int)r[0] == roomId) {
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
                EditRoomDialog dialog = new EditRoomDialog(parent, roomId, (String)r[1], (int)r[2], (int)r[3], (String)r[4], (String)r[5], hostelId);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    roomDAO.updateRoom(roomId, dialog.getRoomNumber(), dialog.getFloorNumber(), dialog.getCapacity(), dialog.getHostelId());
                    loadRoomCombo();
                    roomPanel.refreshRoomCards();
                }
                break;
            }
        }
    }

    private void openDeleteRoomDialog() {
        if (roomCombo.getSelectedIndex() == -1) return;
        String selectedDisplay = (String) roomCombo.getSelectedItem();
        int roomId = -1;
        for (Map.Entry<Integer, String> entry : roomMap.entrySet()) {
            if (entry.getValue().equals(selectedDisplay)) { roomId = entry.getKey(); break; }
        }
        if (roomId == -1) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete " + selectedDisplay + "? All students will be unassigned.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            roomDAO.deleteRoom(roomId);
            loadRoomCombo();
            roomPanel.refreshRoomCards();
        }
    }

    // --- UI HELPERS ---
    private JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(UITheme.PRIMARY_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 10, 0));
        return label;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.CARD_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        panel.setMaximumSize(new Dimension(650, 1000));
        return panel;
    }

    private JPanel createActionRow(String title, String desc, JButton btn) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(UITheme.CARD_BG);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(UITheme.CARD_BG);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLbl.setForeground(Color.BLACK);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLbl.setForeground(Color.GRAY);

        textPanel.add(titleLbl);
        textPanel.add(descLbl);

        btn.setPreferredSize(new Dimension(120, 35));

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
        btnWrapper.setBackground(UITheme.CARD_BG);
        btnWrapper.add(btn);

        row.add(textPanel, BorderLayout.CENTER);
        row.add(btnWrapper, BorderLayout.EAST);
        return row;
    }

    // --- EXPORT & RESET LOGIC (Unchanged) ---
    private void exportAllData() { /* ... same as before ... */ }
    private void createExcelSheet(Sheet sheet, String[] headers, List<Object[]> data, CellStyle headerStyle) { /* ... */ }
    private void resetData() { /* ... */ }
    private void undoReset() { /* ... */ }
}
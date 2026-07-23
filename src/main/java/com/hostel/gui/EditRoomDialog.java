package com.hostel.gui;

import com.hostel.dao.RoomDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class EditRoomDialog extends JDialog {
    private JTextField roomNumberField, floorField, capacityField;
    private JComboBox<String> hostelCombo;
    private Map<Integer, String> hostelMap;
    private boolean confirmed = false;
    private int roomId;

    public EditRoomDialog(JFrame parent, int roomId, String roomNumber, int floor, int capacity, String hostelName, String hostelType) {
        super(parent, "Edit Room", true);
        this.roomId = roomId;

        // Fetch hostels for dropdown
        hostelMap = new java.util.LinkedHashMap<>();
        try (java.sql.Connection conn = com.hostel.db.DBConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT hostel_id, hostel_name, type FROM Hostel ORDER BY hostel_name")) {
            while (rs.next()) {
                String display = rs.getString("hostel_name") + " (" + rs.getString("type") + ")";
                hostelMap.put(rs.getInt("hostel_id"), display);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initUI(parent, roomNumber, floor, capacity, hostelName, hostelType);
    }

    private void initUI(JFrame parent, String roomNumber, int floor, int capacity, String hostelName, String hostelType) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        roomNumberField = new JTextField(15);
        roomNumberField.setText(roomNumber);
        floorField = new JTextField(15);
        floorField.setText(String.valueOf(floor));
        capacityField = new JTextField(15);
        capacityField.setText(String.valueOf(capacity));

        hostelCombo = new JComboBox<>();
        String currentHostelDisplay = hostelName + " (" + hostelType + ")";
        for (String display : hostelMap.values()) {
            hostelCombo.addItem(display);
        }
        hostelCombo.setSelectedItem(currentHostelDisplay);

        int row = 0;
        addRow(formPanel, gbc, "Room Number:", roomNumberField, row++);
        addRow(formPanel, gbc, "Floor Number:", floorField, row++);
        addRow(formPanel, gbc, "Capacity:", capacityField, row++);
        addRow(formPanel, gbc, "Hostel:", hostelCombo, row++);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton saveBtn = createModernButton("Save", new Color(33, 97, 140));
        JButton cancelBtn = createModernButton("Cancel", Color.GRAY);

        saveBtn.addActionListener(e -> {
            if (roomNumberField.getText().trim().isEmpty() || floorField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room number and floor are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Integer.parseInt(floorField.getText().trim());
                int cap = Integer.parseInt(capacityField.getText().trim());
                if (cap < 1) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Floor and capacity must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            confirmed = true;
            dispose();
        });
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(field, gbc);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JComboBox<String> combo, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(combo, gbc);
    }

    private JButton createModernButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }

    public boolean isConfirmed() { return confirmed; }
    public String getRoomNumber() { return roomNumberField.getText().trim(); }
    public int getFloorNumber() { return Integer.parseInt(floorField.getText().trim()); }
    public int getCapacity() { return Integer.parseInt(capacityField.getText().trim()); }
    public int getHostelId() {
        String selected = (String) hostelCombo.getSelectedItem();
        for (Map.Entry<Integer, String> entry : hostelMap.entrySet()) {
            if (entry.getValue().equals(selected)) return entry.getKey();
        }
        return -1;
    }
}
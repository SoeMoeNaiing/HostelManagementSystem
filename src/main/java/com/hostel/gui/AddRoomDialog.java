package com.hostel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class AddRoomDialog extends JDialog {
    private JTextField roomNumberField, floorField, capacityField;
    private JComboBox<String> hostelCombo;
    private Map<Integer, String> hostelMap;
    private boolean confirmed = false;
    private Integer lockedHostelId;   // non-null = warden's hostel

    public AddRoomDialog(JFrame parent, Integer lockedHostelId) {
        super(parent, "Add New Room", true);
        this.lockedHostelId = lockedHostelId;

        // Load hostels
        hostelMap = new java.util.LinkedHashMap<>();
        try (java.sql.Connection conn = com.hostel.db.DBConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(
                     "SELECT hostel_id, hostel_name, type FROM Hostel ORDER BY hostel_name")) {
            while (rs.next()) {
                String display = rs.getString("hostel_name") + " (" + rs.getString("type") + ")";
                hostelMap.put(rs.getInt("hostel_id"), display);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        initUI(parent);
    }

    private void initUI(JFrame parent) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UITheme.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        roomNumberField = createField();
        floorField = createField();
        capacityField = createField();
        capacityField.setText("2");

        hostelCombo = new JComboBox<>();
        for (String display : hostelMap.values()) hostelCombo.addItem(display);
        hostelCombo.setFont(UITheme.BODY);
        hostelCombo.setPreferredSize(new Dimension(200, UITheme.INPUT_H));

        // If warden (lockedHostelId provided), select their hostel and disable
        if (lockedHostelId != null) {
            String lockedDisplay = hostelMap.get(lockedHostelId);
            if (lockedDisplay != null) hostelCombo.setSelectedItem(lockedDisplay);
            hostelCombo.setEnabled(false);
        }

        int row = 0;
        addRow(formPanel, gbc, "Room Number:", roomNumberField, row++);
        addRow(formPanel, gbc, "Floor Number:", floorField, row++);
        addRow(formPanel, gbc, "Capacity:", capacityField, row++);
        addRow(formPanel, gbc, "Hostel:", hostelCombo, row++);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(UITheme.WHITE);
        JButton saveBtn = UITheme.createButton("Save", UITheme.PRIMARY);
        JButton cancelBtn = UITheme.createButton("Cancel", UITheme.GRAY);

        saveBtn.addActionListener(e -> {
            if (roomNumberField.getText().trim().isEmpty() || floorField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room number and floor are required!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Integer.parseInt(floorField.getText().trim());
                int cap = Integer.parseInt(capacityField.getText().trim());
                if (cap < 1) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Floor and capacity must be valid numbers.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
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

    private JTextField createField() {
        JTextField field = new JTextField(15);
        field.setFont(UITheme.BODY);
        field.setPreferredSize(new Dimension(200, UITheme.INPUT_H));
        return field;
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JTextField field, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.BODY);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(field, gbc);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String label, JComboBox<String> combo, int row) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.2;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.BODY);
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        panel.add(combo, gbc);
    }

    // ---- getters ----
    public boolean isConfirmed() { return confirmed; }
    public String getRoomNumber() { return roomNumberField.getText().trim(); }
    public int getFloorNumber() { return Integer.parseInt(floorField.getText().trim()); }
    public int getCapacity() { return Integer.parseInt(capacityField.getText().trim()); }

    /** Returns the locked hostel ID if warden, otherwise the selected one. */
    public int getHostelId() {
        if (lockedHostelId != null) return lockedHostelId;
        String selected = (String) hostelCombo.getSelectedItem();
        for (Map.Entry<Integer, String> entry : hostelMap.entrySet()) {
            if (entry.getValue().equals(selected)) return entry.getKey();
        }
        return -1;
    }
}
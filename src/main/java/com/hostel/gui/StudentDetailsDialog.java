package com.hostel.gui;

import com.hostel.dao.RoomDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class StudentDetailsDialog extends JDialog {

    public StudentDetailsDialog(JFrame parent, Object[] studentData) {
        super(parent, "Student Details", true);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Student Details");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(33, 97, 140));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Extract data (index matches StudentDAO.getStudentById)
        String studentId = (String) studentData[0];
        String name = (String) studentData[1];
        String gender = (String) studentData[2];
        String year = (String) studentData[3];
        String major = (String) studentData[4];
        String email = (String) studentData[5];
        String phone = (String) studentData[6];
        String guardianName = (String) studentData[7];
        String guardianPhone = (String) studentData[8];
        String address = (String) studentData[9];
        String nrc = (String) studentData[10];
        Integer roomId = (Integer) studentData[11];   // can be null

        // Room display (if assigned)
        String roomDisplay = "Not Assigned";
        if (roomId != null) {
            RoomDAO roomDAO = new RoomDAO();
            Map<Integer, String> roomMap = roomDAO.getRoomMap();
            if (roomMap.containsKey(roomId)) {
                roomDisplay = roomMap.get(roomId);
            }
        }

        int row = 0;
        addRow(detailsPanel, gbc, "Student ID:", studentId, row++);
        addRow(detailsPanel, gbc, "Name:", name, row++);
        addRow(detailsPanel, gbc, "Gender:", gender, row++);
        addRow(detailsPanel, gbc, "Year:", year, row++);
        addRow(detailsPanel, gbc, "Major:", major, row++);
        addRow(detailsPanel, gbc, "Email:", email, row++);
        addRow(detailsPanel, gbc, "Phone:", phone, row++);
        addRow(detailsPanel, gbc, "Guardian Name:", guardianName, row++);
        addRow(detailsPanel, gbc, "Guardian Phone:", guardianPhone, row++);
        addRow(detailsPanel, gbc, "Address:", address, row++);
        addRow(detailsPanel, gbc, "NRC:", nrc, row++);
        addRow(detailsPanel, gbc, "Assigned Room:", roomDisplay, row++);

        mainPanel.add(detailsPanel, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(33, 97, 140));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setOpaque(true);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.setBorder(new EmptyBorder(8, 20, 8, 20));
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(val, gbc);
    }
}
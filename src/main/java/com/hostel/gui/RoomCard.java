package com.hostel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class RoomCard extends JPanel {
    // Card background states
    private static final Color VACANT_BG = new Color(248, 250, 252);
    private static final Color PENDING_BG = new Color(255, 248, 200);
    private static final Color ALL_PRESENT_BG = new Color(215, 250, 220);

    // Bed square colors
    private static final Color OCCUPIED_BED = UITheme.SUCCESS;
    private static final Color EMPTY_BED = new Color(220, 225, 230);

    // Border
    private static final Color BORDER_NORMAL = UITheme.BORDER;
    private static final Color BORDER_SELECTED = UITheme.PRIMARY;

    private final int roomId;
    private final int floor;
    private final int capacity;
    private final String roomNumber;
    private final String hostelName;
    private final String hostelType;

    private JPanel bedPanel;
    private boolean isSelected = false;

    public RoomCard(int roomId, String roomNumber, int floor, int capacity,
                    String hostelName, String hostelType) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.capacity = capacity;
        this.hostelName = hostelName;
        this.hostelType = hostelType;

        setLayout(new BorderLayout(0, 6));
        applyBorder(false);
        setBackground(VACANT_BG);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Room number
        JLabel roomLabel = new JLabel("Room " + roomNumber);
        roomLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        roomLabel.setForeground(UITheme.PRIMARY_DARK);

        // Floor & hostel info
        JLabel infoLabel = new JLabel("Floor " + floor + " – " + hostelName);
        infoLabel.setFont(UITheme.SMALL);
        infoLabel.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel(new BorderLayout(0, 2));
        textPanel.setOpaque(false);
        textPanel.add(roomLabel, BorderLayout.NORTH);
        textPanel.add(infoLabel, BorderLayout.SOUTH);

        // Bed squares
        bedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        bedPanel.setOpaque(false);
        updateBeds(0, capacity);

        add(textPanel, BorderLayout.NORTH);
        add(bedPanel, BorderLayout.SOUTH);
    }

    /** Border thickness constant, so selection never shifts layout. */
    private void applyBorder(boolean selected) {
        Color color = selected ? BORDER_SELECTED : BORDER_NORMAL;
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(color, 2, true),
                new EmptyBorder(9, 9, 9, 9)));
    }

    public void updateBeds(int occupied, int totalCapacity) {
        bedPanel.removeAll();
        for (int i = 0; i < totalCapacity; i++) {
            JLabel square = new JLabel();
            square.setOpaque(true);
            square.setPreferredSize(new Dimension(16, 16));
            square.setBackground(i < occupied ? OCCUPIED_BED : EMPTY_BED);
            square.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            bedPanel.add(square);
        }
        bedPanel.revalidate();
        bedPanel.repaint();
    }

    public void updateAttendanceStatus(int present, int total) {
        if (total == 0) {
            setBackground(VACANT_BG);
        } else if (present == total) {
            setBackground(ALL_PRESENT_BG);
        } else {
            setBackground(PENDING_BG);
        }
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
        applyBorder(selected);
    }

    public boolean isSelected() { return isSelected; }
    public int getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public int getFloor() { return floor; }
    public String getHostelName() { return hostelName; }
    public String getHostelType() { return hostelType; }
    public int getCapacity() { return capacity; }
}
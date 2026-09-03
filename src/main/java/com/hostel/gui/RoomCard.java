package com.hostel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoomCard extends JPanel {
    private static final Color VACANT_BG = new Color(245, 248, 250);
    private static final Color PENDING_BG = new Color(255, 248, 200);
    private static final Color ALL_PRESENT_BG = new Color(200, 255, 200);
    private static final Color BORDER_COLOR = new Color(200, 210, 220);
    private static final Color SELECTED_BORDER_COLOR = new Color(33, 97, 140);
    private static final Color OCCUPIED_BED = new Color(46, 204, 113);
    private static final Color EMPTY_BED = new Color(220, 225, 230);

    private int roomId, floor, capacity, totalAssigned;
    private String roomNumber, hostelName, hostelType;
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

        setLayout(new BorderLayout());
        // Always 2px border, same padding -> no size change when selected
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 2, true),
                new EmptyBorder(9, 9, 9, 9)));
        setBackground(VACANT_BG);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel roomLabel = new JLabel("Room " + roomNumber);
        roomLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        roomLabel.setForeground(new Color(33, 97, 140));
        add(roomLabel, BorderLayout.NORTH);

        JLabel infoLabel = new JLabel("Floor " + floor + " – " + hostelName);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        infoLabel.setForeground(Color.GRAY);
        add(infoLabel, BorderLayout.CENTER);

        bedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        bedPanel.setOpaque(false);
        updateBeds(0, capacity);
        add(bedPanel, BorderLayout.SOUTH);
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
        this.totalAssigned = total;
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
        if (selected) {
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(SELECTED_BORDER_COLOR, 2, true),
                    new EmptyBorder(9, 9, 9, 9)));
        } else {
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 2, true),
                    new EmptyBorder(9, 9, 9, 9)));
        }
    }

    public boolean isSelected() { return isSelected; }
    public int getRoomId() { return roomId; }
    public String getRoomNumber() { return roomNumber; }
    public int getFloor() { return floor; }
    public String getHostelName() { return hostelName; }
    public String getHostelType() { return hostelType; }
    public int getCapacity() { return capacity; }
}
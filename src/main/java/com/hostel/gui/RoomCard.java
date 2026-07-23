package com.hostel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoomCard extends JPanel {
    private static final Color VACANT_BG = new Color(245, 248, 250);   // light grey-blue
    private static final Color PENDING_BG = new Color(255, 248, 200);  // soft yellow
    private static final Color ALL_PRESENT_BG = new Color(200, 255, 200); // soft green
    private static final Color BORDER_COLOR = new Color(200, 210, 220);
    private boolean isSelected = false;
    private int roomId;
    private String roomNumber;
    private int floor;
    private int capacity;
    private String hostelName;
    private String hostelType;
    private int presentCount = 0;
    private int totalAssigned = 0;

    // Bed squares
    private JPanel bedPanel;

    public RoomCard(int roomId, String roomNumber, int floor, int capacity,
                    String hostelName, String hostelType) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.capacity = capacity;
        this.hostelName = hostelName;
        this.hostelType = hostelType;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        setBackground(VACANT_BG);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Room number
        JLabel roomLabel = new JLabel("Room " + roomNumber);
        roomLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        roomLabel.setForeground(new Color(33, 97, 140));
        add(roomLabel, BorderLayout.NORTH);

        // Floor & hostel info
        JLabel infoLabel = new JLabel("Floor " + floor + " – " + hostelName);
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        infoLabel.setForeground(Color.GRAY);
        add(infoLabel, BorderLayout.CENTER);

        // Bed squares panel
        bedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        bedPanel.setOpaque(false);
        updateBeds(0, capacity);   // initially all empty
        add(bedPanel, BorderLayout.SOUTH);
    }

    /**
     * Update the bed squares based on occupied count and total capacity.
     * occupied = number of assigned students.
     */
    public void updateBeds(int occupied, int totalCapacity) {
        bedPanel.removeAll();
        for (int i = 0; i < totalCapacity; i++) {
            JLabel square = new JLabel();
            square.setOpaque(true);
            square.setPreferredSize(new Dimension(16, 16));
            if (i < occupied) {
                square.setBackground(new Color(46, 204, 113)); // green
            } else {
                square.setBackground(new Color(220, 225, 230)); // light grey
            }
            square.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
            bedPanel.add(square);
        }
        bedPanel.revalidate();
        bedPanel.repaint();
    }

    /**
     * Update the card background based on attendance status.
     * present = number of students marked present today.
     * total = number of students assigned to the room.
     */
    public void updateAttendanceStatus(int present, int total) {
        this.presentCount = present;
        this.totalAssigned = total;
        if (total == 0) {
            setBackground(VACANT_BG);
        } else if (present == total) {
            setBackground(ALL_PRESENT_BG);
        } else {
            setBackground(PENDING_BG);
        }
    }

    // Add a click listener from outside
    /**
     * Add a click listener that runs the given action when the card is clicked.
     */
    public void addCardClickListener(Runnable action) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
    }
    /**
     * Highlight or unhighlight the card to show it is selected.
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        if (selected) {
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(33, 97, 140), 2, true),   // thick blue border
                    new EmptyBorder(10, 10, 10, 10)));
        } else {
            // Restore default border
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(new Color(200, 210, 220), 1, true),
                    new EmptyBorder(10, 10, 10, 10)));
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
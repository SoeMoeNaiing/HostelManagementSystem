package com.hostel.gui;

import com.hostel.dao.RoomDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class RoomPanel extends JPanel {
    private RoomDAO roomDAO;
    private JPanel cardGrid;
    private int selectedRoomId = -1;
    private RoomCard selectedCard = null;

    private JButton editRoomBtn;
    private JButton deleteRoomBtn;
    private Integer hostelId;
    private String hostelType;

    public RoomPanel(Integer hostelId, String hostelType) {
        this.hostelId = hostelId;
        this.hostelType = hostelType;

        roomDAO = new RoomDAO();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        // ----- Title bar -----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UITheme.PRIMARY);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("Room Management");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        // ----- Toolbar -----
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.setBackground(UITheme.BACKGROUND);

        JButton addRoomBtn = UITheme.createButton("+ Add Room", UITheme.PRIMARY);
        editRoomBtn = UITheme.createButton("✎ Edit Room", UITheme.PRIMARY);
        deleteRoomBtn = UITheme.createButton("✕ Delete Room", UITheme.DANGER);
        JButton refreshBtn = UITheme.createButton("↻ Refresh", UITheme.GRAY);

        editRoomBtn.setEnabled(false);
        deleteRoomBtn.setEnabled(false);

        toolbar.add(addRoomBtn);
        toolbar.add(editRoomBtn);
        toolbar.add(deleteRoomBtn);
        toolbar.add(refreshBtn);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(UITheme.BACKGROUND);
        northPanel.add(titleBar, BorderLayout.NORTH);
        northPanel.add(toolbar, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // ----- Card grid -----
        cardGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        cardGrid.setBackground(UITheme.BACKGROUND);
        cardGrid.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(cardGrid,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(UITheme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Click blank area of grid → deselect
        cardGrid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == cardGrid) {
                    clearSelection();
                }
            }
        });

        // ----- Actions -----
        addRoomBtn.addActionListener(e -> openAddRoomDialog());
        editRoomBtn.addActionListener(e -> openEditRoomDialog());
        deleteRoomBtn.addActionListener(e -> deleteSelectedRoom());
        refreshBtn.addActionListener(e -> refreshRoomCards());

        refreshRoomCards();
    }

    private void clearSelection() {
        if (selectedCard != null) {
            selectedCard.setSelected(false);
            selectedCard = null;
        }
        selectedRoomId = -1;
        editRoomBtn.setEnabled(false);
        deleteRoomBtn.setEnabled(false);
    }

    private void openAddRoomDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddRoomDialog dialog = new AddRoomDialog(parent, hostelId);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            boolean ok = roomDAO.insertRoom(dialog.getRoomNumber(), dialog.getFloorNumber(),
                    dialog.getCapacity(), dialog.getHostelId());
            if (ok) {
                refreshRoomCards();
                JOptionPane.showMessageDialog(this, "Room added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add room.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openEditRoomDialog() {
        if (selectedRoomId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room first.");
            return;
        }
        List<Object[]> rooms = roomDAO.getAllRooms(hostelId);
        for (Object[] r : rooms) {
            int id = (int) r[0];
            if (id == selectedRoomId) {
                String roomNumber = (String) r[1];
                int floor = (int) r[2];
                int capacity = (int) r[3];
                String hostelName = (String) r[4];
                String hostelType = (String) r[5];

                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
                EditRoomDialog dialog = new EditRoomDialog(parent, id, roomNumber, floor, capacity, hostelName, hostelType, hostelId);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    boolean ok = roomDAO.updateRoom(id, dialog.getRoomNumber(), dialog.getFloorNumber(),
                            dialog.getCapacity(), dialog.getHostelId());
                    if (ok) {
                        refreshRoomCards();
                        JOptionPane.showMessageDialog(this, "Room updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update room.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                return;
            }
        }
    }

    private void deleteSelectedRoom() {
        if (selectedRoomId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this room? All students will be unassigned first.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = roomDAO.deleteRoom(selectedRoomId);
            if (ok) {
                clearSelection();
                refreshRoomCards();
                JOptionPane.showMessageDialog(this, "Room deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openRoomDetails() {
        if (selectedRoomId == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room first.");
            return;
        }
        List<Object[]> rooms = roomDAO.getAllRooms(hostelId);
        for (Object[] r : rooms) {
            int id = (int) r[0];
            if (id == selectedRoomId) {
                String roomNumber = (String) r[1];
                String hostelName = (String) r[4];
                JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(RoomPanel.this);
                RoomDetailsDialog dialog = new RoomDetailsDialog(parent, id,
                        roomNumber + " (" + hostelName + ")", this::refreshRoomCards, hostelType);
                dialog.setVisible(true);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Selected room not found.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void refreshRoomCards() {
        clearSelection();

        cardGrid.removeAll();
        // N+1 fix: getAllRoomsWithOccupancy returns rooms + occupancy in one query
        List<Object[]> rooms = roomDAO.getAllRoomsWithOccupancy(hostelId);
        for (Object[] r : rooms) {
            int roomId = (int) r[0];
            String roomNumber = (String) r[1];
            int floor = (int) r[2];
            int capacity = (int) r[3];
            String hostelName = (String) r[4];
            String hostelType = (String) r[5];
            int occupancy = (int) r[6];

            RoomCard card = new RoomCard(roomId, roomNumber, floor, capacity, hostelName, hostelType);
            card.updateBeds(occupancy, capacity);
            card.updateAttendanceStatus(0, occupancy);

            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        if (selectedCard != null) selectedCard.setSelected(false);
                        selectedCard = card;
                        card.setSelected(true);
                        selectedRoomId = roomId;
                        editRoomBtn.setEnabled(true);
                        deleteRoomBtn.setEnabled(true);
                    } else if (e.getClickCount() == 2) {
                        selectedRoomId = roomId;
                        openRoomDetails();
                    }
                }
            });
            cardGrid.add(card);
        }
        cardGrid.revalidate();
        cardGrid.repaint();
    }
}
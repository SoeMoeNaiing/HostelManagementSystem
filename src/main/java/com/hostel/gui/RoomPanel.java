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
    private static final Color PRIMARY_COLOR = new Color(33, 97, 140);
    private int selectedRoomId = -1;
    private RoomCard selectedCard = null;

    private JButton editRoomBtn;
    private JButton deleteRoomBtn;
    private Runnable onStudentDataChanged;
    private Integer hostelId;

    public RoomPanel(Integer hostelId) {
        this.hostelId = hostelId;

        roomDAO = new RoomDAO();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("Room Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.setBackground(Color.WHITE);

        JButton addRoomBtn = createModernButton("＋ Add Room", PRIMARY_COLOR);
        editRoomBtn = createModernButton("✎ Edit Room", PRIMARY_COLOR);
        deleteRoomBtn = createModernButton("✕ Delete Room", new Color(180, 50, 50));
        JButton refreshBtn = createModernButton("↻ Refresh", Color.GRAY);

        editRoomBtn.setEnabled(false);
        deleteRoomBtn.setEnabled(false);

        toolbar.add(addRoomBtn);
        toolbar.add(editRoomBtn);
        toolbar.add(deleteRoomBtn);
        toolbar.add(refreshBtn);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(Color.WHITE);
        northPanel.add(titleBar, BorderLayout.NORTH);
        northPanel.add(toolbar, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // Card grid
        cardGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        cardGrid.setBackground(Color.WHITE);
        cardGrid.setBorder(new EmptyBorder(15, 15, 15, 15));
        JScrollPane scrollPane = new JScrollPane(cardGrid,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        add(scrollPane, BorderLayout.CENTER);
        // Click on blank space in the grid → deselect any selected card
        cardGrid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == cardGrid) {
                    if (selectedCard != null) {
                        selectedCard.setSelected(false);
                        selectedCard = null;
                    }
                    selectedRoomId = -1;
                    editRoomBtn.setEnabled(false);
                    deleteRoomBtn.setEnabled(false);
                }
            }
        });

        // Actions
        addRoomBtn.addActionListener(e -> openAddRoomDialog());
        editRoomBtn.addActionListener(e -> openEditRoomDialog());
        deleteRoomBtn.addActionListener(e -> deleteSelectedRoom());
        refreshBtn.addActionListener(e -> refreshRoomCards());

        refreshRoomCards();
    }

    private void openAddRoomDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        AddRoomDialog dialog = new AddRoomDialog(parent);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            boolean ok = roomDAO.insertRoom(dialog.getRoomNumber(), dialog.getFloorNumber(),
                    dialog.getCapacity(), dialog.getHostelId());
            if (ok) {
                refreshRoomCards();
                JOptionPane.showMessageDialog(this, "Room added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add room.", "Error", JOptionPane.ERROR_MESSAGE);
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
                EditRoomDialog dialog = new EditRoomDialog(parent, id, roomNumber, floor, capacity, hostelName, hostelType);
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    boolean ok = roomDAO.updateRoom(id, dialog.getRoomNumber(), dialog.getFloorNumber(),
                            dialog.getCapacity(), dialog.getHostelId());
                    if (ok) {
                        refreshRoomCards();
                        JOptionPane.showMessageDialog(this, "Room updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to update room.", "Error", JOptionPane.ERROR_MESSAGE);
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
                selectedRoomId = -1;
                refreshRoomCards();
                JOptionPane.showMessageDialog(this, "Room deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete room.", "Error", JOptionPane.ERROR_MESSAGE);
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
                        roomNumber + " (" + hostelName + ")", this::refreshRoomCards);
                dialog.setVisible(true);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Selected room not found.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    public void setOnStudentDataChanged(Runnable callback) {
        this.onStudentDataChanged = callback;
    }
    public void refreshRoomCards() {
        selectedRoomId = -1;
        selectedCard = null;
        editRoomBtn.setEnabled(false);
        deleteRoomBtn.setEnabled(false);

        cardGrid.removeAll();
        List<Object[]> rooms = roomDAO.getAllRooms(hostelId);
        for (Object[] r : rooms) {
            int roomId = (int) r[0];
            String roomNumber = (String) r[1];
            int floor = (int) r[2];
            int capacity = (int) r[3];
            String hostelName = (String) r[4];
            String hostelType = (String) r[5];
            int occupancy = roomDAO.getOccupancyCount(roomId);

            RoomCard card = new RoomCard(roomId, roomNumber, floor, capacity, hostelName, hostelType);
            card.updateBeds(occupancy, capacity);
            card.updateAttendanceStatus(0, occupancy);

            // Add mouse listener for single-click (select) and double-click (open details)
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        // Single click: select the card
                        if (selectedCard != null) {
                            selectedCard.setSelected(false);
                        }
                        selectedCard = card;
                        card.setSelected(true);
                        selectedRoomId = roomId;
                        editRoomBtn.setEnabled(true);
                        deleteRoomBtn.setEnabled(true);
                    } else if (e.getClickCount() == 2) {
                        // Double click: open the room details dialog
                        selectedRoomId = roomId; // ensure it's the selected one
                        openRoomDetails();
                    }
                }
            });
            cardGrid.add(card);
        }
        cardGrid.revalidate();
        cardGrid.repaint();
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
}
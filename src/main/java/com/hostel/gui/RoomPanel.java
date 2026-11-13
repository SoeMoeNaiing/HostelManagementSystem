package com.hostel.gui;

import com.hostel.dao.RoomDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class RoomPanel extends JPanel {
    private RoomDAO roomDAO;
    private JPanel cardGrid;
    private JPanel centerPanel;
    private CardLayout centerLayout;
    private int selectedRoomId = -1;
    private RoomCard selectedCard = null;

    private Integer hostelId;
    private String hostelType;
    private String currentSearchQuery = "";

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
        JLabel title = new JLabel("Room Occupancy");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        // ----- Toolbar -----
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        toolbar.setBackground(UITheme.BACKGROUND);

        JButton refreshBtn = UITheme.createButton("↻ Refresh", UITheme.GRAY);
        toolbar.add(refreshBtn);

        JLabel searchIcon = new JLabel("🔍 Search Room:");
        searchIcon.setFont(UITheme.BODY);
        JTextField searchField = new JTextField(15);
        searchField.setFont(UITheme.BODY);
        searchField.setPreferredSize(new Dimension(150, UITheme.INPUT_H));
        toolbar.add(searchIcon);
        toolbar.add(searchField);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(UITheme.BACKGROUND);
        northPanel.add(titleBar, BorderLayout.NORTH);
        northPanel.add(toolbar, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // ----- Center: CardLayout (Cards vs Empty State) -----
        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);
        centerPanel.setBackground(UITheme.BACKGROUND);

        // 1. The Card Grid (Fixed stretching with BorderLayout.NORTH)
        cardGrid = new JPanel(new GridLayout(0, 4, 15, 15)); // Changed to 4 columns
        cardGrid.setBackground(UITheme.BACKGROUND);
        cardGrid.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(UITheme.BACKGROUND);
        wrapperPanel.add(cardGrid, BorderLayout.NORTH); // This prevents vertical stretching!

        JScrollPane scrollPane = new JScrollPane(wrapperPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(UITheme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setBorder(null);

        // 2. The Empty State
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(UITheme.BACKGROUND);
        JLabel emptyLabel = new JLabel("No rooms found.");
        emptyLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        emptyLabel.setForeground(Color.GRAY);
        emptyPanel.add(emptyLabel);

        centerPanel.add(scrollPane, "CARDS");
        centerPanel.add(emptyPanel, "EMPTY");
        add(centerPanel, BorderLayout.CENTER);

        // Click blank area of grid → deselect
        cardGrid.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() == cardGrid) {
                    clearSelection();
                }
            }
        });

        // --- Search Listener ---
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSearch(); }
            public void removeUpdate(DocumentEvent e) { updateSearch(); }
            public void changedUpdate(DocumentEvent e) { updateSearch(); }
            private void updateSearch() {
                currentSearchQuery = searchField.getText().toLowerCase().trim();
                refreshRoomCards();
            }
        });

        refreshBtn.addActionListener(e -> refreshRoomCards());
        refreshRoomCards();
    }

    private void clearSelection() {
        if (selectedCard != null) {
            selectedCard.setSelected(false);
            selectedCard = null;
        }
        selectedRoomId = -1;
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
    }

    public void refreshRoomCards() {
        clearSelection();
        cardGrid.removeAll();

        List<Object[]> allRooms = roomDAO.getAllRoomsWithOccupancy(hostelId);

        // Filter list
        List<Object[]> filteredRooms = allRooms.stream().filter(r -> {
            if (currentSearchQuery.isEmpty()) return true;
            String roomNum = ((String) r[1]).toLowerCase();
            String hostelName = ((String) r[4]).toLowerCase();
            return roomNum.contains(currentSearchQuery) || hostelName.contains(currentSearchQuery);
        }).collect(Collectors.toList());

        // Show empty state OR rebuild cards
        if (filteredRooms.isEmpty()) {
            centerLayout.show(centerPanel, "EMPTY");
        } else {
            centerLayout.show(centerPanel, "CARDS");

            for (Object[] r : filteredRooms) {
                int roomId = (int) r[0];
                String roomNumber = (String) r[1];
                int floor = (int) r[2];
                int capacity = (int) r[3];
                String hostelName = (String) r[4];
                String hostelType = (String) r[5];
                int occupancy = (int) r[6];
                int presentToday = (int) r[7];

                RoomCard card = new RoomCard(roomId, roomNumber, floor, capacity, hostelName, hostelType);
                card.updateBeds(occupancy, capacity);
                card.updateAttendanceStatus(presentToday, occupancy);

                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 1) {
                            if (selectedCard != null) selectedCard.setSelected(false);
                            selectedCard = card;
                            card.setSelected(true);
                            selectedRoomId = roomId;
                        } else if (e.getClickCount() == 2) {
                            selectedRoomId = roomId;
                            openRoomDetails();
                        }
                    }
                });
                cardGrid.add(card);
            }
        }

        cardGrid.revalidate();
        cardGrid.repaint();
    }
}
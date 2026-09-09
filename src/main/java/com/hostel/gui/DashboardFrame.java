package com.hostel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    // Colour scheme
    private static final Color SIDEBAR_BG = new Color(33, 97, 140);   // dark blue
    private static final Color SIDEBAR_BTN_BG = new Color(40, 110, 160);
    private static final Color SIDEBAR_BTN_HOVER = new Color(50, 130, 180);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color MAIN_BG = Color.WHITE;

    // Cards
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Module panels
    private StudentPanel studentPanel;
    private RoomPanel roomPanel;
    private AttendancePanel attendancePanel;
    private DailyAttendancePanel reportPanel;
    private StudentReportPanel studentReportPanel;

    public DashboardFrame() {
        setTitle("Smart Hostel - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // ---------- Overall layout: BorderLayout ----------
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(MAIN_BG);

        // ---------- Sidebar ----------
        JPanel sidebar = createSidebar();

        // ---------- Content area with CardLayout ----------
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(MAIN_BG);

        // Create the module panels
        studentPanel = new StudentPanel();
        roomPanel = new RoomPanel();
        attendancePanel = new AttendancePanel();
        reportPanel = new DailyAttendancePanel();
        studentReportPanel = new StudentReportPanel();
        // Connect student panel to room panel for auto-refresh after changes
        studentPanel.setOnStudentDataChanged(() -> roomPanel.refreshRoomCards());

        // Add cards to the content panel (each with a unique name)
        contentPanel.add(studentPanel, "STUDENTS");
        contentPanel.add(roomPanel, "ROOMS");
        contentPanel.add(attendancePanel, "ATTENDANCE");
        contentPanel.add(reportPanel, "ATTENDANCE LOG");
        contentPanel.add(studentReportPanel, "STUDENT_REPORT");   // <-- fixed card name

        // Show the first card by default
        cardLayout.show(contentPanel, "STUDENTS");

        // Add sidebar and content to main panel
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    /** Creates the sidebar panel with navigation buttons */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        // App logo / title in sidebar
        JLabel logoLabel = new JLabel("Smart Hostel");
        logoLabel.setForeground(TEXT_WHITE);
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logoLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25))); // spacer

        // Navigation buttons
        sidebar.add(createNavButton("👥 Students", "STUDENTS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createNavButton("🏠 Rooms", "ROOMS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        // Attendance button with refresh action
        JButton attendanceNavBtn = createNavButton("📋 Attendance", "ATTENDANCE");
        attendanceNavBtn.addActionListener(e -> attendancePanel.loadNextStudent());
        sidebar.add(attendanceNavBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        sidebar.add(createNavButton("📊 Attendance Log", "ATTENDANCE LOG"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createNavButton("🧾 Student Report", "STUDENT_REPORT"));

        return sidebar;
    }

    /** Creates a single navigation button with hover effect and click action */
    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setBackground(SIDEBAR_BTN_BG);
        button.setForeground(TEXT_WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));

        // Hover effect using mouse listener
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SIDEBAR_BTN_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SIDEBAR_BTN_BG);
            }
        });

        // Action: switch card
        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));

        return button;
    }
}
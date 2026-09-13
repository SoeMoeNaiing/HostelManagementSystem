package com.hostel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private static final Color SIDEBAR_BG = new Color(33, 97, 140);
    private static final Color SIDEBAR_BTN_BG = new Color(40, 110, 160);
    private static final Color SIDEBAR_BTN_HOVER = new Color(50, 130, 180);
    private static final Color TEXT_WHITE = Color.WHITE;
    private static final Color MAIN_BG = Color.WHITE;

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private StudentPanel studentPanel;
    private RoomPanel roomPanel;
    private AttendancePanel attendancePanel;
    private DailyAttendancePanel reportPanel;
    private StudentReportPanel studentReportPanel;

    // user context
    private String role;
    private Integer hostelId;       // null = admin (all)
    private String hostelType;      // "Boys"/"Girls"/null

    public DashboardFrame(String role, Integer hostelId, String hostelType) {
        this.role = role;
        this.hostelId = hostelId;
        this.hostelType = hostelType;

        setTitle("Smart Hostel - Dashboard (" + role + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(MAIN_BG);

        JPanel sidebar = createSidebar();

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(MAIN_BG);

        // Pass filters to panels
        studentPanel = new StudentPanel(hostelType);
        roomPanel = new RoomPanel(hostelId);
        attendancePanel = new AttendancePanel(hostelType);
        reportPanel = new DailyAttendancePanel(hostelType);
        studentReportPanel = new StudentReportPanel(hostelType);

        studentPanel.setOnStudentDataChanged(() -> roomPanel.refreshRoomCards());

        contentPanel.add(studentPanel, "STUDENTS");
        contentPanel.add(roomPanel, "ROOMS");
        contentPanel.add(attendancePanel, "ATTENDANCE");
        contentPanel.add(reportPanel, "ATTENDANCE LOG");
        contentPanel.add(studentReportPanel, "STUDENT_REPORT");

        cardLayout.show(contentPanel, "STUDENTS");

        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new MainFrame().setVisible(true);
        }
    }

    private JButton createLogoutButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(180, 50, 50));   // red
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(180, 50, 50));
            }
        });

        return button;
    }
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel logoLabel = new JLabel("Smart Hostel");
        logoLabel.setForeground(TEXT_WHITE);
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logoLabel);

        // Show who is logged in
        String who;
        if ("admin".equals(role)) who = "Admin";
        else who = hostelType + " Warden";
        JLabel userLabel = new JLabel(who);
        userLabel.setForeground(new Color(230, 240, 250));
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(userLabel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        sidebar.add(createNavButton("👥 Students", "STUDENTS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createNavButton("🏠 Rooms", "ROOMS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        JButton attendanceNavBtn = createNavButton("📋 Attendance", "ATTENDANCE");
        attendanceNavBtn.addActionListener(e -> attendancePanel.loadNextStudent());
        sidebar.add(attendanceNavBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));

        sidebar.add(createNavButton("📊 Attendance Log", "ATTENDANCE LOG"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createNavButton("🧾 Student Report", "STUDENT_REPORT"));

// Push the logout button to the bottom
        sidebar.add(Box.createVerticalGlue());

// Logout button
        JButton logoutBtn = createLogoutButton("↩ Logout");
        logoutBtn.addActionListener(e -> logout());
        sidebar.add(logoutBtn);

        return sidebar;
    }

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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(SIDEBAR_BTN_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(SIDEBAR_BTN_BG);
            }
        });

        button.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        return button;
    }
}
package com.hostel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DashboardFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private StudentPanel studentPanel;
    private RoomPanel roomPanel;
    private AttendancePanel attendancePanel;
    private DailyAttendancePanel reportPanel;
    private StudentReportPanel studentReportPanel;

    private String role;
    private Integer hostelId;
    private String hostelType;

    // Track nav buttons to highlight the active one
    private final Map<String, JButton> navButtons = new HashMap<>();
    private String activeCard = "STUDENTS";

    public DashboardFrame(String role, Integer hostelId, String hostelType) {
        this.role = role;
        this.hostelId = hostelId;
        this.hostelType = hostelType;

        setTitle("Smart Hostel - Dashboard (" + role + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UITheme.WINDOW_W, UITheme.WINDOW_H);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 550));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UITheme.BACKGROUND);

        JPanel sidebar = createSidebar();

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UITheme.BACKGROUND);

        studentPanel = new StudentPanel(hostelType);
        roomPanel = new RoomPanel(hostelId, hostelType);
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
        highlightActiveButton("STUDENTS");

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

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(UITheme.SIDEBAR_W, getHeight()));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        // Logo
        JLabel logoLabel = new JLabel("Smart Hostel");
        logoLabel.setForeground(UITheme.WHITE);
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logoLabel);

        // Logged-in user label
        String who = "admin".equals(role) ? "Admin" : hostelType + " Warden";
        JLabel userLabel = new JLabel(who);
        userLabel.setForeground(new Color(210, 225, 240));
        userLabel.setFont(UITheme.SMALL);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(userLabel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));

        sidebar.add(createNavButton("👥 Students", "STUDENTS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(createNavButton("🏠 Rooms", "ROOMS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));

        JButton attendanceNavBtn = createNavButton("📋 Attendance", "ATTENDANCE");
        attendanceNavBtn.addActionListener(e -> attendancePanel.loadNextStudent());
        sidebar.add(attendanceNavBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));

        sidebar.add(createNavButton("📊 Attendance Log", "ATTENDANCE LOG"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));

        JButton studentReportNavBtn = createNavButton("🧾 Student Report", "STUDENT_REPORT");
        studentReportNavBtn.addActionListener(e -> studentReportPanel.loadStudents());
        sidebar.add(studentReportNavBtn);

        sidebar.add(Box.createVerticalGlue());

        JButton logoutBtn = createLogoutButton("↩ Logout");
        logoutBtn.addActionListener(e -> logout());
        sidebar.add(logoutBtn);

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setBackground(UITheme.SIDEBAR_BTN);
        button.setForeground(UITheme.WHITE);
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
                if (!cardName.equals(activeCard)) {
                    button.setBackground(UITheme.SIDEBAR_BTN_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!cardName.equals(activeCard)) {
                    button.setBackground(UITheme.SIDEBAR_BTN);
                }
            }
        });

        button.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            highlightActiveButton(cardName);
        });

        navButtons.put(cardName, button);
        return button;
    }

    private void highlightActiveButton(String cardName) {
        // Reset all to normal
        for (Map.Entry<String, JButton> e : navButtons.entrySet()) {
            e.getValue().setBackground(UITheme.SIDEBAR_BTN);
        }
        // Highlight active
        JButton active = navButtons.get(cardName);
        if (active != null) {
            active.setBackground(UITheme.SIDEBAR_BTN_HOVER);
        }
        activeCard = cardName;
    }

    private JButton createLogoutButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(UITheme.DANGER);
        button.setForeground(UITheme.WHITE);
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
                button.setBackground(UITheme.DANGER);
            }
        });
        return button;
    }
}
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
    private HomePanel homePanel;
    private SettingsPanel settingsPanel;

    // Track nav buttons to highlight the active one
    private final Map<String, JButton> navButtons = new HashMap<>();
    private String activeCard = "HOME";

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

        // 1. Initialize all panels (Fixed SettingsPanel constructor)
        settingsPanel = new SettingsPanel(hostelType, hostelId, role, this::logout, roomPanel);
        studentPanel = new StudentPanel(hostelType);
        roomPanel = new RoomPanel(hostelId, hostelType);
        attendancePanel = new AttendancePanel(hostelType);
        reportPanel = new DailyAttendancePanel(hostelType);
        studentReportPanel = new StudentReportPanel(hostelType);
        homePanel = new HomePanel(hostelType, hostelId);

        // 2. Add all panels to the CardLayout FIRST
        contentPanel.add(homePanel, "HOME");
        contentPanel.add(studentPanel, "STUDENTS");
        contentPanel.add(roomPanel, "ROOMS");
        contentPanel.add(attendancePanel, "ATTENDANCE");
        contentPanel.add(reportPanel, "ATTENDANCE LOG");
        contentPanel.add(studentReportPanel, "STUDENT_REPORT");
        contentPanel.add(settingsPanel, "SETTINGS");

        // 3. Setup connections
        studentPanel.setOnStudentDataChanged(() -> roomPanel.refreshRoomCards());

        // 4. Show the default card (HOME)
        cardLayout.show(contentPanel, "HOME");
        highlightActiveButton("HOME");

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
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(UITheme.SIDEBAR_W, getHeight()));
        // Keep side borders at 0 so buttons touch the edges
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));

        // ----- TOP SECTION (Logo & User) -----
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(UITheme.SIDEBAR_BG);
        topPanel.setBorder(new EmptyBorder(0, 20, 20, 20)); // Add padding here

        JLabel logoLabel = new JLabel("Smart Hostel");
        logoLabel.setForeground(UITheme.WHITE);
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(logoLabel);

        String who = "admin".equals(role) ? "Admin" : hostelType + " Warden";
        JLabel userLabel = new JLabel(who);
        userLabel.setForeground(new Color(210, 225, 240));
        userLabel.setFont(UITheme.SMALL);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(userLabel);

        sidebar.add(topPanel, BorderLayout.NORTH);

        // ----- CENTER SECTION (Nav Buttons) -----
        // GridLayout(0, 1, 0, 5) forces all buttons to stretch 100% width!
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        buttonPanel.setBackground(UITheme.SIDEBAR_BG);

        JButton homeNavBtn = createNavButton("🏠 Home", "HOME");
        homeNavBtn.addActionListener(e -> homePanel.refreshData());
        buttonPanel.add(homeNavBtn);

        buttonPanel.add(createNavButton("👥 Students", "STUDENTS"));
        buttonPanel.add(createNavButton("🏠 Rooms", "ROOMS"));

        JButton attendanceNavBtn = createNavButton("📋 Attendance", "ATTENDANCE");
        attendanceNavBtn.addActionListener(e -> attendancePanel.loadTableData());
        buttonPanel.add(attendanceNavBtn);

        buttonPanel.add(createNavButton("📊 Attendance Log", "ATTENDANCE LOG"));

        JButton studentReportNavBtn = createNavButton("🧾 Student Report", "STUDENT_REPORT");
        studentReportNavBtn.addActionListener(e -> studentReportPanel.loadStudents());
        buttonPanel.add(studentReportNavBtn);

        // FIX: Wrap in a BorderLayout and put it in the NORTH so it doesn't stretch vertically
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(UITheme.SIDEBAR_BG);
        centerWrapper.add(buttonPanel, BorderLayout.NORTH);

        sidebar.add(centerWrapper, BorderLayout.CENTER);

        // ----- BOTTOM SECTION (Settings) -----
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(UITheme.SIDEBAR_BG);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton settingsIconBtn = new JButton("⚙");
        settingsIconBtn.setFont(new Font("SansSerif", Font.PLAIN, 28));
        settingsIconBtn.setForeground(UITheme.WHITE);
        settingsIconBtn.setBackground(UITheme.SIDEBAR_BG);
        settingsIconBtn.setBorderPainted(false);
        settingsIconBtn.setFocusPainted(false);
        settingsIconBtn.setContentAreaFilled(false);
        settingsIconBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsIconBtn.setToolTipText("Settings");
        settingsIconBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        settingsIconBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                settingsIconBtn.setContentAreaFilled(true);
                settingsIconBtn.setBackground(UITheme.SIDEBAR_BTN_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                settingsIconBtn.setContentAreaFilled(false);
            }
        });

        settingsIconBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "SETTINGS");
            highlightActiveButton("SETTINGS");
        });
        bottomPanel.add(settingsIconBtn);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setBackground(UITheme.SIDEBAR_BTN);
        button.setForeground(UITheme.WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(10, 20, 10, 10));

        // FIX: Explicitly set the height to 45px so it doesn't stretch
        button.setPreferredSize(new Dimension(UITheme.SIDEBAR_W, 45));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Hover effect
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

        // Action
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
}
package com.hostel.gui;
import  com.hostel.db.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    // Modern colour palette
    private static final Color PRIMARY_COLOR = new Color(33, 97, 140);   // deep blue
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 250); // very light grey-blue
    private static final Color TEXT_COLOR = Color.WHITE;

    public MainFrame() {
        setTitle("Smart Hostel - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(380, 220);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Header: app name + subtitle
        JLabel titleLabel = new JLabel("Smart Hostel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel("Attendance & Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(Color.GRAY);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        // Login button – flat & full width
        JButton loginButton = createModernButton("Login", PRIMARY_COLOR);
        loginButton.addActionListener(e -> {
            DashboardFrame dashboard = new DashboardFrame();
            dashboard.setVisible(true);
            dispose();
        });

        // Add to frame
        mainPanel.add(headerPanel, BorderLayout.CENTER);
        mainPanel.add(loginButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /** Helper to create a flat, coloured button */
    private JButton createModernButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));  // padding inside button
        // Slight rounded look via a compound border (optional)
        // button.setBorder(BorderFactory.createCompoundBorder(
        //         BorderFactory.createLineBorder(bgColor, 1, true),
        //         new EmptyBorder(10, 20, 10, 20)));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
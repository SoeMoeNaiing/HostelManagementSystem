package com.hostel.gui;

import com.hostel.dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserDAO userDAO;

    public MainFrame() {
        userDAO = new UserDAO();
        userDAO.seedDefaultUsers();

        setTitle("Smart Hostel - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 20));
        mainPanel.setBackground(UITheme.CARD_BG);
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // ----- Header -----
        JLabel titleLabel = new JLabel("Smart Hostel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(UITheme.PRIMARY);

        JLabel subtitleLabel = new JLabel("Attendance & Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(UITheme.BODY);
        subtitleLabel.setForeground(Color.GRAY);

        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        headerPanel.setBackground(UITheme.CARD_BG);
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);

        // ----- Form -----
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(UITheme.BODY);

        usernameField = new JTextField(20);
        usernameField.setFont(UITheme.BODY);
        usernameField.setPreferredSize(new Dimension(200, UITheme.INPUT_H));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.BODY);

        passwordField = new JPasswordField(20);
        passwordField.setFont(UITheme.BODY);
        passwordField.setPreferredSize(new Dimension(200, UITheme.INPUT_H));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.2;
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.2;
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 0.8;
        formPanel.add(passwordField, gbc);

        // ----- Login button -----
        loginButton = UITheme.createButton("Login", UITheme.PRIMARY);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(150, 42));
        loginButton.addActionListener(e -> authenticate());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPanel.setBackground(UITheme.CARD_BG);
        buttonPanel.add(loginButton);

        // ----- Assemble -----
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Focus username field on open
        SwingUtilities.invokeLater(usernameField::requestFocusInWindow);

        // Press Enter in password field → login
        passwordField.addActionListener(e -> authenticate());
    }

    private void authenticate() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object[] user = userDAO.authenticateAndGetUser(username, password);
        if (user != null) {
            String role = (String) user[0];
            Integer hostelId = (Integer) user[1];
            String hostelType = (String) user[2];
            new DashboardFrame(role, hostelId, hostelType).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
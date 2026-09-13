package com.hostel.gui;

import com.hostel.dao.AttendanceDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AttendancePanel extends JPanel {
    private AttendanceDAO attendanceDAO;
    private Object[] currentStudent;
    private JLabel nameLabel, idLabel, infoLabel;
    private JButton presentBtn, absentBtn;
    private String hostelType;

    private static final Color PRIMARY_COLOR = new Color(33, 97, 140);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(180, 50, 50);
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    public AttendancePanel(String hostelType) {
        this.hostelType = hostelType;
        attendanceDAO = new AttendanceDAO();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("Daily Attendance Terminal");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleBar.add(dateLabel, BorderLayout.EAST);

        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(BACKGROUND_COLOR);
        cardPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(245, 248, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 220), 1, true),
                new EmptyBorder(20, 40, 20, 40)));

        nameLabel = new JLabel("No student");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        idLabel = new JLabel();
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoLabel = new JLabel();
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(idLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(infoLabel);
        card.add(Box.createVerticalStrut(20));

        presentBtn = createModernButton("✔ Present", SUCCESS_COLOR);
        absentBtn = createModernButton("✘ Absent", DANGER_COLOR);

        presentBtn.addActionListener(e -> markCurrent("Present"));
        absentBtn.addActionListener(e -> markCurrent("Absent"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(245, 248, 250));
        buttonPanel.add(presentBtn);
        buttonPanel.add(absentBtn);
        card.add(buttonPanel);

        cardPanel.add(card);

        add(titleBar, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        loadNextStudent();
    }

    public void loadNextStudent() {
        currentStudent = attendanceDAO.getNextUnmarkedStudent(hostelType);
        if (currentStudent == null) {
            nameLabel.setText("All students marked for today");
            idLabel.setText("");
            infoLabel.setText("");
            presentBtn.setEnabled(false);
            absentBtn.setEnabled(false);
            return;
        }
        presentBtn.setEnabled(true);
        absentBtn.setEnabled(true);
        nameLabel.setText((String) currentStudent[1]);
        idLabel.setText("Roll No: " + currentStudent[0]);
        infoLabel.setText(currentStudent[2] + " | " + currentStudent[3] + " | " + currentStudent[4]);
    }

    private void markCurrent(String status) {
        if (currentStudent == null) return;
        String studentId = (String) currentStudent[0];
        // Silent mark — no popup, no remark for Present/Absent
        boolean ok = attendanceDAO.markAttendance(studentId, status, null);
        if (ok) {
            loadNextStudent();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to mark attendance.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createModernButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }
}
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

    public AttendancePanel(String hostelType) {
        this.hostelType = hostelType;
        attendanceDAO = new AttendanceDAO();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        // ----- Title bar -----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UITheme.PRIMARY);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Daily Attendance Terminal");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setForeground(UITheme.WHITE);
        dateLabel.setFont(UITheme.BODY);
        titleBar.add(dateLabel, BorderLayout.EAST);

        // ----- Center card -----
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(UITheme.BACKGROUND);
        cardPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1, true),
                new EmptyBorder(30, 60, 30, 60)));

        nameLabel = new JLabel("No student");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        nameLabel.setForeground(UITheme.PRIMARY_DARK);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        idLabel = new JLabel();
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        idLabel.setForeground(new Color(60, 60, 60));
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoLabel = new JLabel();
        infoLabel.setFont(UITheme.BODY);
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(idLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(infoLabel);
        card.add(Box.createVerticalStrut(25));

        presentBtn = createActionButton("✔ Present", UITheme.SUCCESS);
        absentBtn = createActionButton("✘ Absent", UITheme.DANGER);

        presentBtn.addActionListener(e -> markCurrent("Present"));
        absentBtn.addActionListener(e -> markCurrent("Absent"));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(UITheme.CARD_BG);
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
        boolean ok = attendanceDAO.markAttendance(studentId, status, null);
        if (ok) {
            loadNextStudent();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to mark attendance.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createActionButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(UITheme.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 30, 12, 30));
        btn.setPreferredSize(new Dimension(160, 48));
        return btn;
    }
}
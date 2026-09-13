package com.hostel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LeaveReasonDialog extends JDialog {
    private JTextArea reasonArea;
    private boolean confirmed = false;

    public LeaveReasonDialog(JFrame parent, String studentName) {
        super(parent, "Mark Leave - " + studentName, true);
        initUI(parent, studentName);
    }

    private void initUI(JFrame parent, String studentName) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Enter leave reason for " + studentName + ":");
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        mainPanel.add(header, BorderLayout.NORTH);

        reasonArea = new JTextArea(4, 30);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(reasonArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton saveBtn = createModernButton("Save", new Color(33, 97, 140));
        JButton cancelBtn = createModernButton("Cancel", Color.GRAY);

        saveBtn.addActionListener(e -> {
            if (reasonArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a reason.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            confirmed = true;
            dispose();
        });
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
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
        btn.setBorder(new EmptyBorder(6, 15, 6, 15));
        return btn;
    }

    public boolean isConfirmed() { return confirmed; }
    public String getReason() { return reasonArea.getText().trim(); }
}
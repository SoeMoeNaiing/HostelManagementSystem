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
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UITheme.WHITE);

        JLabel header = new JLabel("Enter leave reason for " + studentName + ":");
        header.setFont(UITheme.HEADER);
        header.setForeground(UITheme.PRIMARY_DARK);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        mainPanel.add(header, BorderLayout.NORTH);

        reasonArea = new JTextArea(5, 32);
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setFont(UITheme.BODY);
        reasonArea.setBorder(new EmptyBorder(6, 8, 6, 8));

        JScrollPane scrollPane = new JScrollPane(reasonArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        scrollPane.setPreferredSize(new Dimension(360, 120));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(UITheme.WHITE);
        JButton saveBtn = UITheme.createButton("Save", UITheme.PRIMARY);
        JButton cancelBtn = UITheme.createButton("Cancel", UITheme.GRAY);

        saveBtn.addActionListener(e -> {
            if (reasonArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a reason.",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
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

        // Focus on the text area for quick typing
        SwingUtilities.invokeLater(reasonArea::requestFocusInWindow);
    }

    public boolean isConfirmed() { return confirmed; }
    public String getReason() { return reasonArea.getText().trim(); }
}
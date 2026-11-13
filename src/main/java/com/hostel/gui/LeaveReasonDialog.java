package com.hostel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;

public class LeaveReasonDialog extends JDialog {
    private JTextArea reasonArea;
    private JComboBox<Integer> startDay, startMonth, startYear;
    private JComboBox<Integer> endDay, endMonth, endYear;
    private boolean confirmed = false;

    public LeaveReasonDialog(JFrame parent, String studentName) {
        super(parent, "Mark Leave - " + studentName, true);
        initUI(parent, studentName);
    }

    private void initUI(JFrame parent, String studentName) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UITheme.WHITE);

        JLabel header = new JLabel("Leave for " + studentName);
        header.setFont(UITheme.HEADER);
        header.setForeground(UITheme.PRIMARY_DARK);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 1, 5, 10));
        formPanel.setBackground(UITheme.WHITE);

        // Start Date
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startPanel.setBackground(UITheme.WHITE);
        startPanel.add(new JLabel("Start Date:"));
        startDay = createCombo(1, 31); startMonth = createMonthCombo(); startYear = createYearCombo();
        startPanel.add(startDay); startPanel.add(startMonth); startPanel.add(startYear);

        // End Date
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        endPanel.setBackground(UITheme.WHITE);
        endPanel.add(new JLabel("End Date:  "));
        endDay = createCombo(1, 31); endMonth = createMonthCombo(); endYear = createYearCombo();
        endPanel.add(endDay); endPanel.add(endMonth); endPanel.add(endYear);

        // Reason
        JPanel reasonPanel = new JPanel(new BorderLayout(5, 5));
        reasonPanel.setBackground(UITheme.WHITE);
        reasonPanel.add(new JLabel("Reason:"), BorderLayout.NORTH);
        reasonArea = new JTextArea(3, 25);
        reasonArea.setLineWrap(true);
        reasonArea.setFont(UITheme.BODY);
        reasonPanel.add(new JScrollPane(reasonArea), BorderLayout.CENTER);

        formPanel.add(startPanel);
        formPanel.add(endPanel);
        formPanel.add(reasonPanel);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.WHITE);
        JButton saveBtn = UITheme.createButton("Save Leave", UITheme.PRIMARY);
        JButton cancelBtn = UITheme.createButton("Cancel", UITheme.GRAY);

        saveBtn.addActionListener(e -> {
            if (reasonArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a reason.");
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

    private JComboBox<Integer> createCombo(int start, int end) {
        JComboBox<Integer> c = new JComboBox<>();
        for (int i = start; i <= end; i++) c.addItem(i);
        c.setSelectedItem(LocalDate.now().getDayOfMonth());
        return c;
    }

    private JComboBox<Integer> createMonthCombo() {
        JComboBox<Integer> c = new JComboBox<>();
        for (int i = 1; i <= 12; i++) c.addItem(i);
        c.setSelectedItem(LocalDate.now().getMonthValue());
        return c;
    }

    private JComboBox<Integer> createYearCombo() {
        JComboBox<Integer> c = new JComboBox<>();
        c.addItem(LocalDate.now().getYear());
        c.addItem(LocalDate.now().getYear() + 1);
        return c;
    }

    public boolean isConfirmed() { return confirmed; }
    public String getReason() { return reasonArea.getText().trim(); }

    public LocalDate getStartDate() {
        return LocalDate.of((int)startYear.getSelectedItem(), (int)startMonth.getSelectedItem(), (int)startDay.getSelectedItem());
    }

    public LocalDate getEndDate() {
        return LocalDate.of((int)endYear.getSelectedItem(), (int)endMonth.getSelectedItem(), (int)endDay.getSelectedItem());
    }
}
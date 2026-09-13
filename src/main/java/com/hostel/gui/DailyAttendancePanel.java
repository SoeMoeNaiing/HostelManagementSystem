package com.hostel.gui;

import com.hostel.dao.AttendanceDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class DailyAttendancePanel extends JPanel {
    private AttendanceDAO attendanceDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> dayCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JButton loadBtn;
    private String hostelType;

    private static final Color PRIMARY_COLOR = new Color(33, 97, 140);
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    public DailyAttendancePanel(String hostelType) {
        this.hostelType = hostelType;
        attendanceDAO = new AttendanceDAO();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // ----- Title bar -----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("Daily Attendance Log");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        // ----- Control panel -----
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        controlPanel.setBackground(BACKGROUND_COLOR);

        dayCombo = new JComboBox<>();
        for (int d = 1; d <= 31; d++) dayCombo.addItem(d);
        dayCombo.setSelectedItem(LocalDate.now().getDayOfMonth());

        monthCombo = new JComboBox<>();
        for (Month m : Month.values()) monthCombo.addItem(m.toString());
        monthCombo.setSelectedItem(LocalDate.now().getMonth().toString());

        yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        yearCombo.addItem(currentYear);
        yearCombo.addItem(currentYear + 1);
        yearCombo.setSelectedItem(currentYear);

        loadBtn = createModernButton("Load", PRIMARY_COLOR);
        loadBtn.addActionListener(e -> loadDailyData());

        controlPanel.add(new JLabel("Day:"));
        controlPanel.add(dayCombo);
        controlPanel.add(new JLabel("Month:"));
        controlPanel.add(monthCombo);
        controlPanel.add(new JLabel("Year:"));
        controlPanel.add(yearCombo);
        controlPanel.add(loadBtn);

        // ----- Top panel (title + control) -----
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(titleBar, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.SOUTH);

        // ----- Table -----
        String[] cols = {"Student ID", "Name", "Status", "Remark"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));

        // ----- Assemble -----
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadDailyData() {
        int day = (int) dayCombo.getSelectedItem();
        String monthName = (String) monthCombo.getSelectedItem();
        int year = (int) yearCombo.getSelectedItem();
        int monthNumber = Month.valueOf(monthName.toUpperCase()).getValue();

        try {
            LocalDate.of(year, monthNumber, day);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        String dateStr = String.format("%04d-%02d-%02d", year, monthNumber, day);
        List<Object[]> data = attendanceDAO.getDailyAttendance(dateStr, hostelType);
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
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
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }
}
package com.hostel.gui;

import com.hostel.dao.AttendanceDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
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

    public DailyAttendancePanel(String hostelType) {
        this.hostelType = hostelType;
        attendanceDAO = new AttendanceDAO();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        // ----- Title bar -----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UITheme.PRIMARY);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("Daily Attendance Log");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        // ----- Control panel -----
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.setBackground(UITheme.BACKGROUND);

        dayCombo = createCombo();
        for (int d = 1; d <= 31; d++) dayCombo.addItem(d);
        dayCombo.setSelectedItem(LocalDate.now().getDayOfMonth());

        monthCombo = new JComboBox<>();
        for (Month m : Month.values()) monthCombo.addItem(m.toString());
        monthCombo.setSelectedItem(LocalDate.now().getMonth().toString());
        monthCombo.setFont(UITheme.BODY);
        monthCombo.setPreferredSize(new Dimension(120, UITheme.INPUT_H));

        yearCombo = createCombo();
        int currentYear = LocalDate.now().getYear();
        yearCombo.addItem(currentYear);
        yearCombo.addItem(currentYear + 1);
        yearCombo.setSelectedItem(currentYear);

        loadBtn = UITheme.createButton("Show", UITheme.PRIMARY);
        loadBtn.addActionListener(e -> loadDailyData());

        controlPanel.add(createLabel("Day:"));
        controlPanel.add(dayCombo);
        controlPanel.add(createLabel("Month:"));
        controlPanel.add(monthCombo);
        controlPanel.add(createLabel("Year:"));
        controlPanel.add(yearCombo);
        controlPanel.add(loadBtn);

        // ----- Top panel -----
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BACKGROUND);
        topPanel.add(titleBar, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.SOUTH);

        // ----- Table -----
        String[] cols = {"Roll No.", "Name", "Status", "Remark"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(UITheme.BODY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(UITheme.BUTTON);
        table.getTableHeader().setBackground(UITheme.PRIMARY);
        table.getTableHeader().setForeground(UITheme.WHITE);

        // Colour-code status column (Present=green, Absent=red, Leave=orange, Unmarked=gray)
        table.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                String status = value == null ? "" : value.toString();
                if (!isSelected) {
                    switch (status) {
                        case "Present":  c.setForeground(UITheme.SUCCESS); break;
                        case "Absent":   c.setForeground(UITheme.DANGER); break;
                        case "Leave":    c.setForeground(UITheme.WARNING); break;
                        default:         c.setForeground(Color.GRAY);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5, 15, 15, 15));

        // ----- Assemble -----
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Auto-load today's data on open
        loadDailyData();
    }

    private JComboBox<Integer> createCombo() {
        JComboBox<Integer> combo = new JComboBox<>();
        combo.setFont(UITheme.BODY);
        combo.setPreferredSize(new Dimension(70, UITheme.INPUT_H));
        return combo;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BODY);
        return label;
    }

    private void loadDailyData() {
        int day = (int) dayCombo.getSelectedItem();
        String monthName = (String) monthCombo.getSelectedItem();
        int year = (int) yearCombo.getSelectedItem();
        int monthNumber = Month.valueOf(monthName.toUpperCase()).getValue();

        try {
            LocalDate.of(year, monthNumber, day);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date selected.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String dateStr = String.format("%04d-%02d-%02d", year, monthNumber, day);
        List<Object[]> data = attendanceDAO.getDailyAttendance(dateStr, hostelType);
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }
}
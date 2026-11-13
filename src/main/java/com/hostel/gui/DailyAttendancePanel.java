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

        dayCombo = new JComboBox<>();
        for (int d = 1; d <= 31; d++) dayCombo.addItem(d);
        dayCombo.setSelectedItem(LocalDate.now().getDayOfMonth());

        monthCombo = new JComboBox<>();
        for (Month m : Month.values()) monthCombo.addItem(m.toString());
        monthCombo.setSelectedItem(LocalDate.now().getMonth().toString());
        monthCombo.setFont(UITheme.BODY);
        monthCombo.setPreferredSize(new Dimension(120, UITheme.INPUT_H));

        yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        yearCombo.addItem(currentYear);
        yearCombo.addItem(currentYear + 1);
        yearCombo.setSelectedItem(currentYear);

        loadBtn = UITheme.createButton("Load", UITheme.PRIMARY);
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
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));

        // Colour-code status column
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

        // ----- Action Panel (Override Button) -----
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        actionPanel.setBackground(UITheme.BACKGROUND);

        JButton overrideBtn = UITheme.createButton("✔ Override to Present", UITheme.SUCCESS);
        overrideBtn.setToolTipText("If a student returns early from Leave, select them and click this.");
        overrideBtn.addActionListener(e -> overrideToPresent());

        actionPanel.add(overrideBtn);

        // ----- Assemble -----
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH); // Add the button below the table

        // Auto-load today's data on open
        loadDailyData();
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

    // Logic to override a Leave to Present
    private void overrideToPresent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table first.");
            return;
        }

        String status = (String) tableModel.getValueAt(selectedRow, 2);
        if (!"Leave".equals(status) && !"Absent".equals(status)) {
            JOptionPane.showMessageDialog(this, "Only students marked as 'Leave' or 'Absent' can be overridden to Present.");
            return;
        }

        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Override " + status + " and mark " + name + " as Present?",
                "Confirm Override", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // We need the specific date string, so we rebuild it
            int day = (int) dayCombo.getSelectedItem();
            int monthNumber = Month.valueOf(monthCombo.getSelectedItem().toString().toUpperCase()).getValue();
            int year = (int) yearCombo.getSelectedItem();
            String dateStr = String.format("%04d-%02d-%02d", year, monthNumber, day);

            boolean ok = attendanceDAO.markAttendanceForDate(studentId, dateStr, "Present", null);
            if (ok) {
                loadDailyData(); // Refresh table to show new status
                JOptionPane.showMessageDialog(this, name + " marked as Present.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
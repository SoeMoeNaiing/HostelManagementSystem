package com.hostel.gui;

import com.hostel.dao.AttendanceDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportPanel extends JPanel {
    private AttendanceDAO attendanceDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeCombo;
    private JComboBox<String> periodCombo;  // weeks or months depending on type
    private JComboBox<String> yearCombo;
    private JButton generateBtn;

    private static final Color PRIMARY_COLOR = new Color(33, 97, 140);
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    public ReportPanel() {
        attendanceDAO = new AttendanceDAO();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("Attendance Reports");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        controlPanel.setBackground(BACKGROUND_COLOR);

        typeCombo = new JComboBox<>(new String[]{"Weekly", "Monthly"});
        yearCombo = new JComboBox<>();
        int startYear = 2026;   // first year of system operation
        int currentYear = LocalDate.now().getYear();
        int endYear = currentYear + 1;   // only one year ahead
        for (int y = startYear; y <= endYear; y++) {
            yearCombo.addItem(String.valueOf(y));
        }
        yearCombo.setSelectedItem(String.valueOf(currentYear));

        periodCombo = new JComboBox<>();
        updatePeriodCombo();

        generateBtn = createModernButton("Generate", PRIMARY_COLOR);
        generateBtn.addActionListener(e -> generateReport());

        controlPanel.add(new JLabel("Type:"));
        controlPanel.add(typeCombo);
        controlPanel.add(new JLabel("Year:"));
        controlPanel.add(yearCombo);
        controlPanel.add(new JLabel("Period:"));
        controlPanel.add(periodCombo);
        controlPanel.add(generateBtn);

        typeCombo.addActionListener(e -> updatePeriodCombo());
        yearCombo.addActionListener(e -> updatePeriodCombo());

        // Table
        String[] cols = {"Student ID", "Name", "Present Days", "Absent Days", "Leave Days"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));

        add(titleBar, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void updatePeriodCombo() {
        periodCombo.removeAllItems();
        String type = (String) typeCombo.getSelectedItem();
        int year = Integer.parseInt((String) yearCombo.getSelectedItem());
        if ("Weekly".equals(type)) {
            // Add week labels, e.g., "Week 1 (Jan 1 - Jan 7)"
            LocalDate start = LocalDate.of(year, 1, 1);
            int weekNum = 1;
            while (start.getYear() == year) {
                LocalDate end = start.plusDays(6);
                if (end.getYear() > year) end = LocalDate.of(year, 12, 31);
                periodCombo.addItem(String.format("Week %d (%s - %s)", weekNum,
                        start.format(DateTimeFormatter.ofPattern("MMM d")),
                        end.format(DateTimeFormatter.ofPattern("MMM d"))));
                start = end.plusDays(1);
                weekNum++;
            }
        } else { // Monthly
            for (int m = 1; m <= 12; m++) {
                YearMonth ym = YearMonth.of(year, m);
                periodCombo.addItem(String.format("%s %d", ym.getMonth().toString(), year));
            }
        }
    }

    private void generateReport() {
        String type = (String) typeCombo.getSelectedItem();
        int year = Integer.parseInt((String) yearCombo.getSelectedItem());
        LocalDate startDate, endDate;
        if ("Weekly".equals(type)) {
            // Parse selected week string (e.g., "Week 3 (Jan 15 - Jan 21)")
            String selected = (String) periodCombo.getSelectedItem();
            if (selected == null) return;
            // Extract the date range from the string
            int idx1 = selected.indexOf("(");
            int idx2 = selected.indexOf(" - ");
            int idx3 = selected.indexOf(")");
            String startStr = selected.substring(idx1 + 1, idx2).trim();
            String endStr = selected.substring(idx2 + 3, idx3).trim();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM d");
            startDate = LocalDate.parse(startStr + " " + year, DateTimeFormatter.ofPattern("MMM d yyyy"));
            endDate = LocalDate.parse(endStr + " " + year, DateTimeFormatter.ofPattern("MMM d yyyy"));
        } else {
            String selected = (String) periodCombo.getSelectedItem();
            if (selected == null) return;
            // e.g., "JANUARY 2025"
            String[] parts = selected.split(" ");
            String monthStr = parts[0];
            int year2 = Integer.parseInt(parts[1]);
            YearMonth ym = YearMonth.of(year2, Month.valueOf(monthStr.toUpperCase()));
            startDate = ym.atDay(1);
            endDate = ym.atEndOfMonth();
        }

        List<Object[]> summary = attendanceDAO.getAttendanceSummary(startDate.toString(), endDate.toString());
        tableModel.setRowCount(0);
        for (Object[] row : summary) {
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
package com.hostel.gui;

import com.hostel.dao.StudentDAO;
import com.hostel.dao.AttendanceDAO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentReportPanel extends JPanel {
    private StudentDAO studentDAO;
    private AttendanceDAO attendanceDAO;
    private JComboBox<String> studentCombo;
    private Map<String, String> studentMap;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JButton generateBtn;
    private JButton exportBtn;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel infoLabel;

    private String currentStudentId;
    private String currentStudentName;
    private String currentHostel;
    private String currentRoom;
    private YearMonth currentYearMonth;
    private int currentPresent, currentAbsent, currentLeave;

    private String hostelType;

    public StudentReportPanel(String hostelType) {
        this.hostelType = hostelType;
        studentDAO = new StudentDAO();
        attendanceDAO = new AttendanceDAO();
        studentMap = new LinkedHashMap<>();

        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        // ----- Title bar -----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UITheme.PRIMARY);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("Student Monthly Report");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        // ----- Control panel -----
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        controlPanel.setBackground(UITheme.BACKGROUND);

        studentCombo = new JComboBox<>();
        studentCombo.setFont(UITheme.BODY);
        studentCombo.setPreferredSize(new Dimension(220, UITheme.INPUT_H));
        loadStudents();

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
        yearCombo.setFont(UITheme.BODY);
        yearCombo.setPreferredSize(new Dimension(80, UITheme.INPUT_H));

        generateBtn = UITheme.createButton("Generate Report", UITheme.PRIMARY);
        generateBtn.addActionListener(e -> generateReport());

        exportBtn = UITheme.createButton("💾 Export to Excel", new Color(34, 139, 34));
        exportBtn.addActionListener(e -> exportToExcel());
        exportBtn.setEnabled(false);

        controlPanel.add(createLabel("Student:"));
        controlPanel.add(studentCombo);
        controlPanel.add(createLabel("Month:"));
        controlPanel.add(monthCombo);
        controlPanel.add(createLabel("Year:"));
        controlPanel.add(yearCombo);
        controlPanel.add(generateBtn);
        controlPanel.add(exportBtn);

        // ----- Info label -----
        infoLabel = new JLabel(" ");
        infoLabel.setFont(UITheme.BODY);
        infoLabel.setBorder(new EmptyBorder(6, 20, 6, 20));
        infoLabel.setForeground(new Color(60, 60, 60));

        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(UITheme.INFO_BG);
        infoPanel.add(infoLabel, BorderLayout.WEST);

        // ----- Table -----
        String[] cols = {"Date", "Day", "Status", "Remark"};
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

        // Color-code the Status column
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
                        case "Unmarked": c.setForeground(Color.GRAY); break;
                        default:         c.setForeground(Color.LIGHT_GRAY);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5, 15, 15, 15));

        // ----- Assemble -----
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UITheme.BACKGROUND);
        topPanel.add(titleBar, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);
        topPanel.add(infoPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.BODY);
        return label;
    }

    void loadStudents() {
        studentCombo.removeAllItems();
        studentMap.clear();
        List<Object[]> students = studentDAO.getAllStudents(hostelType);
        for (Object[] s : students) {
            String id = (String) s[0];
            String name = (String) s[1];
            String display = id + " - " + name;
            studentMap.put(display, id);
            studentCombo.addItem(display);
        }
    }

    private void generateReport() {
        String selectedDisplay = (String) studentCombo.getSelectedItem();
        if (selectedDisplay == null) {
            JOptionPane.showMessageDialog(this, "No students available.");
            return;
        }
        currentStudentId = studentMap.get(selectedDisplay);

        int month = Month.valueOf(monthCombo.getSelectedItem().toString().toUpperCase()).getValue();
        int year = (int) yearCombo.getSelectedItem();
        currentYearMonth = YearMonth.of(year, month);

        String startDate = currentYearMonth.atDay(1).toString();
        String endDate = currentYearMonth.atEndOfMonth().toString();

        Object[] info = studentDAO.getStudentReportInfo(currentStudentId);
        if (info == null) {
            JOptionPane.showMessageDialog(this, "Student not found.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        currentStudentName = (String) info[1];
        currentHostel = (info[6] != null) ? info[6].toString() : "Not Assigned";
        currentRoom = (info[5] != null) ? info[5].toString() : "Not Assigned";

        List<Object[]> dailyRecords = attendanceDAO.getStudentDailyRecords(currentStudentId, startDate, endDate);
        Map<LocalDate, Object[]> recordMap = new LinkedHashMap<>();
        for (Object[] rec : dailyRecords) {
            recordMap.put((LocalDate) rec[0], rec);
        }

        tableModel.setRowCount(0);
        int present = 0, absent = 0, leave = 0;
        int totalDays = currentYearMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        for (int d = 1; d <= totalDays; d++) {
            LocalDate date = currentYearMonth.atDay(d);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String status;
            String remark = "";

            if (recordMap.containsKey(date)) {
                Object[] rec = recordMap.get(date);
                status = (String) rec[1];
                remark = (rec[2] != null) ? rec[2].toString() : "";
                if ("Present".equalsIgnoreCase(status)) present++;
                else if ("Absent".equalsIgnoreCase(status)) absent++;
                else if ("Leave".equalsIgnoreCase(status)) leave++;
            } else if (date.isAfter(today)) {
                status = "—";
            } else {
                status = "Unmarked";
            }

            tableModel.addRow(new Object[]{date.toString(), dayName, status, remark});
        }

        currentPresent = present;
        currentAbsent = absent;
        currentLeave = leave;

        int workingDays = present + absent;
        double percent = (workingDays > 0) ? ((double) present / workingDays * 100) : 0;

        infoLabel.setText(String.format(
                "Student: %s (%s)  |  Hostel: %s  |  Room: %s  |  Month: %s %d   " +
                        "▶ Present: %d   Absent: %d   Leave: %d   Attendance: %.2f%%",
                currentStudentName, currentStudentId, currentHostel, currentRoom,
                currentYearMonth.getMonth(), currentYearMonth.getYear(),
                present, absent, leave, percent));

        exportBtn.setEnabled(true);
    }

    private void exportToExcel() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No data to export. Generate a report first.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report as Excel");
        fileChooser.setSelectedFile(new File(
                "StudentReport_" + currentStudentId + "_" +
                        currentYearMonth.getMonthValue() + "_" + currentYearMonth.getYear() + ".xlsx"));
        if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance Report");

            // ----- Styles -----
            CellStyle titleStyle = workbook.createCellStyle();
            XSSFFont titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            CellStyle boldStyle = workbook.createCellStyle();
            XSSFFont boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            XSSFCellStyle headerStyle = workbook.createCellStyle();
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(new XSSFColor(new byte[]{(byte)255, (byte)255, (byte)255}, null));
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte)33, (byte)97, (byte)140}, null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // ----- Content -----
            int rowIdx = 0;

            Row titleRow = sheet.createRow(rowIdx++);
            Cell tc = titleRow.createCell(0);
            tc.setCellValue("MONTHLY ATTENDANCE REPORT");
            tc.setCellStyle(titleStyle);

            rowIdx++;

            rowIdx = addInfoRow(sheet, rowIdx, "Student ID", currentStudentId, boldStyle);
            rowIdx = addInfoRow(sheet, rowIdx, "Student Name", currentStudentName, boldStyle);
            rowIdx = addInfoRow(sheet, rowIdx, "Hostel", currentHostel, boldStyle);
            rowIdx = addInfoRow(sheet, rowIdx, "Room", currentRoom, boldStyle);
            rowIdx = addInfoRow(sheet, rowIdx, "Month",
                    currentYearMonth.getMonth() + " " + currentYearMonth.getYear(), boldStyle);

            rowIdx++;

            int workingDays = currentPresent + currentAbsent;
            double percent = (workingDays > 0) ? ((double) currentPresent / workingDays * 100) : 0;
            rowIdx = addInfoRow(sheet, rowIdx, "Present Days", String.valueOf(currentPresent), boldStyle);
            rowIdx = addInfoRow(sheet, rowIdx, "Absent Days", String.valueOf(currentAbsent), boldStyle);
            rowIdx = addInfoRow(sheet, rowIdx, "Leave Days", String.valueOf(currentLeave), boldStyle);
            rowIdx = addInfoRow(sheet, rowIdx, "Attendance %", String.format("%.2f%%", percent), boldStyle);

            rowIdx++;

            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(tableModel.getColumnName(i));
                c.setCellStyle(headerStyle);
            }

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Row row = sheet.createRow(rowIdx++);
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    Object val = tableModel.getValueAt(i, j);
                    row.createCell(j).setCellValue(val != null ? val.toString() : "");
                }
            }

            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int addInfoRow(Sheet sheet, int rowIdx, String label, String value, CellStyle boldStyle) {
        Row row = sheet.createRow(rowIdx);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(boldStyle);
        row.createCell(1).setCellValue(value);
        return rowIdx + 1;
    }
}
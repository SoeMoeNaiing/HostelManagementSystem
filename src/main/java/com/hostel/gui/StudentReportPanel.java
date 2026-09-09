package com.hostel.gui;

import com.hostel.dao.StudentDAO;
import com.hostel.dao.AttendanceDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StudentReportPanel extends JPanel {
    private StudentDAO studentDAO;
    private AttendanceDAO attendanceDAO;
    private JComboBox<String> studentCombo;
    private Map<String, String> studentMap;
    private JComboBox<String> monthCombo;
    private JComboBox<Integer> yearCombo;
    private JButton generateBtn;
    private JButton saveBtn;
    private JTextArea reportArea;

    private static final Color PRIMARY_COLOR = new Color(33, 97, 140);
    private static final Color BACKGROUND_COLOR = Color.WHITE;

    public StudentReportPanel() {
        studentDAO = new StudentDAO();
        attendanceDAO = new AttendanceDAO();
        studentMap = new LinkedHashMap<>();

        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(PRIMARY_COLOR);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel title = new JLabel("Student Monthly Report");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        controlPanel.setBackground(BACKGROUND_COLOR);

        // Student combo
        studentCombo = new JComboBox<>();
        loadStudents();

        // Month combo
        monthCombo = new JComboBox<>();
        for (Month m : Month.values()) {
            monthCombo.addItem(m.toString());
        }
        monthCombo.setSelectedItem(LocalDate.now().getMonth().toString());

        // Year combo
        yearCombo = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        yearCombo.addItem(currentYear);
        yearCombo.addItem(currentYear + 1);
        yearCombo.setSelectedItem(currentYear);

        generateBtn = createModernButton("Generate Report", PRIMARY_COLOR);
        generateBtn.addActionListener(e -> generateReport());

        saveBtn = createModernButton("💾 Save as TXT", PRIMARY_COLOR);
        saveBtn.addActionListener(e -> saveReport());
        saveBtn.setEnabled(false);   // disabled until report generated

        controlPanel.add(new JLabel("Student:"));
        controlPanel.add(studentCombo);
        controlPanel.add(new JLabel("Month:"));
        controlPanel.add(monthCombo);
        controlPanel.add(new JLabel("Year:"));
        controlPanel.add(yearCombo);
        controlPanel.add(generateBtn);
        controlPanel.add(saveBtn);

        // Report area
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        reportArea.setBackground(new Color(250, 250, 250));
        reportArea.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(new EmptyBorder(10, 20, 20, 20));

        add(titleBar, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void loadStudents() {
        studentCombo.removeAllItems();
        studentMap.clear();
        List<Object[]> students = studentDAO.getAllStudents();
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
        String studentId = studentMap.get(selectedDisplay);

        int month = Month.valueOf(monthCombo.getSelectedItem().toString().toUpperCase()).getValue();
        int year = (int) yearCombo.getSelectedItem();
        YearMonth ym = YearMonth.of(year, month);
        String startDate = ym.atDay(1).toString();
        String endDate = ym.atEndOfMonth().toString();

        Object[] studentInfo = studentDAO.getStudentReportInfo(studentId);
        if (studentInfo == null) {
            JOptionPane.showMessageDialog(this, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int[] summary = attendanceDAO.getStudentMonthlySummary(studentId, startDate, endDate);

        String reportText = buildReportText(studentInfo, summary, ym);
        reportArea.setText(reportText);
        saveBtn.setEnabled(true);
    }

    private String buildReportText(Object[] studentInfo, int[] summary, YearMonth ym) {
        String hostelName = (studentInfo[6] != null) ? studentInfo[6].toString() : "Not Assigned";
        String roomNumber = (studentInfo[5] != null) ? studentInfo[5].toString() : "Not Assigned";

        int present = summary[0];
        int absent = summary[1];
        int leave = summary[2];
        int totalDays = ym.lengthOfMonth();
        int workingDays = present + absent;
        double percent = (workingDays > 0) ? ((double) present / workingDays * 100) : 0;
        String grade;
        if (percent >= 90) grade = "Excellent";
        else if (percent >= 75) grade = "Good";
        else if (percent >= 50) grade = "Fair";
        else grade = "Poor";

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("          HOSTEL MANAGEMENT SYSTEM       \n");
        sb.append("          MONTHLY ATTENDANCE REPORT      \n");
        sb.append("========================================\n\n");
        sb.append(String.format("%-20s: %s\n", "Student ID", studentInfo[0]));
        sb.append(String.format("%-20s: %s\n", "Student Name", studentInfo[1]));
        sb.append(String.format("%-20s: %s\n", "Gender", studentInfo[2]));
        sb.append(String.format("%-20s: %s\n", "Year", studentInfo[3]));
        sb.append(String.format("%-20s: %s\n", "Major", studentInfo[4]));
        sb.append(String.format("%-20s: %s\n", "Hostel", hostelName));
        sb.append(String.format("%-20s: %s\n", "Room", roomNumber));
        sb.append(String.format("%-20s: %s %d\n", "Month", ym.getMonth(), ym.getYear()));
        sb.append("\n----------------------------------------\n");
        sb.append(String.format("%-20s: %d\n", "Total Days", totalDays));
        sb.append(String.format("%-20s: %d\n", "Present Days", present));
        sb.append(String.format("%-20s: %d\n", "Absent Days", absent));
        sb.append(String.format("%-20s: %d\n", "Leave Days", leave));
        sb.append(String.format("%-20s: %.2f%%\n", "Attendance %", percent));
        sb.append(String.format("%-20s: %s\n", "Remark", grade));
        sb.append("----------------------------------------\n");
        sb.append("\n\nWarden Signature: _____________   Date: ____/____/______\n");
        return sb.toString();
    }

    private void saveReport() {
        String content = reportArea.getText();
        if (content.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No report to save. Please generate a report first.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report As");
        // Suggest file name
        String selectedDisplay = (String) studentCombo.getSelectedItem();
        if (selectedDisplay != null) {
            String studentId = studentMap.get(selectedDisplay);
            String monthName = (String) monthCombo.getSelectedItem();
            int year = (int) yearCombo.getSelectedItem();
            String suggested = "StudentReport_" + studentId + "_" + monthName + "_" + year + ".txt";
            fileChooser.setSelectedFile(new File(suggested));
        }
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(content);
                JOptionPane.showMessageDialog(this, "Report saved successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
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
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }
}
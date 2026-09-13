package com.hostel.gui;

import com.hostel.dao.StudentDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.hostel.dao.AttendanceDAO;
public class StudentPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO;
    private Runnable onStudentDataChanged;
    private String hostelType;
    private AttendanceDAO attendanceDAO;

    private static final Color PRIMARY_COLOR = new Color(33, 97, 140);
    private static final Color DANGER_COLOR = new Color(180, 50, 50);
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private void markLeaveForSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first.");
            return;
        }
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        LeaveReasonDialog dialog = new LeaveReasonDialog(parent, name);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            boolean ok = attendanceDAO.markAttendance(studentId, "Leave", dialog.getReason());
            if (ok) {
                JOptionPane.showMessageDialog(this, name + " marked on Leave for today.");
                if (onStudentDataChanged != null) onStudentDataChanged.run();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to mark leave. The student may already be marked for today.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public StudentPanel(String hostelType) {
        this.hostelType = hostelType;
        studentDAO = new StudentDAO();
        attendanceDAO = new AttendanceDAO();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel titleLabel = new JLabel("Student Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonBar.setBackground(BACKGROUND_COLOR);
        buttonBar.setBorder(new EmptyBorder(5, 10, 5, 10));

        JButton addButton = createModernButton("+ Add Student", PRIMARY_COLOR);
        JButton editButton = createModernButton("✎ Edit", PRIMARY_COLOR);
        JButton deleteButton = createModernButton("✕ Delete", DANGER_COLOR);
        JButton markLeaveButton = createModernButton("✚ Mark Leave", new Color(255, 165, 0));
        JButton refreshButton = createModernButton("↻ Refresh", Color.GRAY);
        JButton detailsButton = createModernButton("⋯ More Details", new Color(70, 130, 180));
        buttonBar.add(addButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);
        buttonBar.add(markLeaveButton);
        buttonBar.add(detailsButton);
        buttonBar.add(refreshButton);

        String[] columnNames = {"Roll No.", "Name", "Gender", "Year", "Major", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5, 10, 10, 10));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(BACKGROUND_COLOR);
        northPanel.add(titlePanel, BorderLayout.NORTH);
        northPanel.add(buttonBar, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(e -> openAddDialog());
        editButton.addActionListener(e -> openEditDialog());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        markLeaveButton.addActionListener(e -> markLeaveForSelectedStudent());
        refreshButton.addActionListener(e -> loadStudents());
        detailsButton.addActionListener(e -> showDetails());

        loadStudents();
    }

    public void setOnStudentDataChanged(Runnable callback) {
        this.onStudentDataChanged = callback;
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        List<Object[]> students = studentDAO.getAllStudents(hostelType);
        for (Object[] row : students) {
            tableModel.addRow(row);
        }
    }

    private void openAddDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        StudentFormDialog dialog = new StudentFormDialog(parent, hostelType);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            boolean success = studentDAO.insertStudent(
                    dialog.getStudentId(),
                    dialog.getStudentName(),
                    dialog.getGender(),
                    dialog.getYear(),
                    dialog.getMajor(),
                    dialog.getEmail(),
                    dialog.getPhone(),
                    dialog.getGuardianName(),
                    dialog.getGuardianPhone(),
                    dialog.getAddress(),
                    dialog.getNrc()
            );
            if (success) {
                loadStudents();
                if (onStudentDataChanged != null) onStudentDataChanged.run();
                JOptionPane.showMessageDialog(this, "Student added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.");
            return;
        }
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        Object[] studentData = studentDAO.getStudentById(studentId);
        if (studentData == null) {
            JOptionPane.showMessageDialog(this, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        StudentFormDialog dialog = new StudentFormDialog(parent,
                (String) studentData[0],
                (String) studentData[1],
                (String) studentData[2],
                (String) studentData[3],
                (String) studentData[4],
                (String) studentData[5],
                (String) studentData[6],
                (String) studentData[7],
                (String) studentData[8],
                (String) studentData[9],
                (String) studentData[10],
                hostelType        // <-- new parameter
        );
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            boolean success = studentDAO.updateStudent(
                    studentId,
                    dialog.getStudentName(),
                    dialog.getGender(),
                    dialog.getYear(),
                    dialog.getMajor(),
                    dialog.getEmail(),
                    dialog.getPhone(),
                    dialog.getGuardianName(),
                    dialog.getGuardianPhone(),
                    dialog.getAddress(),
                    dialog.getNrc()
            );
            if (success) {
                loadStudents();
                if (onStudentDataChanged != null) onStudentDataChanged.run();
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
            return;
        }
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + name + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = studentDAO.deleteStudent(studentId);
            if (success) {
                loadStudents();
                if (onStudentDataChanged != null) onStudentDataChanged.run();
                JOptionPane.showMessageDialog(this, "Student deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student first.");
            return;
        }
        String studentId = (String) tableModel.getValueAt(selectedRow, 0);
        Object[] studentData = studentDAO.getStudentById(studentId);
        if (studentData == null) {
            JOptionPane.showMessageDialog(this, "Student data not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        StudentDetailsDialog dialog = new StudentDetailsDialog(parent, studentData);
        dialog.setVisible(true);
    }

    private JButton createModernButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }
}
package com.hostel.gui;

import com.hostel.dao.AttendanceDAO;
import com.hostel.dao.StudentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class StudentPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO;
    private AttendanceDAO attendanceDAO;
    private Runnable onStudentDataChanged;
    private String hostelType;

    // For empty state
    private JPanel centerPanel;
    private CardLayout centerCardLayout;

    public StudentPanel(String hostelType) {
        this.hostelType = hostelType;
        studentDAO = new StudentDAO();
        attendanceDAO = new AttendanceDAO();
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        // ----- Title bar -----
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(UITheme.PRIMARY);
        titlePanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel titleLabel = new JLabel("Student Management");
        titleLabel.setFont(UITheme.TITLE);
        titleLabel.setForeground(UITheme.WHITE);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // ----- Button toolbar -----
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        buttonBar.setBackground(UITheme.BACKGROUND);

        JButton addButton = UITheme.createButton("+ Add Student", UITheme.PRIMARY);
        JButton editButton = UITheme.createButton("✎ Edit", UITheme.PRIMARY);
        JButton deleteButton = UITheme.createButton("✕ Delete", UITheme.DANGER);
        JButton markLeaveButton = UITheme.createButton("✚ Mark Leave", UITheme.WARNING);
        JButton detailsButton = UITheme.createButton("⋯ More Details", UITheme.INFO);
        JButton refreshButton = UITheme.createButton("↻ Refresh", UITheme.GRAY);

        buttonBar.add(addButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);
        buttonBar.add(markLeaveButton);
        buttonBar.add(detailsButton);
        buttonBar.add(refreshButton);

        // --- Search Bar ---
        JLabel searchIcon = new JLabel("🔍 Search:");
        searchIcon.setFont(UITheme.BODY);
        JTextField searchField = new JTextField(15);
        searchField.setFont(UITheme.BODY);
        searchField.setPreferredSize(new Dimension(150, UITheme.INPUT_H));
        buttonBar.add(searchIcon);
        buttonBar.add(searchField);

        // ----- Table -----
        String[] columnNames = {"Roll No.", "Name", "Gender", "Year", "Major", "Phone"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(28);
        table.setFont(UITheme.BODY);
        table.getTableHeader().setFont(UITheme.BUTTON);
        table.getTableHeader().setBackground(UITheme.PRIMARY);
        table.getTableHeader().setForeground(UITheme.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5, 15, 15, 15));

        // --- Empty State Panel ---
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setBackground(UITheme.BACKGROUND);
        JLabel emptyLabel = new JLabel("No students found.");
        emptyLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        emptyLabel.setForeground(Color.GRAY);
        emptyPanel.add(emptyLabel);

        // --- Center CardLayout ---
        centerCardLayout = new CardLayout();
        centerPanel = new JPanel(centerCardLayout);
        centerPanel.add(scrollPane, "TABLE");
        centerPanel.add(emptyPanel, "EMPTY");

        // --- Search Listener ---
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filter(); }
            public void removeUpdate(DocumentEvent e) { filter(); }
            public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim();
                if (text.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }

                // Toggle Empty State
                if (table.getRowCount() == 0) {
                    centerCardLayout.show(centerPanel, "EMPTY");
                } else {
                    centerCardLayout.show(centerPanel, "TABLE");
                }
            }
        });

        // Click empty row area → clear selection, OR Double-click → open details
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    showDetails(); // Trigger the details dialog
                } else if (table.rowAtPoint(e.getPoint()) == -1) {
                    table.clearSelection();
                }
            }
        });

        // ----- Assemble -----
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(UITheme.BACKGROUND);
        northPanel.add(titlePanel, BorderLayout.NORTH);
        northPanel.add(buttonBar, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        // ----- Actions -----
        addButton.addActionListener(e -> openAddDialog());
        editButton.addActionListener(e -> openEditDialog());
        deleteButton.addActionListener(e -> deleteSelectedStudent());
        markLeaveButton.addActionListener(e -> markLeaveForSelectedStudent());
        detailsButton.addActionListener(e -> showDetails());
        refreshButton.addActionListener(e -> loadStudents());

        loadStudents();
    }

    public void setOnStudentDataChanged(Runnable callback) {
        this.onStudentDataChanged = callback;
    }

    public void loadStudents() {
        tableModel.setRowCount(0);
        List<Object[]> students = studentDAO.getAllStudents(hostelType);
        for (Object[] row : students) {
            tableModel.addRow(row);
        }

        // Toggle empty state if all students are deleted
        if (table.getRowCount() == 0) {
            centerCardLayout.show(centerPanel, "EMPTY");
        } else {
            centerCardLayout.show(centerPanel, "TABLE");
        }
    }

    // ... (Keep all the openAddDialog, openEditDialog, deleteSelectedStudent,
    // showDetails, and markLeaveForSelectedStudent methods exactly the same as before) ...

    private void openAddDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        StudentFormDialog dialog = new StudentFormDialog(parent, hostelType);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            boolean success = studentDAO.insertStudent(
                    dialog.getStudentId(), dialog.getStudentName(), dialog.getGender(),
                    dialog.getYear(), dialog.getMajor(), dialog.getEmail(), dialog.getPhone(),
                    dialog.getGuardianName(), dialog.getGuardianPhone(), dialog.getAddress(), dialog.getNrc()
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
        if (studentData == null) return;

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        StudentFormDialog dialog = new StudentFormDialog(parent,
                (String) studentData[0], (String) studentData[1], (String) studentData[2],
                (String) studentData[3], (String) studentData[4], (String) studentData[5],
                (String) studentData[6], (String) studentData[7], (String) studentData[8],
                (String) studentData[9], (String) studentData[10], hostelType
        );
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            boolean success = studentDAO.updateStudent(
                    studentId, dialog.getStudentName(), dialog.getGender(),
                    dialog.getYear(), dialog.getMajor(), dialog.getEmail(), dialog.getPhone(),
                    dialog.getGuardianName(), dialog.getGuardianPhone(), dialog.getAddress(), dialog.getNrc()
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
        if (studentData == null) return;

        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        StudentDetailsDialog dialog = new StudentDetailsDialog(parent, studentData);
        dialog.setVisible(true);
    }

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
            LocalDate start = dialog.getStartDate();
            LocalDate end = dialog.getEndDate();

            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this, "End date cannot be before start date.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int daysMarked = 0;
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                boolean ok = attendanceDAO.markLeaveForDate(studentId, date.toString(), dialog.getReason());
                if (ok) daysMarked++;
            }

            if (daysMarked > 0) {
                JOptionPane.showMessageDialog(this, name + " marked on Leave for " + daysMarked + " day(s).");
                if (onStudentDataChanged != null) onStudentDataChanged.run();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to mark leave. The student may already be marked for those dates.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
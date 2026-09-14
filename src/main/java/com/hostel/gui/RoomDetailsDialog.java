package com.hostel.gui;

import com.hostel.dao.RoomDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomDetailsDialog extends JDialog {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private RoomDAO roomDAO;
    private int roomId;
    private Runnable refreshCallback;
    private String hostelType;

    public RoomDetailsDialog(JFrame parent, int roomId, String roomTitle,
                             Runnable refreshCallback, String hostelType) {
        super(parent, "Room " + roomTitle, true);
        this.roomId = roomId;
        this.roomDAO = new RoomDAO();
        this.refreshCallback = refreshCallback;
        this.hostelType = hostelType;
        initUI();
        loadAssignedStudents();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UITheme.WHITE);

        // Title
        JLabel title = new JLabel("Assigned Students");
        title.setFont(UITheme.HEADER);
        title.setForeground(UITheme.PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 8, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"Roll No.", "Name", "Year", "Major", "Phone"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(25);
        studentTable.setFont(UITheme.BODY);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setFont(UITheme.BUTTON);
        studentTable.getTableHeader().setBackground(UITheme.PRIMARY);
        studentTable.getTableHeader().setForeground(UITheme.WHITE);

        // Double-click → unassign quickly
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && studentTable.getSelectedRow() != -1) {
                    unassignSelectedStudent();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(UITheme.WHITE);

        JButton assignBtn = UITheme.createButton("+ Assign Student", UITheme.PRIMARY);
        JButton unassignBtn = UITheme.createButton("– Unassign Selected", UITheme.DANGER);
        JButton closeBtn = UITheme.createButton("Close", UITheme.GRAY);

        assignBtn.addActionListener(e -> openAssignDialog());
        unassignBtn.addActionListener(e -> unassignSelectedStudent());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(assignBtn);
        buttonPanel.add(unassignBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        setSize(620, 420);
        setLocationRelativeTo(getParent());
    }

    private void loadAssignedStudents() {
        tableModel.setRowCount(0);
        List<Object[]> students = roomDAO.getStudentsByRoom(roomId);
        for (Object[] s : students) {
            tableModel.addRow(s);
        }
    }

    private void openAssignDialog() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        AssignStudentDialog dialog = new AssignStudentDialog(parent, roomId, hostelType);
        dialog.setVisible(true);
        if (dialog.isStudentAssigned()) {
            loadAssignedStudents();
            if (refreshCallback != null) refreshCallback.run();
            JOptionPane.showMessageDialog(this, "Student assigned successfully.");
        }
    }

    private void unassignSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to unassign.");
            return;
        }
        String studentId = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Unassign " + name + " from this room?",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = roomDAO.unassignStudent(studentId);
            if (ok) {
                loadAssignedStudents();
                if (refreshCallback != null) refreshCallback.run();
                JOptionPane.showMessageDialog(this, "Student unassigned.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to unassign.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
package com.hostel.gui;

import com.hostel.dao.RoomDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AssignStudentDialog extends JDialog {
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private RoomDAO roomDAO;
    private int roomId;
    private boolean studentAssigned = false;
    private String hostelType;

    public AssignStudentDialog(JFrame parent, int roomId, String hostelType) {
        super(parent, "Assign Student", true);
        this.roomId = roomId;
        this.hostelType = hostelType;
        this.roomDAO = new RoomDAO();
        initUI();
        loadUnassignedStudents();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UITheme.WHITE);

        JLabel title = new JLabel("Select a student to assign");
        title.setFont(UITheme.HEADER);
        title.setForeground(UITheme.PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 8, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        String[] cols = {"Roll No.", "Name", "Year", "Major", "Phone"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(25);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setFont(UITheme.BUTTON);
        studentTable.setFont(UITheme.BODY);

        // Double-click a row → assign directly
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && studentTable.getSelectedRow() != -1) {
                    assignSelectedStudent();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(UITheme.WHITE);
        JButton assignBtn = UITheme.createButton("Assign", UITheme.PRIMARY);
        JButton cancelBtn = UITheme.createButton("Cancel", UITheme.GRAY);

        assignBtn.addActionListener(e -> assignSelectedStudent());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(assignBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        pack();
        setSize(600, 420);
        setLocationRelativeTo(getParent());
    }

    private void loadUnassignedStudents() {
        tableModel.setRowCount(0);
        List<Object[]> unassigned = roomDAO.getUnassignedStudents(hostelType);
        for (Object[] s : unassigned) {
            tableModel.addRow(s);
        }
    }

    private void assignSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student.");
            return;
        }
        String studentId = (String) tableModel.getValueAt(row, 0);

        if (roomDAO.isRoomFull(roomId)) {
            JOptionPane.showMessageDialog(this,
                    "This room is full. Cannot assign more students.",
                    "Room Full", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean ok = roomDAO.assignStudentToRoom(studentId, roomId);
        if (ok) {
            studentAssigned = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to assign student.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isStudentAssigned() { return studentAssigned; }
}
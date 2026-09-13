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
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Select a student to assign");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainPanel.add(title, BorderLayout.NORTH);

        String[] cols = {"Roll No.", "Name", "Year", "Major", "Phone"};
        tableModel = new DefaultTableModel(cols, 0);
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        JButton assignBtn = createModernButton("Assign", new Color(33, 97, 140));
        JButton cancelBtn = createModernButton("Cancel", Color.GRAY);

        assignBtn.addActionListener(e -> assignSelectedStudent());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(assignBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        pack();
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

        // Check if room is full
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
            JOptionPane.showMessageDialog(this, "Failed to assign student.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isStudentAssigned() { return studentAssigned; }

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
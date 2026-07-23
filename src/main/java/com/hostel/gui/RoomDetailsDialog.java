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
    private Runnable refreshCallback;    // to refresh the room panel after changes

    public RoomDetailsDialog(JFrame parent, int roomId, String roomTitle, Runnable refreshCallback) {
        super(parent, "Room " + roomTitle, true);
        this.roomId = roomId;
        this.roomDAO = new RoomDAO();
        this.refreshCallback = refreshCallback;
        initUI();
        loadAssignedStudents();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel("Assigned Students");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(new Color(33, 97, 140));
        mainPanel.add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"Student ID", "Name", "Year", "Major", "Phone"};
        tableModel = new DefaultTableModel(cols, 0);
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(22);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton assignBtn = createModernButton("+ Assign Student", new Color(33, 97, 140));
        JButton unassignBtn = createModernButton("– Unassign Selected", new Color(180, 50, 50));
        JButton closeBtn = createModernButton("Close", Color.GRAY);

        assignBtn.addActionListener(e -> openAssignDialog());
        unassignBtn.addActionListener(e -> unassignSelectedStudent());
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(assignBtn);
        buttonPanel.add(unassignBtn);
        buttonPanel.add(closeBtn);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        pack();
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
        AssignStudentDialog dialog = new AssignStudentDialog(parent, roomId);
        dialog.setVisible(true);
        if (dialog.isStudentAssigned()) {
            loadAssignedStudents();
            if (refreshCallback != null) refreshCallback.run();   // refresh room cards
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
                "Unassign " + name + " from this room?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = roomDAO.unassignStudent(studentId);
            if (ok) {
                loadAssignedStudents();
                if (refreshCallback != null) refreshCallback.run();
                JOptionPane.showMessageDialog(this, "Student unassigned.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to unassign.", "Error", JOptionPane.ERROR_MESSAGE);
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
        btn.setBorder(new EmptyBorder(6, 12, 6, 12));
        return btn;
    }
}
package com.hostel.gui;

import com.hostel.dao.AttendanceDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AttendancePanel extends JPanel {
    private AttendanceDAO attendanceDAO;
    private JTable table;
    private DefaultTableModel tableModel;
    private String hostelType;

    public AttendancePanel(String hostelType) {
        this.hostelType = hostelType;
        this.attendanceDAO = new AttendanceDAO();

        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);

        // ----- Title bar -----
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(UITheme.PRIMARY);
        titleBar.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Daily Attendance Terminal");
        title.setFont(UITheme.TITLE);
        title.setForeground(UITheme.WHITE);
        titleBar.add(title, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setForeground(UITheme.WHITE);
        dateLabel.setFont(UITheme.BODY);
        titleBar.add(dateLabel, BorderLayout.EAST);
        add(titleBar, BorderLayout.NORTH);

        // ----- Table -----
        // Columns 4 and 5 will hold our buttons
        String[] cols = {"Roll No.", "Name", "Year", "Major", "Mark Present", "Mark Absent"};

        // We override getColumnClass so the table knows columns 4 and 5 are JButtons
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; // Only buttons are editable
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex >= 4 ? JButton.class : String.class;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(38); // Taller rows to fit buttons
        table.setFont(UITheme.BODY);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(UITheme.BUTTON);
        table.getTableHeader().setBackground(UITheme.PRIMARY);
        table.getTableHeader().setForeground(UITheme.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);

        // Attach the custom button renderer and editor
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("✔ Present", UITheme.SUCCESS));
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor("✔ Present", UITheme.SUCCESS, "Present"));

        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("✘ Absent", UITheme.DANGER));
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor("✘ Absent", UITheme.DANGER, "Absent"));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5, 15, 15, 15));

        add(scrollPane, BorderLayout.CENTER);

        // Load initial data
        loadTableData();
    }

    public void loadTableData() {
        tableModel.setRowCount(0);
        List<Object[]> data = attendanceDAO.getPendingAttendanceTable(hostelType);

        for (Object[] row : data) {
            // We add null for the button columns, the renderer draws the buttons
            tableModel.addRow(new Object[]{ row[0], row[1], row[2], row[3], null, null });
        }

        if (data.isEmpty()) {
            // If empty, we can show a message or just leave the empty table
        }
    }

    // ----- INNER CLASSES FOR BUTTONS -----

    // 1. Renderer (What the button looks like)
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer(String text, Color bg) {
            setText(text);
            setBackground(bg);
            setForeground(Color.WHITE);
            setFont(UITheme.BUTTON);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(true);
        }
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // 2. Editor (What happens when you click the button)
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String statusToMark;
        private int currentRow;

        public ButtonEditor(String text, Color bg, String statusToMark) {
            super(new JCheckBox()); // Dummy component for superclass
            this.statusToMark = statusToMark;
            button = new JButton(text);
            button.setBackground(bg);
            button.setForeground(Color.WHITE);
            button.setFont(UITheme.BUTTON);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);

            button.addActionListener(e -> {
                fireEditingStopped(); // Tell table editing is done
                markStudent(currentRow, statusToMark);
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            return null;
        }
    }

    // Logic to update database and remove the row
    private void markStudent(int row, String status) {
        String studentId = (String) tableModel.getValueAt(row, 0);
        String studentName = (String) tableModel.getValueAt(row, 1);

        // Check for Leave Override (Just in case they somehow got into this list)
        // Since we query only "Unmarked", Leave students won't appear here naturally.
        // But this is good safety.

        boolean ok = attendanceDAO.markOrUpdateAttendance(studentId, status);
        if (ok) {
            // Instantly remove the row so the next student moves up
            tableModel.removeRow(row);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to mark " + studentName, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
package com.hostel.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StudentFormDialog extends JDialog {
    private JTextField studentIdField, nameField, yearField, majorField, emailField, phoneField,
            guardianNameField, guardianPhoneField, addressField, nrcField;
    private JComboBox<String> genderCombo;

    private boolean confirmed = false;

    // Constructor for ADD
    public StudentFormDialog(JFrame parent, String hostelType) {
        super(parent, "Add New Student", true);
        initComponents(parent, null, null, null, null, null, null, null, null, null, null, null, 0, hostelType);
    }

    // Constructor for EDIT
    public StudentFormDialog(JFrame parent, String studentId, String name, String gender,
                             String year, String major, String email, String phone,
                             String guardianName, String guardianPhone, String address, String nrc,
                             String hostelType) {
        super(parent, "Edit Student", true);
        initComponents(parent, studentId, name, gender, year, major, email, phone,
                guardianName, guardianPhone, address, nrc, 1, hostelType);
    }

    private void initComponents(JFrame parent, String studentId, String name, String gender,
                                String year, String major, String email, String phone,
                                String guardianName, String guardianPhone, String address,
                                String nrc, int mode, String hostelType) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        studentIdField = new JTextField(15);
        nameField = new JTextField(20);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        yearField = new JTextField(20);
        majorField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        guardianNameField = new JTextField(20);
        guardianPhoneField = new JTextField(20);
        addressField = new JTextField(20);
        nrcField = new JTextField(20);

        // If hostelType is provided, lock the gender combo
        if (hostelType != null) {
            String lockedGender = "Boys".equalsIgnoreCase(hostelType) ? "Male" : "Female";
            genderCombo.setSelectedItem(lockedGender);
            genderCombo.setEnabled(false);
        }

        // Pre-fill if editing
        if (mode == 1) {
            studentIdField.setText(studentId);
            studentIdField.setEditable(false);
            studentIdField.setBackground(new Color(240, 240, 240));
            nameField.setText(name);
            genderCombo.setSelectedItem(gender);
            yearField.setText(year);
            majorField.setText(major);
            emailField.setText(email);
            phoneField.setText(phone);
            guardianNameField.setText(guardianName);
            guardianPhoneField.setText(guardianPhone);
            addressField.setText(address);
            nrcField.setText(nrc);
        }

        int row = 0;
        addFormRow(formPanel, gbc, "Student ID:", studentIdField, row++);
        addFormRow(formPanel, gbc, "Name:", nameField, row++);
        addFormRow(formPanel, gbc, "Gender:", genderCombo, row++);
        addFormRow(formPanel, gbc, "Year:", yearField, row++);
        addFormRow(formPanel, gbc, "Major:", majorField, row++);
        addFormRow(formPanel, gbc, "Email:", emailField, row++);
        addFormRow(formPanel, gbc, "Phone:", phoneField, row++);
        addFormRow(formPanel, gbc, "Guardian:", guardianNameField, row++);
        addFormRow(formPanel, gbc, "Guardian Ph:", guardianPhoneField, row++);
        addFormRow(formPanel, gbc, "Address:", addressField, row++);
        addFormRow(formPanel, gbc, "NRC:", nrcField, row++);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton saveButton = createModernButton("Save", new Color(33, 97, 140));
        JButton cancelButton = createModernButton("Cancel", Color.GRAY);

        saveButton.addActionListener(e -> {
            if (studentIdField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Student ID and Name are required!",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            confirmed = true;
            dispose();
        });
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        pack();
        setLocationRelativeTo(parent);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText, Component field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.1;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.9;
        panel.add(field, gbc);
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

    public boolean isConfirmed() { return confirmed; }
    public String getStudentId() { return studentIdField.getText().trim(); }
    public String getStudentName() { return nameField.getText().trim(); }
    public String getGender() { return (String) genderCombo.getSelectedItem(); }
    public String getYear() { return yearField.getText().trim(); }
    public String getMajor() { return majorField.getText().trim(); }
    public String getEmail() { return emailField.getText().trim(); }
    public String getPhone() { return phoneField.getText().trim(); }
    public String getGuardianName() { return guardianNameField.getText().trim(); }
    public String getGuardianPhone() { return guardianPhoneField.getText().trim(); }
    public String getAddress() { return addressField.getText().trim(); }
    public String getNrc() { return nrcField.getText().trim(); }
}
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
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(UITheme.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ----- Fields -----
        studentIdField = createField(15);
        nameField = createField(22);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        genderCombo.setFont(UITheme.BODY);
        genderCombo.setPreferredSize(new Dimension(220, UITheme.INPUT_H));
        yearField = createField(22);
        majorField = createField(22);
        emailField = createField(22);
        phoneField = createField(22);
        guardianNameField = createField(22);
        guardianPhoneField = createField(22);
        addressField = createField(22);
        nrcField = createField(22);

        // Lock gender if a warden is logged in
        if (hostelType != null) {
            String lockedGender = "Boys".equalsIgnoreCase(hostelType) ? "Male" : "Female";
            genderCombo.setSelectedItem(lockedGender);
            genderCombo.setEnabled(false);
        }

        // Pre-fill for edit
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
        addFormRow(formPanel, gbc, "Roll No:", studentIdField, row++);
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

        // ----- Buttons -----
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(UITheme.WHITE);
        JButton saveButton = UITheme.createButton("Save", UITheme.PRIMARY);
        JButton cancelButton = UITheme.createButton("Cancel", UITheme.GRAY);

        saveButton.addActionListener(e -> {
            if (studentIdField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Roll No. and Name are required!",
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

        // Focus first editable field
        if (mode == 0) {
            SwingUtilities.invokeLater(studentIdField::requestFocusInWindow);
        } else {
            SwingUtilities.invokeLater(nameField::requestFocusInWindow);
        }
    }

    private JTextField createField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(UITheme.BODY);
        field.setPreferredSize(new Dimension(220, UITheme.INPUT_H));
        return field;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String labelText, Component field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        JLabel label = new JLabel(labelText);
        label.setFont(UITheme.BODY);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        panel.add(field, gbc);
    }

    // ---- Getters ----
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
package com.hostel.db;

import com.hostel.gui.MainFrame;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // CHANGE THESE to match your MySQL setup
    private static final String URL = "jdbc:mysql://localhost:3306/smart_hostel";
    private static final String USER = "root";          // or your custom user
    private static final String PASSWORD = "smn12345";

    // Static block to load the driver (optional for modern JDBC but safe)
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found!");
            e.printStackTrace();
        }
    }

    /**
     * Returns a new database connection.
     * Caller is responsible for closing it.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        // Quick database test
        try {
            Connection conn = DBConnection.getConnection();
            System.out.println("Database connected successfully!");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Database connection failed:");
            e.printStackTrace();
        }

        // Then launch the GUI as usual
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}

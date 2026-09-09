package com.hostel.dao;

import com.hostel.db.DBConnection;
import java.sql.*;

public class UserDAO {

    /**
     * Authenticate user.
     * Returns "admin", "warden", or null if invalid.
     */
    public String authenticate(String username, String password) {
        String sql = "SELECT role FROM User WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("role");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Seed default users if User table is empty.
     * Creates admin/admin123 and warden/warden.
     */
    public void seedDefaultUsers() {
        if (countUsers() > 0) return;

        Integer firstHostelId = getFirstHostelId();

        String sql = "INSERT INTO User (username, password, role, hostel_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Admin user
            ps.setString(1, "admin");
            ps.setString(2, "admin123");
            ps.setString(3, "admin");
            ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();

            // Warden user
            ps.setString(1, "warden");
            ps.setString(2, "warden");
            ps.setString(3, "warden");
            if (firstHostelId != null) {
                ps.setInt(4, firstHostelId);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int countUsers() {
        String sql = "SELECT COUNT(*) FROM User";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Integer getFirstHostelId() {
        String sql = "SELECT hostel_id FROM Hostel LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("hostel_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
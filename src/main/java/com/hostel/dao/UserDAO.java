package com.hostel.dao;

import com.hostel.db.DBConnection;
import java.sql.*;

public class UserDAO {

    /**
     * Returns Object[]{role, hostelId, hostelType} or null if invalid.
     * hostelId/hostelType are null for admin.
     */
    public Object[] authenticateAndGetUser(String username, String password) {
        String sql = "SELECT u.role, u.hostel_id, h.type " +
                "FROM User u LEFT JOIN Hostel h ON u.hostel_id = h.hostel_id " +
                "WHERE u.username = ? AND u.password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Integer hostelId = (Integer) rs.getObject("hostel_id");
                    String type = rs.getString("type");
                    return new Object[]{ rs.getString("role"), hostelId, type };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void seedDefaultUsers() {
        if (countUsers() > 0) return;

        Integer boysId = getHostelIdByType("Boys");
        Integer girlsId = getHostelIdByType("Girls");

        String sql = "INSERT INTO User (username, password, role, hostel_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Admin
            ps.setString(1, "admin");
            ps.setString(2, "admin123");
            ps.setString(3, "admin");
            ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();

            // Boys warden
            if (boysId != null) {
                ps.setString(1, "boyswarden");
                ps.setString(2, "boyswarden");
                ps.setString(3, "warden");
                ps.setInt(4, boysId);
                ps.executeUpdate();
            }

            // Girls warden
            if (girlsId != null) {
                ps.setString(1, "girlswarden");
                ps.setString(2, "girlswarden");
                ps.setString(3, "warden");
                ps.setInt(4, girlsId);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int countUsers() {
        String sql = "SELECT COUNT(*) FROM User";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Integer getHostelIdByType(String type) {
        String sql = "SELECT hostel_id FROM Hostel WHERE type = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("hostel_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
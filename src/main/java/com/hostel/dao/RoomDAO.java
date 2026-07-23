package com.hostel.dao;

import com.hostel.db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RoomDAO {

    // ---------------- Existing method (unchanged) ----------------
    public Map<Integer, String> getRoomMap() {
        Map<Integer, String> rooms = new LinkedHashMap<>();
        String sql = "SELECT r.room_id, CONCAT(r.room_number, ' (', h.hostel_name, ')') AS display " +
                "FROM Room r JOIN Hostel h ON r.hostel_id = h.hostel_id ORDER BY h.hostel_name, r.room_number";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.put(rs.getInt("room_id"), rs.getString("display"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    // ---------------- New methods for Room Management ----------------

    /**
     * Get all rooms with occupancy info.
     * Returns list of Object[]: room_id, room_number, floor_number, capacity, hostel_name, type.
     */
    public List<Object[]> getAllRooms() {
        List<Object[]> rooms = new ArrayList<>();
        String sql = "SELECT r.room_id, r.room_number, r.floor_number, r.capacity, " +
                "h.hostel_name, h.type " +
                "FROM Room r JOIN Hostel h ON r.hostel_id = h.hostel_id " +
                // Sort by floor first, then by numeric part of room number
                "ORDER BY r.floor_number, CAST(SUBSTRING(r.room_number, 2) AS UNSIGNED)";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rooms.add(new Object[]{
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getInt("floor_number"),
                        rs.getInt("capacity"),
                        rs.getString("hostel_name"),
                        rs.getString("type")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    /**
     * Get all students assigned to a specific room.
     * Returns list of Object[]: student_id, student_name, year, major, phone_number.
     */
    public List<Object[]> getStudentsByRoom(int roomId) {
        List<Object[]> students = new ArrayList<>();
        String sql = "SELECT student_id, student_name, year, major, phone_number " +
                "FROM Student WHERE room_id = ? ORDER BY student_name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(new Object[]{
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("year"),
                            rs.getString("major"),
                            rs.getString("phone_number")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Get all students who are NOT assigned to any room.
     * Returns list of Object[]: student_id, student_name, year, major, phone_number.
     */
    public List<Object[]> getUnassignedStudents() {
        List<Object[]> students = new ArrayList<>();
        String sql = "SELECT student_id, student_name, year, major, phone_number " +
                "FROM Student WHERE room_id IS NULL ORDER BY student_name";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                students.add(new Object[]{
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("year"),
                        rs.getString("major"),
                        rs.getString("phone_number")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Assign a student to a room (update Student.room_id).
     */
    public boolean assignStudentToRoom(String studentId, int roomId) {
        String sql = "UPDATE Student SET room_id = ? WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Unassign a student from any room (set room_id to NULL).
     */
    public boolean unassignStudent(String studentId) {
        String sql = "UPDATE Student SET room_id = NULL WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get the number of students currently assigned to a room.
     */
    public int getOccupancyCount(int roomId) {
        String sql = "SELECT COUNT(*) FROM Student WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    /**
     * Insert a new room.
     */
    public boolean insertRoom(String roomNumber, int floorNumber, int capacity, int hostelId) {
        String sql = "INSERT INTO Room (room_number, floor_number, capacity, hostel_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ps.setInt(2, floorNumber);
            ps.setInt(3, capacity);
            ps.setInt(4, hostelId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing room.
     */
    public boolean updateRoom(int roomId, String roomNumber, int floorNumber, int capacity, int hostelId) {
        String sql = "UPDATE Room SET room_number=?, floor_number=?, capacity=?, hostel_id=? WHERE room_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomNumber);
            ps.setInt(2, floorNumber);
            ps.setInt(3, capacity);
            ps.setInt(4, hostelId);
            ps.setInt(5, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a room. Students must be unassigned first.
     * Returns true if successful.
     */
    public boolean deleteRoom(int roomId) {
        // First, unassign all students from this room
        String unassignSql = "UPDATE Student SET room_id = NULL WHERE room_id = ?";
        String deleteSql = "DELETE FROM Room WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps1 = conn.prepareStatement(unassignSql);
                 PreparedStatement ps2 = conn.prepareStatement(deleteSql)) {
                ps1.setInt(1, roomId);
                ps1.executeUpdate();
                ps2.setInt(1, roomId);
                int rows = ps2.executeUpdate();
                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
package com.hostel.dao;


import com.hostel.db.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    /**
     * Get the first student who has NOT been marked (present or absent) today.
     * Returns Object[]: student_id, student_name, gender, year, major, phone_number
     * or null if all marked.
     */
    public Object[] getNextUnmarkedStudent() {
        String sql = "SELECT s.student_id, s.student_name, s.gender, s.year, s.major, s.phone_number " +
                "FROM Student s " +
                "LEFT JOIN RollCall rc ON s.student_id = rc.student_id AND rc.date = CURDATE() " +
                "WHERE rc.rollcall_id IS NULL " +
                "ORDER BY s.student_id ASC " +
                "LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Object[]{
                        rs.getString("student_id"),
                        rs.getString("student_name"),
                        rs.getString("gender"),
                        rs.getString("year"),
                        rs.getString("major"),
                        rs.getString("phone_number")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mark attendance for a student today.
     * @param studentId roll number
     * @param status 'Present', 'Absent', or 'Leave'
     * @return true if inserted successfully
     */
    public boolean markAttendance(String studentId, String status) {
        String sql = "INSERT INTO RollCall (student_id, date, check_in_time, method, terminal_id, status) " +
                "VALUES (?, CURDATE(), ?, 'MANUAL', 'DESKTOP-1', ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            if ("Present".equalsIgnoreCase(status)) {
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            } else {
                ps.setNull(2, Types.TIMESTAMP);
            }
            ps.setString(3, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get attendance summary for all students between two dates (inclusive).
     * Returns list of Object[]: student_id, student_name, present_days, absent_days, leave_days
     */
    public List<Object[]> getAttendanceSummary(String startDate, String endDate) {
        List<Object[]> summary = new ArrayList<>();
        String sql = "SELECT s.student_id, s.student_name, " +
                "SUM(CASE WHEN rc.status = 'Present' THEN 1 ELSE 0 END) AS present_days, " +
                "SUM(CASE WHEN rc.status = 'Absent' THEN 1 ELSE 0 END) AS absent_days, " +
                "SUM(CASE WHEN rc.status = 'Leave' THEN 1 ELSE 0 END) AS leave_days " +
                "FROM Student s " +
                "LEFT JOIN RollCall rc ON s.student_id = rc.student_id " +
                "AND rc.date BETWEEN ? AND ? " +
                "GROUP BY s.student_id, s.student_name " +
                "ORDER BY s.student_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    summary.add(new Object[]{
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getInt("present_days"),
                            rs.getInt("absent_days"),
                            rs.getInt("leave_days")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }
}
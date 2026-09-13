package com.hostel.dao;

import com.hostel.db.DBConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    /**
     * Get the first student who has NOT been marked (present/absent/leave) today.
     */
    public Object[] getNextUnmarkedStudent(String hostelType) {
        String sql = "SELECT s.student_id, s.student_name, s.gender, s.year, s.major, s.phone_number " +
                "FROM Student s " +
                "LEFT JOIN RollCall rc ON s.student_id = rc.student_id AND rc.date = CURDATE() " +
                "WHERE rc.rollcall_id IS NULL ";
        if (hostelType != null) {
            sql += " AND s.gender = ? ";
        }
        sql += " ORDER BY s.student_id ASC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hostelType != null) {
                ps.setString(1, "Boys".equalsIgnoreCase(hostelType) ? "Male" : "Female");
            }
            try (ResultSet rs = ps.executeQuery()) {
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
     * @param remark optional remark (e.g., leave reason)
     * @return true if inserted successfully
     */
    public boolean markAttendance(String studentId, String status, String remark) {
        String sql = "INSERT INTO RollCall (student_id, date, check_in_time, method, terminal_id, status, remark) " +
                "VALUES (?, CURDATE(), ?, 'MANUAL', 'DESKTOP-1', ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            if ("Present".equalsIgnoreCase(status)) {
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            } else {
                ps.setNull(2, Types.TIMESTAMP);
            }
            ps.setString(3, status);
            if (remark == null || remark.trim().isEmpty()) {
                ps.setNull(4, Types.VARCHAR);
            } else {
                ps.setString(4, remark);
            }
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

    /**
     * Get attendance log for a specific date (all students with status/remark).
     * @param date in 'YYYY-MM-DD' format
     * @return list of Object[]: student_id, student_name, status, remark
     */
    public List<Object[]> getDailyAttendance(String date, String hostelType) {
        List<Object[]> records = new ArrayList<>();
        String sql = "SELECT s.student_id, s.student_name, " +
                "COALESCE(rc.status, 'Unmarked') AS status, " +
                "rc.remark " +
                "FROM Student s " +
                "LEFT JOIN RollCall rc ON s.student_id = rc.student_id AND rc.date = ? ";
        if (hostelType != null) {
            sql += " WHERE s.gender = ? ";
        }
        sql += " ORDER BY s.student_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, date);
            if (hostelType != null) {
                ps.setString(2, "Boys".equalsIgnoreCase(hostelType) ? "Male" : "Female");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(new Object[]{
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("status"),
                            rs.getString("remark")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    public int[] getStudentMonthlySummary(String studentId, String startDate, String endDate) {
        String sql = "SELECT " +
                "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN status = 'Absent' THEN 1 ELSE 0 END), " +
                "SUM(CASE WHEN status = 'Leave' THEN 1 ELSE 0 END) " +
                "FROM RollCall " +
                "WHERE student_id = ? AND date BETWEEN ? AND ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, startDate);
            ps.setString(3, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[]{
                            rs.getInt(1),  // present
                            rs.getInt(2),  // absent
                            rs.getInt(3)   // leave
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new int[]{0, 0, 0};
    }
    /**
     * Get daily records for one student in a date range.
     * Returns list of Object[]: date (LocalDate), status, remark
     */
    public List<Object[]> getStudentDailyRecords(String studentId, String startDate, String endDate) {
        List<Object[]> records = new ArrayList<>();
        String sql = "SELECT date, status, remark FROM RollCall " +
                "WHERE student_id = ? AND date BETWEEN ? AND ? " +
                "ORDER BY date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, startDate);
            ps.setString(3, endDate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    records.add(new Object[]{
                            rs.getDate("date").toLocalDate(),
                            rs.getString("status"),
                            rs.getString("remark")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
}
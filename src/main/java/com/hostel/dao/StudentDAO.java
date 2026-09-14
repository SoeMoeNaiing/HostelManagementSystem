package com.hostel.dao;

import com.hostel.db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public List<Object[]> getAllStudents(String hostelType) {
        List<Object[]> students = new ArrayList<>();
        String sql = "SELECT student_id, student_name, gender, year, major, phone_number " +
                "FROM Student";
        if (hostelType != null) {
            sql += " WHERE gender = ?";
        }
        sql += " ORDER BY student_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (hostelType != null) {
                String gender = "Boys".equalsIgnoreCase(hostelType) ? "Male" : "Female";
                ps.setString(1, gender);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    students.add(new Object[]{
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("gender"),
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

    public Object[] getStudentById(String studentId) {
        String sql = "SELECT * FROM Student WHERE student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // room_id can be NULL – retrieve as Integer
                    Integer roomId = (Integer) rs.getObject("room_id");
                    return new Object[]{
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("gender"),
                            rs.getString("year"),
                            rs.getString("major"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("guardian_name"),
                            rs.getString("guardian_phone"),
                            rs.getString("address"),
                            rs.getString("nrc"),
                            roomId               // can be null
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Insert student: room_id is now optional (NULL)
    public boolean insertStudent(String studentId, String name, String gender, String year,
                                 String major, String email, String phone,
                                 String guardianName, String guardianPhone,
                                 String address, String nrc) {
        String sql = "INSERT INTO Student (student_id, student_name, gender, year, major, email, " +
                "phone_number, guardian_name, guardian_phone, address, nrc, room_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,NULL )";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, name);
            ps.setString(3, gender);
            ps.setString(4, year);
            ps.setString(5, major);
            ps.setString(6, email);
            ps.setString(7, phone);
            ps.setString(8, guardianName);
            ps.setString(9, guardianPhone);
            ps.setString(10, address);
            ps.setString(11, nrc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update student: room_id not touched (remains as assigned via Room module)
    public boolean updateStudent(String studentId, String name, String gender, String year,
                                 String major, String email, String phone,
                                 String guardianName, String guardianPhone,
                                 String address, String nrc) {
        String sql = "UPDATE Student SET student_name=?, gender=?, year=?, major=?, email=?, " +
                "phone_number=?, guardian_name=?, guardian_phone=?, address=?, nrc=? " +
                "WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, gender);
            ps.setString(3, year);
            ps.setString(4, major);
            ps.setString(5, email);
            ps.setString(6, phone);
            ps.setString(7, guardianName);
            ps.setString(8, guardianPhone);
            ps.setString(9, address);
            ps.setString(10, nrc);
            ps.setString(11, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(String studentId) {
        String sql = "DELETE FROM Student WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object[] getStudentReportInfo(String studentId) {
        String sql = "SELECT s.student_id, s.student_name, s.gender, s.year, s.major, " +
                "r.room_number, h.hostel_name " +
                "FROM Student s " +
                "LEFT JOIN Room r ON s.room_id = r.room_id " +
                "LEFT JOIN Hostel h ON r.hostel_id = h.hostel_id " +
                "WHERE s.student_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[]{
                            rs.getString("student_id"),
                            rs.getString("student_name"),
                            rs.getString("gender"),
                            rs.getString("year"),
                            rs.getString("major"),
                            rs.getString("room_number"),   // may be null
                            rs.getString("hostel_name")    // may be null
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
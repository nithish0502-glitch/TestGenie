package com.examly.springapp.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.examly.springapp.config.JdbcUtils;
import com.examly.springapp.dao.ProductDAO;
import com.examly.springapp.exception.LowAttendanceException;
import com.examly.springapp.model.Product;

public class AttendanceDAOImpl implements ProductDAO {

    @Override
    public void markAttendance(Product attendance) {
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "INSERT INTO attendance(student_id, date, name, course, attendance_percentage) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ptmt = con.prepareStatement(query);
            ptmt.setInt(1, attendance.getStudentId());
            ptmt.setString(2, attendance.getDate());
            ptmt.setString(3, attendance.getName());
            ptmt.setString(4, attendance.getCourse());
            ptmt.setDouble(5, attendance.getAttendancePercentage());
            ptmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
 
    @Override
    public List<Product> getAttendanceByStudentId(int studentId) {
        List<Product> matchingAttendance = new ArrayList<>();
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "SELECT * FROM attendance WHERE student_id = ?";
            PreparedStatement ptmt = con.prepareStatement(query);
            ptmt.setInt(1, studentId);
            ResultSet rs = ptmt.executeQuery();
            while (rs.next()) {
                matchingAttendance.add(new Product(
                    rs.getInt("student_id"),
                    rs.getString("date"),
                    rs.getString("name"),
                    rs.getString("course"),
                    rs.getDouble("attendance_percentage")
                ));
            }
        } catch (SQLException | LowAttendanceException e) {
            System.out.println(e.getMessage());
        }
        return matchingAttendance;
    }

    @Override
    public List<Product> getStudentByAttendanceByRange(int minPercent, int maxPercent) {
        List<Product> matchingAttendance = new ArrayList<>();
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "SELECT * FROM attendance WHERE attendance_percentage BETWEEN ? AND ?";
            PreparedStatement ptmt = con.prepareStatement(query);
            ptmt.setInt(1, minPercent);
            ptmt.setInt(2, maxPercent);
            ResultSet rs = ptmt.executeQuery();
            while (rs.next()) {
                matchingAttendance.add(new Product(
                    rs.getInt("student_id"),
                    rs.getString("date"),
                    rs.getString("name"),
                    rs.getString("course"),
                    rs.getDouble("attendance_percentage")
                ));
            }
        } catch (SQLException | LowAttendanceException e) {
            System.out.println(e.getMessage());
        }
        return matchingAttendance;
    }

    @Override
    public List<Product> getStudentByAttendanceAndCourse(String course, int attendancePercentage) {
        List<Product> matchingAttendance = new ArrayList<>();
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "SELECT * FROM attendance WHERE course = ? AND attendance_percentage = ?";
            PreparedStatement ptmt = con.prepareStatement(query);
            ptmt.setString(1, course);
            ptmt.setInt(2, attendancePercentage);
            ResultSet rs = ptmt.executeQuery();
            while (rs.next()) {
                matchingAttendance.add(new Product(
                    rs.getInt("student_id"),
                    rs.getString("date"),
                    rs.getString("name"),
                    rs.getString("course"),
                    rs.getDouble("attendance_percentage")
                ));
            }
        } catch (SQLException | LowAttendanceException e) {
            System.out.println(e.getMessage());
        }
        return matchingAttendance;
    }

    @Override
    public void deleteStudentByAttendanceAndCourse(String course, int attendancePercentage) {
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "DELETE FROM attendance WHERE course = ? AND attendance_percentage < ?";
            PreparedStatement ptmt = con.prepareStatement(query);
            ptmt.setString(1, course);
            ptmt.setInt(2, attendancePercentage);
            ptmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateAttendanceByNameAndCourse(String name, String course, int newAttendancePercent) throws LowAttendanceException {
        if (newAttendancePercent < 40) {
            throw new LowAttendanceException("Attendance cannot be updated to below 40%");
        }
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "UPDATE attendance SET attendance_percentage = ? WHERE name = ? AND course = ?";
            PreparedStatement ptmt = con.prepareStatement(query);
            ptmt.setInt(1, newAttendancePercent);
            ptmt.setString(2, name);
            ptmt.setString(3, course);
            ptmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

package com.examly.springapp.service;

import java.util.List;

import com.examly.springapp.exception.LowAttendanceException;
import com.examly.springapp.model.Product;

public interface ProductService {
    void markAttendance(Product attendance) throws LowAttendanceException;
    List<Product> getAttendanceByStudentId(int studentId);
    List<Product> getStudentByAttendanceByRange(int minPercent, int maxPercent);
    void deleteStudentByAttendanceAndCourse(String course, int attendancePercentage);
    void updateAttendanceByNameAndCourse(String name, String course, int newAttendancePercent) throws LowAttendanceException;
}

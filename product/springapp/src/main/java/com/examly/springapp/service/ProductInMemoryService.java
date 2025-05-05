package com.examly.springapp.service;

import java.util.List;

import com.examly.springapp.exception.LowAttendanceException;
import com.examly.springapp.model.Product;

public interface ProductInMemoryService {
    Product markAttendance(Product attendance);

    List<Product> getAttendanceByStudentId(int studentId);

    List<Product> getStudentByAttendanceByRange(int minPercent, int maxPercent);

    List<Product> getStudentByAttendanceAndCourse(String course, int attendancePercentage);

    void deleteStudentByAttendanceAndCourse(String course, int attendancePercentage);

    void updateAttendanceByNameAndCourse(String name, String course, int newAttendancePercent) throws LowAttendanceException;
}

package com.examly.springapp.service.impl;

import java.util.List;

import com.examly.springapp.dao.AttendanceMemoryDAO;
import com.examly.springapp.dao.impl.AttendanceMemoryDAOImpl;
import com.examly.springapp.exception.LowAttendanceException;
import com.examly.springapp.model.Product;
import com.examly.springapp.service.ProductInMemoryService;
 
public class ProductInMemoryServiceImpl implements ProductInMemoryService {

    private AttendanceMemoryDAO attendanceDAO;

    public ProductInMemoryServiceImpl() {
        this.attendanceDAO = new AttendanceMemoryDAOImpl(); // using in-memory DAO
    }
 
    @Override
    public Product markAttendance(Product attendance) {
        return attendanceDAO.markAttendance(attendance);
    }

    @Override
    public List<Product> getAttendanceByStudentId(int studentId) {
        return attendanceDAO.getAttendanceByStudentId(studentId);
    }

    @Override
    public List<Product> getStudentByAttendanceByRange(int minPercent, int maxPercent) {
        return attendanceDAO.getStudentByAttendanceByRange(minPercent, maxPercent);
    }

    @Override
    public List<Product> getStudentByAttendanceAndCourse(String course, int attendancePercentage) {
        return attendanceDAO.getStudentByAttendanceAndCourse(course, attendancePercentage);
    }

    @Override
    public void deleteStudentByAttendanceAndCourse(String course, int attendancePercentage) {
        attendanceDAO.deleteStudentByAttendanceAndCourse(course, attendancePercentage);
    }

    @Override
    public void updateAttendanceByNameAndCourse(String name, String course, int newAttendancePercent)
            throws LowAttendanceException {
        attendanceDAO.updateAttendanceByNameAndCourse(name, course, newAttendancePercent);
    }
}

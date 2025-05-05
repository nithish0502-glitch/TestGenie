package com.examly.springapp.service.impl;

import java.util.List;

import com.examly.springapp.dao.ProductDAO;
import com.examly.springapp.model.Product;
import com.examly.springapp.dao.impl.ProductDAOImpl;
import com.examly.springapp.service.ProductService;

public class ProductServiceImpl implements ProductService {

    private ProductDAO attendanceDAO = new ProductDAOImpl();

    @Override
    public void deleteStudentByAttendanceAndCourse(String course, int attendancePercentage) {
        attendanceDAO.deleteStudentByAttendanceAndCourse(course, attendancePercentage);
        
    }

    @Override
    public List<Product> getStudentByAttendanceByRange(int minPercent, int maxPercent) {
        return attendanceDAO.getStudentByAttendanceByRange(minPercent, maxPercent);
    }

    @Override
    public List<Product> getAttendanceByStudentId(int studentId) { 
        return attendanceDAO.getAttendanceByStudentId(studentId);
    }

    @Override
    public void markAttendance(Product attendance) throws LowAttendanceException {
        if(attendance.getAttendancePercentage() < 40) throw new LowAttendanceException("Low Attendance");
        attendanceDAO.markAttendance(attendance);
        
    }

    @Override
    public void updateAttendanceByNameAndCourse(String name, String course, int newAttendancePercent) throws LowAttendanceException {
        attendanceDAO.updateAttendanceByNameAndCourse(name, course, newAttendancePercent);
        
    }
    
}
 
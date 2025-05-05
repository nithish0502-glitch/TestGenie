package com.examly.springapp.dao.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.examly.springapp.dao.AttendanceMemoryDAO;
import com.examly.springapp.exception.LowAttendanceException;
import com.examly.springapp.model.Product;

public class AttendanceMemoryDAOImpl implements AttendanceMemoryDAO {
     private List<Product> attendanceDatabase = new ArrayList<>();
  
    @Override
    public Product markAttendance(Product attendance) {
        attendanceDatabase.add(attendance);
        return attendance;
    }
  
    @Override
    public List<Product> getAttendanceByStudentId(int studentId) {
        List<Product> matchingAttendance = new ArrayList<>();
        for (Product att : attendanceDatabase) {
            if (att.getStudentId() == studentId) {
                matchingAttendance.add(att);
            }
        }
        return matchingAttendance;
    }

    @Override
    public List<Product> getStudentByAttendanceByRange(int minPercent, int maxPercent) {
        List<Product> matchingAttendance = new ArrayList<>();
        for (Product att : attendanceDatabase) {
            if (att.getAttendancePercentage() > minPercent && att.getAttendancePercentage() < maxPercent) {
                matchingAttendance.add(att);
            }
        }
        return matchingAttendance;
    }

    @Override
    public List<Product> getStudentByAttendanceAndCourse(String course, int attendancePercentage) {
        List<Product> matchingAttendance = new ArrayList<>();
        for (Product att : attendanceDatabase) {
            if (att.getCourse().equals(course) && att.getAttendancePercentage() == attendancePercentage) {
                matchingAttendance.add(att);
            }
        }
        return matchingAttendance;
    }

    @Override
    public void deleteStudentByAttendanceAndCourse(String course, int attendancePercentage) {
        Iterator<Product> iterator = attendanceDatabase.iterator();
        while (iterator.hasNext()) {
            Product att = iterator.next();
            if (att.getCourse().equals(course) && att.getAttendancePercentage() < attendancePercentage) {
                iterator.remove();
            }
        }
    }
  
    @Override
    public void updateAttendanceByNameAndCourse(String name, String course, int newAttendancePercent) throws LowAttendanceException {
        for (Product att : attendanceDatabase) {
            if (att.getName().equals(name) && att.getCourse().equals(course)) {
                att.setAttendancePercentage(newAttendancePercent);
            }
        }
    }
}

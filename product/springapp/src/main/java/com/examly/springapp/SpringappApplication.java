package com.examly.springapp;
import java.util.Scanner;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.examly.springapp.exception.LowAttendanceException;
import com.examly.springapp.model.Product;
import com.examly.springapp.service.ProductService;
import com.examly.springapp.service.impl.ProductServiceImpl;


@SpringBootApplication
public class SpringappApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringappApplication.class, args);
        Scanner scanner = new Scanner(System.in);
        int choice = Integer.parseInt(scanner.nextLine());
        ProductService attendanceService = new ProductServiceImpl();
        switch(choice){
            case 1: 
                    try{
                        attendanceService.markAttendance(new Product(Integer.parseInt(scanner.nextLine()), scanner.nextLine(), scanner.nextLine(), scanner.nextLine(), Double.parseDouble(scanner.nextLine())));

                    }catch(LowAttendanceException e){
                        System.out.println("Low Attendance");
                    }
                    break;
            case 2: 
                    attendanceService.getStudentByAttendanceByRange(Integer.parseInt(scanner.nextLine()), Integer.parseInt(scanner.nextLine()));
                    break;
            case 3: 
                    break;
            case 4: 
            try {
                attendanceService.updateAttendanceByNameAndCourse(
                    scanner.nextLine(),
                    scanner.nextLine(),
                    Integer.parseInt(scanner.nextLine())
                );
            } catch (LowAttendanceException e) {
                System.out.println(e.getMessage());
            }
            break;
            case 5: 
                    attendanceService.deleteStudentByAttendanceAndCourse(scanner.nextLine(), Integer.parseInt(scanner.nextLine()));
                    break;
            case 6:
                    System.out.println("Program Exited!");
                    break;
        }
        System.out.println("1. Add a new Attendance Record");
        System.out.println("2. Display Attendance Records by Range");
        System.out.println("3. Display Attendance Records Ordered by Student ID");
        System.out.println("4. Update Attendance by Name and Course");
        System.out.println("5. Delete Attendance by Percentage and Course");
        System.out.println("6. Exit");
        try{
            Product attendance1 = new Product(001, "09/10/2001","Ishan", "SDET", 99.99 );
            Product attendance2 = new Product(002, "09/10/2003","Student2", "Java Full Stack", 39 );
            if(attendance1.getAttendancePercentage() <40 || attendance2.getAttendancePercentage()< 40) throw new LowAttendanceException("Attendance is Low");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
     

    }
    // Rest of the methods for sorting and displaying data
    public static void displayAttendanceDetails(Product attendance){
       System.out.printf("StudentId: %d, date: %s, name: %s, course: %s, attendance Percentage: %.2f", attendance.getStudentId(), attendance.getDate(), attendance.getName(), attendance.getCourse(), attendance.getAttendancePercentage());
    }
}

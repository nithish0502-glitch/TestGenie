package com.examly.springapp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import org.junit.jupiter.api.MethodOrderer;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = SpringappApplication.class)
@AutoConfigureMockMvc
public class SpringappApplicationTests {

    private static Object serviceInstance;
    private static Class<?> attendanceClass;
    private static Constructor<?> constructor;
    private static Connection connection;
    private static Object attendanceDAO;

    @BeforeAll
    public static void setUp() throws Exception {
        Class<?> serviceClass = Class.forName("com.examly.springapp.service.impl.AttendanceInMemoryServiceImpl");
        serviceInstance = serviceClass.getDeclaredConstructor().newInstance();

        attendanceClass = Class.forName("com.examly.springapp.model.Attendance");
        constructor = attendanceClass.getConstructor(int.class, String.class, String.class, String.class, double.class);
        Class<?> daoClass = Class.forName("com.examly.springapp.dao.impl.AttendanceDAOImpl");
        attendanceDAO = daoClass.getDeclaredConstructor().newInstance();

        Class<?> jdbcUtilsClass = Class.forName("com.examly.springapp.config.JdbcUtils");
        connection = (Connection) jdbcUtilsClass.getMethod("getConnection").invoke(null);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        clearDatabase();
        System.out.println("Database cleared after all tests.");
    }

    private static void clearDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM attendance");
        }
    }

    @Test
    public void Week1_Day1_AllRequiredFoldersExist() {
        String[] requiredFolders = {
                "src/main/java/com/examly/springapp/dao",
                "src/main/java/com/examly/springapp/config",
                "src/main/java/com/examly/springapp/model",
                "src/main/java/com/examly/springapp/service",
                "src/main/java/com/examly/springapp/exception"
        };

        for (String folderPath : requiredFolders) {
            File directory = new File(folderPath);
            assertTrue(directory.exists() && directory.isDirectory(),
                    "Folder should exist: " + folderPath);
        }
    }

    @Test
    public void Week1_Day1_AllRequiredClassesExist() {
        String[] requiredClasses = {
                "com.examly.springapp.model.Attendance",
                "com.examly.springapp.service.AttendanceInMemoryService",
                "com.examly.springapp.dao.impl.AttendanceInMemoryDAOImpl"
        };

        for (String className : requiredClasses) {
            try {
                Class.forName(className);
            } catch (ClassNotFoundException e) {
                fail("Class should exist: " + className);
            }
        }
    }

    @Test
    public void Week1_Day1_testValidAttendanceCreation() throws Exception {
        // Load Attendance class
        Class<?> attendanceClass = Class.forName("com.examly.springapp.model.Attendance");

        // Create a constructor with appropriate parameters
        Constructor<?> constructor = attendanceClass.getConstructor(int.class, String.class, String.class, String.class,
                double.class);

        // Create valid attendance object (attendance percentage > 40)
        Object validAttendance = constructor.newInstance(1, "2024-04-01", "John Doe", "Math", 75.0);

        // Validate the attendance was created successfully
        Method toStringMethod = attendanceClass.getMethod("toString");
        String attendanceDetails = (String) toStringMethod.invoke(validAttendance);

        // Assert that the attendance details contain expected values
        assertTrue(attendanceDetails.contains("John Doe"));
        assertTrue(attendanceDetails.contains("Math"));
        assertTrue(attendanceDetails.contains("75.0"));
    }

    @Test
    public void Week1_Day1_testLowAttendanceThrowsException() throws Exception {
        // Load Product class (renamed from Attendance to match product-based domain)
        Class<?> productClass = Class.forName("com.examly.springapp.model.Product");
    
        // Create constructor with appropriate parameters
        Constructor<?> constructor = productClass.getConstructor(int.class, String.class, String.class, String.class, double.class);
    
        // Load exception class via reflection
        Class<?> exceptionClass = Class.forName("com.examly.springapp.exception.LowStockException");
    
        // Trigger exception through reflection
        Exception exception = assertThrows(Exception.class, () -> {
            constructor.newInstance(2, "2024-04-01", "Jane Doe", "Math", 5.0); // stock below 10
        });
        
        assertTrue(exceptionClass.isInstance(exception.getCause()));
        assertTrue(exception.getCause().getMessage().contains("Stock is low"));
    }
    

    @Test
    @Order(1)
    public void Week1_Day2_testMarkAttendance_InMemoryList() throws Exception {
        Object att1 = constructor.newInstance(1, "2024-04-29", "Alice", "Math", 85.0);
        Object att2 = constructor.newInstance(2, "2024-04-29", "Bob", "Physics", 75.0);
        Object att3 = constructor.newInstance(3, "2024-04-29", "Charlie", "Chemistry", 60.0);
        Object att4 = constructor.newInstance(4, "2024-04-29", "Diana", "Math", 95.0);

        Method markMethod = serviceInstance.getClass().getMethod("markAttendance", attendanceClass);
        markMethod.invoke(serviceInstance, att1);
        markMethod.invoke(serviceInstance, att2);
        markMethod.invoke(serviceInstance, att3);
        markMethod.invoke(serviceInstance, att4);

        // Access internal DAO and list
        Field daoField = serviceInstance.getClass().getDeclaredField("attendanceDAO");
        daoField.setAccessible(true);
        Object daoInstance = daoField.get(serviceInstance);

        Field internalListField = daoInstance.getClass().getDeclaredField("attendanceDatabase");
        internalListField.setAccessible(true);
        List<?> attendanceList = (List<?>) internalListField.get(daoInstance);

        assertEquals(4, attendanceList.size(), "All attendance records should be added to the list");
    }

    @Test
    @Order(2)
    public void Week1_Day2_testGetAttendanceByStudentId_InMemoryList() throws Exception {
        Method getById = serviceInstance.getClass().getMethod("getAttendanceByStudentId", int.class);
        List<?> result = (List<?>) getById.invoke(serviceInstance, 1);

        Method toStringMethod = attendanceClass.getMethod("toString");
        String resultStr = (String) toStringMethod.invoke(result.get(0));

        assertTrue(resultStr.contains("Alice"));
        assertTrue(resultStr.contains("Math"));
        assertTrue(resultStr.contains("85.0"));
    }

    @Test
    @Order(3)
    public void Week1_Day2_testUpdateAttendance_InMemoryList() throws Exception {
        Method updateMethod = serviceInstance.getClass().getMethod("updateAttendanceByNameAndCourse", String.class,
                String.class, int.class);
        updateMethod.invoke(serviceInstance, "Alice", "Math", 92);

        Method getById = serviceInstance.getClass().getMethod("getAttendanceByStudentId", int.class);
        List<?> result = (List<?>) getById.invoke(serviceInstance, 1);

        Method toStringMethod = attendanceClass.getMethod("toString");
        String resultStr = (String) toStringMethod.invoke(result.get(0));

        assertTrue(resultStr.contains("92.0"));
    }

    @Test
    @Order(4)
    public void Week1_Day2_testDeleteAttendanceByCourseAndPercent_InMemoryList() throws Exception {
        Method deleteMethod = serviceInstance.getClass().getMethod("deleteStudentByAttendanceAndCourse", String.class,
                int.class);
        deleteMethod.invoke(serviceInstance, "Physics", 80); // Bob = 75 → should be deleted

        Field daoField = serviceInstance.getClass().getDeclaredField("attendanceDAO");
        daoField.setAccessible(true);
        Object daoInstance = daoField.get(serviceInstance);

        Field internalListField = daoInstance.getClass().getDeclaredField("attendanceDatabase");
        internalListField.setAccessible(true);
        List<?> attendanceList = (List<?>) internalListField.get(daoInstance);

        Method toStringMethod = attendanceClass.getMethod("toString");

        assertEquals(3, attendanceList.size(), "Bob should be deleted");

        for (Object att : attendanceList) {
            String info = (String) toStringMethod.invoke(att);
            assertFalse(info.contains("Bob"), "Bob should have been removed");
        }
    }

    @Test
    @Order(5)
    public void Week1_Day2_testGetAllByRange_InMemoryList() throws Exception {
        Method rangeMethod = serviceInstance.getClass().getMethod("getStudentByAttendanceByRange", int.class,
                int.class);
        List<?> result = (List<?>) rangeMethod.invoke(serviceInstance, 80, 100);

        assertEquals(2, result.size(), "Alice and Diana should be in the 80–100 range");

        Method toStringMethod = attendanceClass.getMethod("toString");

        for (Object att : result) {
            String info = (String) toStringMethod.invoke(att);
            assertTrue(info.contains("Alice") || info.contains("Diana"));
        }
    }

    @Test
    @Order(6)
    public void Week1_Day2_testGetByAttendanceAndCourse_InMemoryList() throws Exception {
        Method byCourseAndPercentMethod = serviceInstance.getClass().getMethod(
                "getStudentByAttendanceAndCourse", String.class, int.class);

        List<?> result = (List<?>) byCourseAndPercentMethod.invoke(serviceInstance, "Math", 95);

        assertEquals(1, result.size(), "Only Diana has 95% in Math");

        Method toStringMethod = attendanceClass.getMethod("toString");
        String info = (String) toStringMethod.invoke(result.get(0));

        assertTrue(info.contains("Diana"));
        assertTrue(info.contains("Math"));
        assertTrue(info.contains("95.0"));
    }

    private int getRowCount() throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM attendance");
                ResultSet rs = pstmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Test
    @Order(7)
    public void Week1_Day3_testMarkAttendance() throws Exception {
        int rowCountBefore = getRowCount();

        Object att1 = attendanceClass.getConstructor(
                int.class, String.class, String.class, String.class, double.class)
                .newInstance(101, "2024-04-01", "Alice", "Math", 75.0);

        Object att2 = attendanceClass.getConstructor(
                int.class, String.class, String.class, String.class, double.class)
                .newInstance(102, "2024-04-01", "Bob", "Physics", 60.0);

        Method markMethod = attendanceDAO.getClass().getMethod("markAttendance", attendanceClass);
        markMethod.invoke(attendanceDAO, att1);
        markMethod.invoke(attendanceDAO, att2);

        int rowCountAfter = getRowCount();
        assertEquals(rowCountBefore + 2, rowCountAfter, "Two attendance records should be added");
    }

    @Test
    @Order(8)
    public void Week1_Day3_testGetAttendanceByStudentId() throws Exception {
        Method getByIdMethod = attendanceDAO.getClass().getMethod("getAttendanceByStudentId", int.class);
        Object result = getByIdMethod.invoke(attendanceDAO, 101);

        assertTrue(result instanceof List<?>);
        List<?> list = (List<?>) result;
        assertFalse(list.isEmpty());

        Method getName = attendanceClass.getMethod("getName");
        assertEquals("Alice", getName.invoke(list.get(0)));
    }

    @Test
    @Order(9)
    public void Week1_Day3_testGetAllAttendanceByRange() throws Exception {
        Method method = attendanceDAO.getClass().getMethod("getStudentByAttendanceByRange", int.class, int.class);
        Object result = method.invoke(attendanceDAO, 50, 80);

        assertTrue(result instanceof List<?>);
        List<?> list = (List<?>) result;
        assertFalse(list.isEmpty());

        Method getCourse = attendanceClass.getMethod("getCourse");
        boolean hasMath = false;
        for (Object obj : list) {
            if ("Math".equals(getCourse.invoke(obj))) {
                hasMath = true;
            }
        }
        assertTrue(hasMath, "Math course record should exist in range result");
    }

    @Test
    @Order(10)
    public void Week1_Day3_testGetStudentByAttendanceAndCourse() throws Exception {
        Method method = attendanceDAO.getClass().getMethod("getStudentByAttendanceAndCourse", String.class, int.class);
        Object result = method.invoke(attendanceDAO, "Math", 75);

        assertTrue(result instanceof List<?>);
        List<?> list = (List<?>) result;
        assertFalse(list.isEmpty());

        Method getName = attendanceClass.getMethod("getName");
        assertEquals("Alice", getName.invoke(list.get(0)));
    }

    @Test
    @Order(11)
    public void Week1_Day3_testUpdateAttendanceByNameAndCourse() throws Exception {
        Method updateMethod = attendanceDAO.getClass().getMethod("updateAttendanceByNameAndCourse", String.class,
                String.class, int.class);
        updateMethod.invoke(attendanceDAO, "Alice", "Math", 85);

        Method getMethod = attendanceDAO.getClass().getMethod("getStudentByAttendanceAndCourse", String.class,
                int.class);
        Object result = getMethod.invoke(attendanceDAO, "Math", 85);

        List<?> updatedList = (List<?>) result;
        assertFalse(updatedList.isEmpty());

        Method getPercent = attendanceClass.getMethod("getAttendancePercentage");
        assertEquals(85.0, (double) getPercent.invoke(updatedList.get(0)));
    }

    @Test
    @Order(12)
    public void Week1_Day3_testDeleteByCourseAndAttendance() throws Exception {
        int rowCountBefore = getRowCount();

        Method deleteMethod = attendanceDAO.getClass().getMethod("deleteStudentByAttendanceAndCourse", String.class,
                int.class);
        deleteMethod.invoke(attendanceDAO, "Physics", 70);

        int rowCountAfter = getRowCount();
        assertTrue(rowCountAfter < rowCountBefore, "Rows with attendance < 70 should be deleted from Physics");
    }

    @Test
    public void Week1_Day4_testSwitchInMainMethod() {
        String filePath = "src/main/java/com/examly/springapp/SpringappApplication.java";

        boolean switchFound = isSwitchPresentInMain(filePath);

        assertTrue(switchFound, "The switch statement should be present in the main method");
    }

    public static boolean isSwitchPresentInMain(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inMainMethod = false;
            while ((line = br.readLine()) != null) {
                if (line.contains("public static void main")) {
                    inMainMethod = true;
                }
                if (inMainMethod && line.contains("switch")) {
                    return true; 
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // return false if no switch is found
    }

}
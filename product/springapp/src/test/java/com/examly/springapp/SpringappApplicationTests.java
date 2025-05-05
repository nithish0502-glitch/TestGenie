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
    

     private static Object serviceInstance;
    private static Class<?> productClass;
    private static Constructor<?> constructor;
    private static Connection connection;
    private static Object productDAO;

    @BeforeAll
    public static void setUp() throws Exception {
        Class<?> serviceClass = Class.forName("com.examly.springapp.service.impl.ProductInMemoryServiceImpl");
        serviceInstance = serviceClass.getDeclaredConstructor().newInstance();

        productClass = Class.forName("com.examly.springapp.model.Product");
        constructor = productClass.getConstructor(int.class, String.class, String.class, double.class, int.class, boolean.class);

        Class<?> daoClass = Class.forName("com.examly.springapp.dao.impl.ProductInMemoryDAOImpl");
        productDAO = daoClass.getDeclaredConstructor().newInstance();

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
            stmt.executeUpdate("DELETE FROM product");
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
                "com.examly.springapp.model.Product",
                "com.examly.springapp.service.ProductInMemoryService",
                "com.examly.springapp.dao.impl.ProductInMemoryDAOImpl"
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
    public void Week1_Day1_testValidProductCreation() throws Exception {
        Object product = constructor.newInstance(1, "Laptop", "Electronics", 1200.0, 20, true);

        Method toStringMethod = productClass.getMethod("toString");
        String productDetails = (String) toStringMethod.invoke(product);

        assertTrue(productDetails.contains("Laptop"));
        assertTrue(productDetails.contains("Electronics"));
        assertTrue(productDetails.contains("1200.0"));
        assertTrue(productDetails.contains("true"));
    }

    @Test
    public void Week1_Day1_testLowStockThrowsException() throws Exception {
        Class<?> exceptionClass = Class.forName("com.examly.springapp.exception.LowStockException");

        Exception exception = assertThrows(Exception.class, () -> {
            constructor.newInstance(2, "Mouse", "Accessories", 25.0, 5, true);
        });

        assertTrue(exceptionClass.isInstance(exception.getCause()));
        assertTrue(exception.getCause().getMessage().contains("Stock is low"));
    }

    @Test
    @Order(1)
    public void Week1_Day2_testAddProduct_InMemoryList() throws Exception {
        Object p1 = constructor.newInstance(1, "Monitor", "Electronics", 200.0, 15, true);
        Object p2 = constructor.newInstance(2, "Keyboard", "Accessories", 50.0, 12, true);
        Object p3 = constructor.newInstance(3, "Tablet", "Electronics", 300.0, 20, false);
        Object p4 = constructor.newInstance(4, "Mouse", "Accessories", 25.0, 14, true);

        Method createMethod = serviceInstance.getClass().getMethod("addProduct", productClass);
        createMethod.invoke(serviceInstance, p1);
        createMethod.invoke(serviceInstance, p2);
        createMethod.invoke(serviceInstance, p3);
        createMethod.invoke(serviceInstance, p4);

        Field daoField = serviceInstance.getClass().getDeclaredField("productDAO");
        daoField.setAccessible(true);
        Object daoInstance = daoField.get(serviceInstance);

        Field listField = daoInstance.getClass().getDeclaredField("productDatabase");
        listField.setAccessible(true);
        List<?> products = (List<?>) listField.get(daoInstance);

        assertEquals(4, products.size());
    }

    @Test
    @Order(2)
    public void Week1_Day2_testGetProductById_InMemoryList() throws Exception {
        Method getById = serviceInstance.getClass().getMethod("getProductById", int.class);
        Object product = getById.invoke(serviceInstance, 1);

        Method toStringMethod = productClass.getMethod("toString");
        String resultStr = (String) toStringMethod.invoke(product);

        assertTrue(resultStr.contains("Monitor"));
    }

    @Test
    @Order(3)
    public void Week1_Day2_testUpdateProductByCategory_InMemoryList() throws Exception {
        Method updateMethod = serviceInstance.getClass().getMethod("updateProductByCategory", String.class, double.class, int.class);
        updateMethod.invoke(serviceInstance, "Electronics", 250.0, 18);

        Method getById = serviceInstance.getClass().getMethod("getProductById", int.class);
        Object product = getById.invoke(serviceInstance, 1);

        Method toStringMethod = productClass.getMethod("toString");
        String resultStr = (String) toStringMethod.invoke(product);

        assertTrue(resultStr.contains("250.0"));
    }

    @Test
    @Order(4)
    public void Week1_Day2_testDeleteByPrice_InMemoryList() throws Exception {
        Method deleteMethod = serviceInstance.getClass().getMethod("deleteProductByPrice", double.class);
        deleteMethod.invoke(serviceInstance, 30.0);

        Field daoField = serviceInstance.getClass().getDeclaredField("productDAO");
        daoField.setAccessible(true);
        Object daoInstance = daoField.get(serviceInstance);

        Field listField = daoInstance.getClass().getDeclaredField("productDatabase");
        listField.setAccessible(true);
        List<?> products = (List<?>) listField.get(daoInstance);

        Method toStringMethod = productClass.getMethod("toString");

        for (Object product : products) {
            String info = (String) toStringMethod.invoke(product);
            assertFalse(info.contains("Mouse"));
        }
    }

    @Test
    @Order(5)
    public void Week1_Day2_testViewByCategorySorted_InMemoryList() throws Exception {
        Method viewMethod = serviceInstance.getClass().getMethod("viewProductDetailsByCategory", String.class);
        List<?> result = (List<?>) viewMethod.invoke(serviceInstance, "Accessories");

        assertEquals(1, result.size());
        Method toStringMethod = productClass.getMethod("toString");
        String info = (String) toStringMethod.invoke(result.get(0));
        assertTrue(info.contains("Keyboard"));
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
package com.examly.springapp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    private static Class<?> productClass;
    private static Constructor<?> constructor;
    private static Connection connection;
    private static Object productDAO;

    @BeforeAll
    public static void setUp() throws Exception {
        Class<?> serviceClass = Class.forName("com.examly.springapp.service.impl.ProductInMemoryServiceImpl");
        serviceInstance = serviceClass.getDeclaredConstructor().newInstance();

        productClass = Class.forName("com.examly.springapp.model.Product");
        constructor = productClass.getConstructor(int.class, String.class, String.class, double.class, int.class,
                boolean.class);

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
            stmt.executeUpdate("DELETE FROM products");
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

        Method createMethod = serviceInstance.getClass().getMethod("createProduct", productClass);
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
        Method updateMethod = serviceInstance.getClass().getMethod("updateProductByCategory", String.class,
                double.class, int.class);
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
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM products");
                ResultSet rs = pstmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Test
    @Order(1)
    public void Week1_Day3_testCreateProduct() throws Exception {
        int rowCountBefore = getRowCount();

        Object product1 = productClass.getConstructor(
                int.class, String.class, String.class, double.class, int.class, boolean.class)
                .newInstance(201, "Laptop", "Electronics", 55000.0, 15, true);

        Object product2 = productClass.getConstructor(
                int.class, String.class, String.class, double.class, int.class, boolean.class)
                .newInstance(202, "Chair", "Furniture", 2500.0, 12, false);

        Method createMethod = productDAO.getClass().getMethod("createProduct", productClass);
       System.out.println(createMethod.invoke(productDAO, product1)); 
       System.out.println(); createMethod.invoke(productDAO, product2);
           
        int rowCountAfter = getRowCount();
        assertEquals(rowCountBefore + 2, rowCountAfter, "Two product records should be added");
    }

    @Test
    @Order(2)
    public void Week1_Day3_testGetProductById() throws Exception {
        Method getByIdMethod = productDAO.getClass().getMethod("getProductById", int.class);
        Object result = getByIdMethod.invoke(productDAO, 201);

        assertNotNull(result);
        Method getName = productClass.getMethod("getName");
        assertEquals("Laptop", getName.invoke(result));
    }

    @Test
    @Order(3)
    public void Week1_Day3_testUpdateProductByCategory() throws Exception {
        Method updateMethod = productDAO.getClass().getMethod("updateProductByCategory",
                String.class, double.class, int.class);
        Object result = updateMethod.invoke(productDAO, "Electronics", 60000.0, 20);

        assertTrue(result instanceof List<?>);
        List<?> list = (List<?>) result;
        assertFalse(list.isEmpty());

        Method getPrice = productClass.getMethod("getPrice");
        assertEquals(60000.0, getPrice.invoke(list.get(0)));
    }

    @Test
    @Order(4)
    public void Week1_Day3_testDeleteProductByPrice() throws Exception {
        int rowCountBefore = getRowCount();

        Method deleteMethod = productDAO.getClass().getMethod("deleteProductByPrice", double.class);
        Object result = deleteMethod.invoke(productDAO, 3000.0);

        assertTrue(result instanceof List<?>);
        int rowCountAfter = getRowCount();
        assertTrue(rowCountAfter < rowCountBefore, "Products below price 3000 should be deleted");
    }

    @Test
    @Order(5)
    public void Week1_Day3_testViewProductDetailsByCategory() throws Exception {
        Method viewMethod = productDAO.getClass().getMethod("viewProductDetailsByCategory", String.class);
        Object result = viewMethod.invoke(productDAO, "Electronics");

        assertTrue(result instanceof List<?>);
        List<?> list = (List<?>) result;
        assertFalse(list.isEmpty());

        Method getCategory = productClass.getMethod("getCategory");
        for (Object obj : list) {
            assertEquals("Electronics", getCategory.invoke(obj));
        }
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
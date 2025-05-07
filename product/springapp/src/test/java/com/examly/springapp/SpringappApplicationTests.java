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
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.examly.springapp.dao.impl.BookInMemoryDAOImpl;

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

      private static Object daoInstance;
    private static Class<?> bookClass;
    private static Constructor<?> constructor;
    private static Connection connection;
    private static Object bookDao;

    @BeforeAll
    public static void setUp() throws Exception {
        daoInstance = new BookInMemoryDAOImpl();

        bookClass = Class.forName("com.examly.springapp.model.Book");
        constructor = bookClass.getConstructor(int.class, String.class, String.class, float.class, boolean.class);

        Class<?> daoClass = Class.forName("com.examly.springapp.dao.impl.BookDAOImpl");
        bookDao = daoClass.getDeclaredConstructor().newInstance();

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
            stmt.executeUpdate("DELETE FROM books");
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
            "com.examly.springapp.model.Book",
            "com.examly.springapp.service.BookInMemoryService",
            "com.examly.springapp.service.BookService",
            "com.examly.springapp.service.impl.BookInMemoryServiceImpl",
            "com.examly.springapp.service.impl.BookServiceImpl",
            "com.examly.springapp.dao.impl.BookInMemoryDAOImpl",
            "com.examly.springapp.dao.impl.BookDAOImpl",
            "com.examly.springapp.dao.BookInMemoryDAO",
            "com.examly.springapp.dao.BookDAO",
            "com.examly.springapp.exception.LowPriceException",
            "com.examly.springapp.config.JdbcUtils"
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
    @Order(1)
    public void Week1_Day1_testValidBookCreation() throws Exception {
        Object book = constructor.newInstance(1, "The Alchemist", "Paulo Coelho", 350.0f, true);
        Method toStringMethod = bookClass.getMethod("toString");
        String details = (String) toStringMethod.invoke(book);

        assertTrue(details.contains("The Alchemist"));
        assertTrue(details.contains("Paulo Coelho"));
        assertTrue(details.contains("350.0"));
        assertTrue(details.contains("true"));
    }

    @Test
    @Order(2)
    public void Week1_Day1_testInvalidBookThrowsException() throws Exception {
        Class<?> exceptionClass = Class.forName("com.examly.springapp.exception.LowPriceException");

        Exception exception = assertThrows(Exception.class, () -> {
            constructor.newInstance(2, "Invisible Man", "Ralph Ellison", -100.0f, true);
        });

        assertTrue(exceptionClass.isInstance(exception.getCause()));
        assertTrue(exception.getCause().getMessage().contains("Price cannot be negative"));
    }

    @Test
    @Order(3)
    public void Week1_Day2_testAddBookToInMemoryList() throws Exception {
        Object b1 = constructor.newInstance(1, "Clean Code", "Robert C. Martin", 450.0f, true);
        Object b2 = constructor.newInstance(2, "1984", "George Orwell", 300.0f, false);

        Method addMethod = daoInstance.getClass().getMethod("createBook", bookClass);
        addMethod.invoke(daoInstance, b1);
        addMethod.invoke(daoInstance, b2);

        Field listField = daoInstance.getClass().getDeclaredField("bookDatabase");
        listField.setAccessible(true);
        List<?> books = (List<?>) listField.get(daoInstance);

        assertEquals(2, books.size());
    }

    @Test
    @Order(4)
    public void Week1_Day2_testUpdateBookInMemoryList() throws Exception {
        Object updatedBook = constructor.newInstance(1, "Clean Code 2nd Ed", "Robert C. Martin", 500.0f, false);

        Method updateMethod = daoInstance.getClass().getMethod("updateBook", bookClass);
        updateMethod.invoke(daoInstance, updatedBook);

        Field listField = daoInstance.getClass().getDeclaredField("bookDatabase");
        listField.setAccessible(true);
        List<?> books = (List<?>) listField.get(daoInstance);

        Method toStringMethod = bookClass.getMethod("toString");
        String info = (String) toStringMethod.invoke(books.get(0));

        assertTrue(info.contains("Clean Code 2nd Ed"));
        assertTrue(info.contains("500.0"));
    }

    @Test
    @Order(5)
    public void Week1_Day2_testDeleteBooksByAuthorWithLimit() throws Exception {
        Object b3 = constructor.newInstance(3, "Animal Farm", "George Orwella", 250.0f, true);
        Method addMethod = daoInstance.getClass().getMethod("createBook", bookClass);
        addMethod.invoke(daoInstance, b3);

        Method deleteMethod = daoInstance.getClass().getMethod("deleteBooksByAuthor", String.class, int.class);
        deleteMethod.invoke(daoInstance, "George Orwella", 1);

        Field listField = daoInstance.getClass().getDeclaredField("bookDatabase");
        listField.setAccessible(true);
        List<?> books = (List<?>) listField.get(daoInstance);

        for (Object book : books) {
            Method toStringMethod = bookClass.getMethod("toString");
            String info = (String) toStringMethod.invoke(book);
            assertFalse(info.contains("Animal Farm"));
        }
    }

    @Test
    @Order(6)
    public void Week1_Day2_testGetAllBooksSortedByTitle() throws Exception {
        Method getSortedBooks = daoInstance.getClass().getMethod("getAllBooksByTitle");
        List<?> sortedList = (List<?>) getSortedBooks.invoke(daoInstance);

        Method getTitle = bookClass.getMethod("getTitle");
        String title1 = (String) getTitle.invoke(sortedList.get(0));
        String title2 = (String) getTitle.invoke(sortedList.get(1));

        assertTrue(title1.compareToIgnoreCase(title2) < 0);
    }

    @Test
    @Order(7)
    public void Week1_Day2_testGetAvailableBooks() throws Exception {
        Method getAvailableMethod = daoInstance.getClass().getMethod("getAvailableBooks");
        List<?> availableBooks = (List<?>) getAvailableMethod.invoke(daoInstance);

        for (Object book : availableBooks) {
            Method isAvailable = bookClass.getMethod("isAvailable");
            assertTrue((Boolean) isAvailable.invoke(book));
        }
    }

    private int getRowCount() throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM books");
             ResultSet rs = pstmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Test
    @Order(1)
    public void Week1_Day3_testCreateBook() throws Exception {
        int rowCountBefore = getRowCount();

        Object book1 = bookClass.getConstructor(int.class, String.class, String.class, float.class, boolean.class)
                .newInstance(301, "Java Basics", "Alice", 450.0f, true);
        Object book2 = bookClass.getConstructor(int.class, String.class, String.class, float.class, boolean.class)
                .newInstance(302, "Data Structures", "Bob", 600.0f, false);

        Method createMethod = bookDao.getClass().getMethod("createBook", bookClass);
        createMethod.invoke(bookDao, book1);
        createMethod.invoke(bookDao, book2);

        int rowCountAfter = getRowCount();
        assertEquals(rowCountBefore + 2, rowCountAfter, "Two book records should be added");
    }

    @Test
    @Order(2)
    public void Week1_Day3_testUpdateBook() throws Exception {
        Object updatedBook = bookClass.getConstructor(int.class, String.class, String.class, float.class, boolean.class)
                .newInstance(301, "Advanced Java", "Alice", 500.0f, false);

        Method updateMethod = bookDao.getClass().getMethod("updateBook", bookClass);
        updateMethod.invoke(bookDao, updatedBook);

        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM books WHERE bookId = ?");
        stmt.setInt(1, 301);
        ResultSet rs = stmt.executeQuery();

        assertTrue(rs.next());
        assertEquals("Advanced Java", rs.getString("title"));
    }

    @Test
    @Order(3)
    public void Week1_Day3_testDeleteBooksByAuthor() throws Exception {
        int rowCountBefore = getRowCount();

        Method deleteMethod = bookDao.getClass().getMethod("deleteBooksByAuthor", String.class, int.class);
        deleteMethod.invoke(bookDao, "Bob", 1);

        int rowCountAfter = getRowCount();
        assertTrue(rowCountAfter < rowCountBefore, "At least one book should be deleted for author Bob");
    }

    @Test
    @Order(4)
    public void Week1_Day3_testGetAllBooksByTitle() throws Exception {
        Method viewMethod = bookDao.getClass().getMethod("getAllBooksByTitle");
        Object result = viewMethod.invoke(bookDao);

        assertTrue(result instanceof List<?>);
        List<?> list = (List<?>) result;
        assertFalse(list.isEmpty());

        Method getTitle = bookClass.getMethod("getTitle");
        List<String> titles = new ArrayList<>();
        for (Object obj : list) {
            titles.add((String) getTitle.invoke(obj));
        }

        List<String> sortedTitles = new ArrayList<>(titles);
        sortedTitles.sort(String.CASE_INSENSITIVE_ORDER);
        assertEquals(sortedTitles, titles, "Books should be sorted by title");
    }

    @Test
    @Order(5)
    public void Week1_Day3_testGetAvailableBooks() throws Exception {
        Method availableMethod = bookDao.getClass().getMethod("getAvailableBooks");
        Object result = availableMethod.invoke(bookDao);

        assertTrue(result instanceof List<?>);
        List<?> list = (List<?>) result;

        Method isAvailable = bookClass.getMethod("isAvailable");
        for (Object book : list) {
            assertTrue((Boolean) isAvailable.invoke(book), "Book should be available");
        }
    }

    @Test
    public void Week1_Day4_testSwitchInMainMethod() {
        String filePath = "src/main/java/com/examly/springapp/SpringappApplication.java";
        assertTrue(isSwitchPresentInMain(filePath), "The switch statement should be present in the main method");
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
        return false;
    }
}
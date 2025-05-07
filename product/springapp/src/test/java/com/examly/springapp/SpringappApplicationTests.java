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

    @BeforeAll
    public static void setUp() throws Exception {
        daoInstance = new BookInMemoryDAOImpl();

        bookClass = Class.forName("com.examly.springapp.model.Book");
        constructor = bookClass.getConstructor(int.class, String.class, String.class, float.class, boolean.class);
    }

    @Test
    @Order(1)
    public void testValidBookCreation() throws Exception {
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
    public void testInvalidBookThrowsException() throws Exception {
        Class<?> exceptionClass = Class.forName("com.examly.springapp.exception.InvalidBookPriceException");

        Exception exception = assertThrows(Exception.class, () -> {
            constructor.newInstance(2, "Invisible Man", "Ralph Ellison", -100.0, true);
        });

        assertTrue(exceptionClass.isInstance(exception.getCause()));
        assertTrue(exception.getCause().getMessage().contains("Invalid book price"));
    }

    @Test
    @Order(3)
    public void testAddBookToInMemoryList() throws Exception {
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
    public void testUpdateBookInMemoryList() throws Exception {
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
    public void testDeleteBooksByAuthorWithLimit() throws Exception {
        Object b3 = constructor.newInstance(3, "Animal Farm", "George Orwell", 250.0, true);
        Method addMethod = daoInstance.getClass().getMethod("createBook", bookClass);
        addMethod.invoke(daoInstance, b3);

        Method deleteMethod = daoInstance.getClass().getMethod("deleteBooksByAuthor", String.class, int.class);
        deleteMethod.invoke(daoInstance, "George Orwell", 1);

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
    public void testGetAllBooksSortedByTitle() throws Exception {
        Method getSortedBooks = daoInstance.getClass().getMethod("getAllBooksByTitle");
        List<?> sortedList = (List<?>) getSortedBooks.invoke(daoInstance);

        Method getTitle = bookClass.getMethod("getTitle");
        String title1 = (String) getTitle.invoke(sortedList.get(0));
        String title2 = (String) getTitle.invoke(sortedList.get(1));

        assertTrue(title1.compareToIgnoreCase(title2) < 0);
    }

    @Test
    @Order(7)
    public void testGetAvailableBooks() throws Exception {
        Method getAvailableMethod = daoInstance.getClass().getMethod("getAvailableBooks");
        List<?> availableBooks = (List<?>) getAvailableMethod.invoke(daoInstance);

        for (Object book : availableBooks) {
            Method isAvailable = bookClass.getMethod("isAvailable");
            assertTrue((Boolean) isAvailable.invoke(book));
        }
    }
}
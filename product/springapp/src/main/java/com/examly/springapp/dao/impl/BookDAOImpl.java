package com.examly.springapp.dao.impl;

import java.sql.*;
import java.util.*;

import com.examly.springapp.config.JdbcUtils;
import com.examly.springapp.dao.BookDAO;
import com.examly.springapp.exception.LowPriceException;
import com.examly.springapp.model.Book;

public class BookDAOImpl implements BookDAO {

    @Override
    public void createBook(Book book) {
        try {
            if (book.getPrice() < 0) {
                throw new LowPriceException("Price cannot be negative");
            }

            try (Connection con = JdbcUtils.getConnection()) {
                String query = "INSERT INTO books (bookId, title, author, price, available) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ptmt = con.prepareStatement(query);
                ptmt.setInt(1, book.getBookId());
                ptmt.setString(2, book.getTitle());
                ptmt.setString(3, book.getAuthor());
                ptmt.setFloat(4, book.getPrice());
                ptmt.setBoolean(5, book.isAvailable());
                ptmt.executeUpdate();
            }
        } catch (SQLException | LowPriceException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateBook(Book book) {
        try {
            if (book.getPrice() < 0) {
                throw new LowPriceException("Price cannot be negative");
            }

            try (Connection con = JdbcUtils.getConnection()) {
                String query = "UPDATE books SET title=?, author=?, price=?, available=? WHERE bookId=?";
                PreparedStatement ptmt = con.prepareStatement(query);
                ptmt.setString(1, book.getTitle());
                ptmt.setString(2, book.getAuthor());
                ptmt.setFloat(3, book.getPrice());
                ptmt.setBoolean(4, book.isAvailable());
                ptmt.setInt(5, book.getBookId());
                ptmt.executeUpdate();
            }
        } catch (SQLException | LowPriceException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteBooksByAuthor(String author, int limit) {
        try (Connection con = JdbcUtils.getConnection()) {
            // Fetch books to delete
            String selectQuery = "SELECT * FROM books WHERE author = ? LIMIT ?";
            PreparedStatement selectStmt = con.prepareStatement(selectQuery);
            selectStmt.setString(1, author);
            selectStmt.setInt(2, limit);
            ResultSet rs = selectStmt.executeQuery();

            List<Integer> bookIdsToDelete = new ArrayList<>();
            while (rs.next()) {
                bookIdsToDelete.add(rs.getInt("bookId"));
            }

            if (!bookIdsToDelete.isEmpty()) {
                String inSql = String.join(",", Collections.nCopies(bookIdsToDelete.size(), "?"));
                String deleteQuery = "DELETE FROM books WHERE bookId IN (" + inSql + ")";
                PreparedStatement deleteStmt = con.prepareStatement(deleteQuery);
                for (int i = 0; i < bookIdsToDelete.size(); i++) {
                    deleteStmt.setInt(i + 1, bookIdsToDelete.get(i));
                }
                deleteStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Book> getAllBooksByTitle() {
        List<Book> books = new ArrayList<>();
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "SELECT * FROM books";
            PreparedStatement ptmt = con.prepareStatement(query);
            ResultSet rs = ptmt.executeQuery();

            while (rs.next()) {
                books.add(new Book(
                    rs.getInt("bookId"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getFloat("price"),
                    rs.getBoolean("available")
                ));
            }

            // Sort alphabetically by title (case-insensitive)
            books.sort(Comparator.comparing(b -> b.getTitle().toLowerCase()));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        try (Connection con = JdbcUtils.getConnection()) {
            String query = "SELECT * FROM books WHERE available = true";
            PreparedStatement ptmt = con.prepareStatement(query);
            ResultSet rs = ptmt.executeQuery();

            while (rs.next()) {
                availableBooks.add(new Book(
                    rs.getInt("bookId"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getFloat("price"),
                    true
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return availableBooks;
    }
}

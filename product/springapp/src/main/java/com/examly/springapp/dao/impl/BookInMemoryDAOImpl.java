package com.examly.springapp.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import com.examly.springapp.dao.BookInMemoryDAO;
import com.examly.springapp.model.Book;

public class BookInMemoryDAOImpl implements BookInMemoryDAO {

    private List<Book> bookDatabase = new ArrayList<>();

    // Adds a new Book to the in-memory list
    @Override
    public void createBook(Book book) {
        bookDatabase.add(book);
    }

    // Updates the book's information based on bookId
    @Override
    public void updateBook(Book updatedBook) {
        for (Book book : bookDatabase) {
            if (book.getBookId() == updatedBook.getBookId()) {
                book.setTitle(updatedBook.getTitle());
                book.setAuthor(updatedBook.getAuthor());
                try {
                    book.setPrice(updatedBook.getPrice());
                } catch (Exception e) {
                    System.out.println("Error updating price: " + e.getMessage());
                }
                book.setAvailable(updatedBook.isAvailable());
                break;
            }
        }
    }

    // Deletes books by author, up to a specified limit
    @Override
    public void deleteBooksByAuthor(String author, int limit) {
        List<Book> booksToRemove = new ArrayList<>();
        for (Book book : bookDatabase) {
            if (book.getAuthor().equalsIgnoreCase(author) && booksToRemove.size() < limit) {
                booksToRemove.add(book);
            }
        }
        bookDatabase.removeAll(booksToRemove);
    }

    // Retrieves all books sorted by title alphabetically
    @Override
    public List<Book> getAllBooksByTitle() {
        List<Book> sortedBooks = new ArrayList<>(bookDatabase);
        Collections.sort(sortedBooks, new Comparator<Book>() {
            @Override
            public int compare(Book b1, Book b2) {
                return b1.getTitle().compareToIgnoreCase(b2.getTitle());
            }
        });
        return sortedBooks;
    }

    @Override
    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : bookDatabase) {
            if (book.isAvailable()) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

}

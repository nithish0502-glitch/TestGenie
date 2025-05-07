package com.examly.springapp.service.impl;

import java.util.List;

import com.examly.springapp.dao.BookInMemoryDAO;
import com.examly.springapp.dao.impl.BookInMemoryDAOImpl;
import com.examly.springapp.model.Book;
import com.examly.springapp.service.BookInMemoryService;

public class BookInMemoryServiceImpl implements BookInMemoryService {

    private BookInMemoryDAO bookDAO;

    public BookInMemoryServiceImpl() {
        this.bookDAO = new BookInMemoryDAOImpl(); // using in-memory DAO
    }

    @Override
    public void createBook(Book book) {
        bookDAO.createBook(book);
    }

    @Override
    public void updateBook(Book book) {
        bookDAO.updateBook(book);
    }

    @Override
    public void deleteBooksByAuthor(String author, int limit) {
        bookDAO.deleteBooksByAuthor(author, limit);
    }

    @Override
    public List<Book> getAllBooksByTitle() {
        return bookDAO.getAllBooksByTitle();
    }

    @Override
    public List<Book> getAvailableBooks() {
        return bookDAO.getAvailableBooks();
    }
}

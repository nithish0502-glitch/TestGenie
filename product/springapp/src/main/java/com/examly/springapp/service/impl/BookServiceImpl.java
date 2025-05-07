package com.examly.springapp.service.impl;

import java.util.List;

import com.examly.springapp.dao.BookDAO;
import com.examly.springapp.dao.impl.BookDAOImpl;
import com.examly.springapp.exception.LowPriceException;
import com.examly.springapp.model.Book;
import com.examly.springapp.service.BookService;

public class BookServiceImpl implements BookService {

    private BookDAO bookDAO = new BookDAOImpl();

    @Override
    public void createBook(Book book) throws LowPriceException {
        bookDAO.createBook(book);
    }

    @Override
    public void updateBook(Book book) throws LowPriceException {
        bookDAO.updateBook(book);
    }

    @Override
    public void deleteBooksByAuthor(String author, int limit) {
        bookDAO.deleteBooksByAuthor(author, limit);
    }

    @Override
    public List<Book> getAllBooksByTitle() throws LowPriceException {
        return bookDAO.getAllBooksByTitle();
    }

    @Override
    public List<Book> getAvailableBooks() throws LowPriceException {
        return bookDAO.getAvailableBooks();
    }
}

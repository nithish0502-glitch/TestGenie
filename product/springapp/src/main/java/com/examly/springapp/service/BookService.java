package com.examly.springapp.service;

import java.util.List;
import com.examly.springapp.exception.LowPriceException;
import com.examly.springapp.model.Book;

public interface BookService {
    void createBook(Book book) throws LowPriceException;
    void updateBook(Book book) throws LowPriceException;
    void deleteBooksByAuthor(String author, int limit);
    List<Book> getAllBooksByTitle() throws LowPriceException;
    List<Book> getAvailableBooks() throws LowPriceException;
}

package com.examly.springapp.dao;

import java.util.List;
import com.examly.springapp.model.Book;

public interface BookDAO {
    void createBook(Book book);
    void updateBook(Book book);
    void deleteBooksByAuthor(String author, int limit);
    List<Book> getAllBooksByTitle();
    List<Book> getAvailableBooks();
}

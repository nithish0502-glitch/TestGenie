package com.examly.springapp.model;

import com.examly.springapp.exception.LowPriceException;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private float price;
    private boolean available;

    public Book() {}

    public Book(int bookId, String title, String author, float price, boolean available) throws LowPriceException {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        setPrice(price);  // validate price
        this.available = available;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) throws LowPriceException {
        if (price < 0) {
            throw new LowPriceException("Price cannot be negative");
        }
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Book [bookId=" + bookId + ", title=" + title + ", author=" + author + ", price=" + price
                + ", available=" + available + "]";
    }
}

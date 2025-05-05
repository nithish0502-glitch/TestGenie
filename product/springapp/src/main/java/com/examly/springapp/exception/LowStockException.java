package com.examly.springapp.exception;

public class LowStockException extends Exception {
    public LowStockException(String message) {
        super(message);
    }
}

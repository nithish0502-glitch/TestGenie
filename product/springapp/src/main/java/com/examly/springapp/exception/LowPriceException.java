package com.examly.springapp.exception;

public class LowPriceException extends Exception {
    public LowPriceException(String message) {
        super(message);
    }
}

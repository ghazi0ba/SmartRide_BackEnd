package com.example.smartridetrajetservice.exception;

public class TrajetNotFoundException extends RuntimeException {
    public TrajetNotFoundException(String message) {
        super(message);
    }
}

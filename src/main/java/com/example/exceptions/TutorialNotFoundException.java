package com.example.exceptions;

public class TutorialNotFoundException extends RuntimeException {
    public TutorialNotFoundException(String message) {
        super(message);
    }
    public TutorialNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

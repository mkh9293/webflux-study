package com.reactivespring.exception;

public class ReviewsServerException extends RuntimeException {
    private String message;

    public ReviewsServerException(String message) {
        this.message = message;
    }
}

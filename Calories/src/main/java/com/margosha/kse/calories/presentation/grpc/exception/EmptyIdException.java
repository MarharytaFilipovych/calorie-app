package com.margosha.kse.calories.presentation.grpc.exception;

public class EmptyIdException extends RuntimeException {
    public EmptyIdException() {
        super("Empty id was passed!");
    }
}

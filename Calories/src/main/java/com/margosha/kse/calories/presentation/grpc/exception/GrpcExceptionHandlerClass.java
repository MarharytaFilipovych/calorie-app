package com.margosha.kse.calories.presentation.grpc.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@GrpcAdvice
public class GrpcExceptionHandlerClass {

    @GrpcExceptionHandler(EmptyIdException.class)
    public StatusException handleEmptyId(EmptyIdException e) {
        return Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asException();
    }

    @GrpcExceptionHandler(EntityNotFoundException.class)
    public StatusException handleNotFound(EntityNotFoundException e) {
        return Status.NOT_FOUND
                .withDescription("Resource not found: " + e.getMessage())
                .asException();
    }

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public StatusException handleInvalidArgument(IllegalArgumentException e) {
        return Status.INVALID_ARGUMENT
                .withDescription("Invalid input: " + e.getMessage())
                .asException();
    }

    @GrpcExceptionHandler(ConstraintViolationException.class)
    public StatusException handleValidation(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return Status.INVALID_ARGUMENT
                .withDescription("Validation failed: " + message)
                .asException();
    }

    @GrpcExceptionHandler(AccessDeniedException.class)
    public StatusException handleAccessDenied(AccessDeniedException e) {
        return Status.PERMISSION_DENIED
                .withDescription("Access denied: " + e.getMessage())
                .asException();
    }

    @GrpcExceptionHandler(RuntimeException.class)
    public StatusException handleDefault(Exception e) {
        return Status.INTERNAL
                .withDescription("OOOOPPPPPSSSS")
                .withCause(e)
                .asException();
    }
}

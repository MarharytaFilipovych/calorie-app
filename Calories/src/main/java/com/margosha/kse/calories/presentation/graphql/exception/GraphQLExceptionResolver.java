package com.margosha.kse.calories.presentation.graphql.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@Component
public class GraphQLExceptionResolver extends DataFetcherExceptionResolverAdapter {
    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        if(ex instanceof ConstraintViolationException){
            return handleConstraintViolation((ConstraintViolationException)ex);
        }
        if (ex instanceof MethodArgumentNotValidException) {
            return handleMethodArgumentNotValid((MethodArgumentNotValidException) ex);
        }

        if (ex instanceof IllegalArgumentException) {
            return handleIllegalArgument((IllegalArgumentException) ex);
        }

        if (ex instanceof EntityNotFoundException) {
            return handleNotFoundException((EntityNotFoundException) ex);
        }

        if (ex instanceof AccessDeniedException) {
            return handleAccessDenied((AccessDeniedException) ex);
        }

        return null;
    }

    private GraphQLError handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return GraphqlErrorBuilder.newError()
                .message("Validation failed: " + message)
                .errorType(ErrorType.ValidationError)
                .build();
    }


    public GraphQLError handleMethodArgumentNotValid(MethodArgumentNotValidException e){
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("|\n"));
        return GraphqlErrorBuilder.newError()
                .message("Validation failed: " + message)
                .errorType(ErrorType.ValidationError)
                .build();
    }

    public GraphQLError handleNotFoundException(EntityNotFoundException e){
        String message = e.getMessage();
        if(message == null)message = "Not found!";
        else if(message.trim().split("\\s+").length == 1)message = "Entity with id " + e.getMessage() + " was not found!";
        return GraphqlErrorBuilder.newError()
                .message(message)
                .errorType(ErrorType.DataFetchingException)
                .build();
    }

    private GraphQLError handleIllegalArgument(IllegalArgumentException ex) {
        return GraphqlErrorBuilder.newError()
                .message("Invalid input: " + ex.getMessage())
                .errorType(ErrorType.ValidationError)
                .build();
    }

    private GraphQLError handleAccessDenied(AccessDeniedException ex) {
        return GraphqlErrorBuilder.newError()
                .message("Access denied: " + ex.getMessage())
                .errorType(ErrorType.ExecutionAborted)
                .build();
    }
}

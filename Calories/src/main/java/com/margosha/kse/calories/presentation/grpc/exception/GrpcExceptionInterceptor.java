package com.margosha.kse.calories.presentation.grpc.exception;

import io.grpc.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;
import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@Component
public class GrpcExceptionInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(
                next.startCall(new ExceptionHandlingServerCall<>(call), headers)) {
        };
    }

    private static class ExceptionHandlingServerCall<ReqT, RespT>
            extends ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT> {

        protected ExceptionHandlingServerCall(ServerCall<ReqT, RespT> delegate) {
            super(delegate);
        }

        @Override
        public void close(Status status, Metadata trailers) {
            if (status.isOk()) {
                super.close(status, trailers);
                return;
            }
            Throwable cause = status.getCause();
            Status newStatus = mapExceptionToStatus(cause);
            super.close(newStatus, trailers);
        }

        private Status mapExceptionToStatus(Throwable throwable) {
            if(throwable instanceof EmptyIdException e){
                return Status.INVALID_ARGUMENT
                        .withDescription(e.getMessage());
            }

            if (throwable instanceof EntityNotFoundException e) {
                return Status.NOT_FOUND
                        .withDescription("Resource not found: " + e.getMessage());
            }

            if (throwable instanceof IllegalArgumentException e) {
                return Status.INVALID_ARGUMENT
                        .withDescription("Invalid input: " + e.getMessage());
            }

            if (throwable instanceof ConstraintViolationException e) {
                return Status.INVALID_ARGUMENT
                        .withDescription("Validation failed: " + getValidationMessage(e));
            }

            if (throwable instanceof AccessDeniedException e) {
                return Status.PERMISSION_DENIED
                        .withDescription("Access denied: " + e.getMessage());
            }

            return Status.INTERNAL
                    .withDescription("Internal server error");
        }

        private String getValidationMessage(ConstraintViolationException e) {
            return e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        }
    }
}
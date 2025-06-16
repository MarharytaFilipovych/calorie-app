package com.margosha.kse.calories.presentation.aspects;

import com.margosha.kse.calories.business.dto.RecordResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Around("execution(* com.margosha.kse.calories.presentation.controller.*.*(..))")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return joinPoint.proceed();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        log.info("Request: {} {}", request.getMethod(), request.getRequestURI());
        if (!request.getParameterMap().isEmpty())
            log.debug("Request params: {}", request.getParameterMap().toString());

        Instant start = Instant.now();
        try {
            Object result = joinPoint.proceed();
            log.info("Execution time: {}", Duration.between(start, Instant.now()));
            if (response != null) log.info("Response: {}", response.getStatus());
            return result;
        } catch (Exception e) {
            log.warn("Request failed: {}", e.getMessage());
            throw e;
        }
    }

    @AfterThrowing(value = "execution(* com.margosha.kse.calories.business.service.*.*(..)) && !execution(* com.margosha.kse.calories.business.service.RecordOutboxService.*(..))", throwing = "exception")
    public void logServiceException(Exception exception) {
        if (!(exception instanceof ConversionFailedException)) {
            log.error("Service layer exception occurred: {} - {}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage());
        }
    }

    @AfterThrowing(value = "execution(* com.margosha.kse.calories.data.repository.*.*(..)) && !execution(* com.margosha.kse.calories.data.repository.RecordOutboxRepository.*(..))", throwing = "exception")
    public void logRepositoryException(Exception exception) {
        if (!(exception instanceof ConversionFailedException)) {
            log.error("Repository layer exception occurred: {} - {}",
                    exception.getClass().getSimpleName(),
                    exception.getMessage());
        }
    }

    @Around("execution(* com.margosha.kse.calories.business.service.RecordOutboxService.processRecord(..))")
    public Object logEventProduction(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        RecordResponseDto record = null;
        if (args.length > 0 && args[0] instanceof RecordResponseDto) {
            record = (RecordResponseDto) args[0];
        }
        UUID id = record != null ? record.getId() : null;
        Instant start = Instant.now();
        try {
            log.debug("Starting event production for record with ID {}", id);
            if (record != null && log.isTraceEnabled()) {
                log.trace("Record details: Meal: {}, Calories: {}, Products: {}",
                        record.getMealType(),
                        record.getCaloriesConsumed(),
                        record.getProducts().size());
            }
            Object result = joinPoint.proceed();
            log.debug("Successfully produced event for record with ID {} in {}ms",
                    id, Duration.between(start, Instant.now()).toMillis());
            return result;
        } catch (Exception e) {
            log.warn("Event production failed for record ID {} after {}ms. Error: {}",
                    id, Duration.between(start, Instant.now()).toMillis(), e.getMessage());
            throw e;
        }
    }

    @Around("execution(* com.margosha.kse.calories.business.service.RecordOutboxService.publish(..))")
    public Object logPublishProcess(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now();
        try {
            log.debug("Starting outbox publish batch");
            Object result = joinPoint.proceed();
            log.debug("Completed outbox publish batch in {}ms",
                    Duration.between(startTime, Instant.now()).toMillis());
            return result;
        } catch (ConversionFailedException e) {
            // Don't log conversion exceptions - they're expected during development
            log.debug("Outbox publish batch failed with conversion error after {}ms",
                    Duration.between(startTime, Instant.now()).toMillis());
            throw e;
        } catch (Exception e) {
            log.error("Outbox publish batch failed after {}ms: {}",
                    Duration.between(startTime, Instant.now()).toMillis(), e.getMessage());
            throw e;
        }
    }

    @Around("execution(* com.margosha.kse.calories.business.service.RecordOutboxService.processDeleteEvent(..))")
    public Object logDeletionEventProduction(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        UUID id = args.length > 0 && args[0] instanceof UUID ? (UUID) args[0] : null;
        Instant start = Instant.now();
        try {
            log.debug("Starting deletion event production for record with ID {}", id);
            Object result = joinPoint.proceed();
            log.debug("Successfully produced deletion event for record with ID {} in {}ms",
                    id, Duration.between(start, Instant.now()).toMillis());
            return result;
        } catch (Exception e) {
            log.warn("Deletion event production failed for record ID {} after {}ms. Error: {}",
                    id, Duration.between(start, Instant.now()).toMillis(), e.getMessage());
            throw e;
        }
    }
}
package com.margosha.kse.calories.presentation.aspects;

import com.margosha.kse.calories.business.dto.RecordResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Around("execution(* com.margosha.kse.calories.presentation.controller.*.*(..))")
    public Object logRequest(ProceedingJoinPoint joinPoint)throws Throwable{
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null)return joinPoint.proceed();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();

        log.info("Request: {} {}", request.getMethod(), request.getRequestURI());
        if(!request.getParameterMap().isEmpty())log.info("Request params: {}", request.getParameterMap().toString());
        Instant start = Instant.now();
        try{
            Object result =  joinPoint.proceed();
            log.info("Execution time: {}", Duration.between(start, Instant.now()));
            if(response != null)log.info("Response: {}", response.getStatus());
            return result;
        }catch (Exception e){
            log.error("Request failed: {}", e.getMessage());
            throw e;
        }
    }

    @AfterThrowing(value = "execution(* com.margosha.kse.calories.business.service.*.*(..)))", throwing = "exception")
    public void logServiceException(Exception exception){
        log.error("Service layer exception occurred: {} - {}",
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }

    @AfterThrowing(value = "execution(* com.margosha.kse.calories.data.repository.*.*(..))", throwing = "exception")
    public void logRepositoryException(Exception exception) {
        log.error("Repository layer exception occurred: {} - {}",
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }

    @Around("execution(com.margosha.kse.calories.business.service.RecordOutboxService.processRecord(*))")
    public Object logEventProduction(ProceedingJoinPoint joinPoint)throws Throwable{
        RecordResponseDto record = (RecordResponseDto) joinPoint.getArgs()[0];
        log.info("Starting to process record event for record ID: {}", record.getId());
        try {
            Object result = joinPoint.proceed();
            log.info("Successfully processed record event for record ID: {}", record.getId());
            return result;
        } catch (Exception e) {
            log.error("Failed to process record event for record ID: {}, error: {}",
                    record.getId(), e.getMessage(), e);
            throw e;
        }
    }
}

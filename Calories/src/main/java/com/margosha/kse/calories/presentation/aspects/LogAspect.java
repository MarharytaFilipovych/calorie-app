package com.margosha.kse.calories.presentation.aspects;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Before("execution(* com.margosha.kse.calories.presentation.controller.*.*(..))")
    public void logRequest(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null)return;
        HttpServletRequest request = attributes.getRequest();
        log.info("Request: {} {}", request.getMethod(), request.getRequestURI());
    }
}

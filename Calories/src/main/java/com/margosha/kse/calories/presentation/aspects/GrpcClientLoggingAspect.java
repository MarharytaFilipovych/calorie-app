package com.margosha.kse.calories.presentation.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class GrpcClientLoggingAspect {

    @Around("execution(* com.margosha.kse.calories.presentation.grpc.client.CaloriesGrpcClient.*(..))")
    public Object logGrpcClientOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("gRPC operation started: {} with args: {}", methodName, formatArgs(args));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            log.info("gRPC operation completed: {} in {}ms, result: {}",
                    methodName, 
                    stopWatch.getTotalTimeMillis(), 
                    formatResult(result));
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("gRPC operation failed: {} in {}ms, error: {}",
                    methodName, 
                    stopWatch.getTotalTimeMillis(), 
                    e.getMessage());
            throw e;
        }
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) return "none";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            Object arg = args[i];
            if (arg == null) sb.append("null");
            else if (arg instanceof String) sb.append("'").append(arg).append("'");
            else if (arg instanceof Number) sb.append(arg);
            else sb.append(arg.getClass().getSimpleName());
        }
        return sb.toString();
    }

    private String formatResult(Object result) {
        if (result == null) return "null";
        String className = result.getClass().getSimpleName();
        switch (className) {
            case "User" -> {
                try {
                    return "User[email=" + result.getClass().getMethod("getEmail").invoke(result) + "]";
                } catch (Exception e) {
                    return "User";
                }
            }
            case "Product" -> {
                try {
                    return "Product[name=" + result.getClass().getMethod("getName").invoke(result) + "]";
                } catch (Exception e) {
                    return "Product";
                }
            }
            case "Brand" -> {
                try {
                    return "Brand[name=" + result.getClass().getMethod("getName").invoke(result) + "]";
                } catch (Exception e) {
                    return "Brand";
                }
            }
            case "Record" -> {
                try {
                    return "Record[mealType=" + result.getClass().getMethod("getMealType").invoke(result) + "]";
                } catch (Exception e) {
                    return "Record";
                }
            }
        }
        return className;
    }
}
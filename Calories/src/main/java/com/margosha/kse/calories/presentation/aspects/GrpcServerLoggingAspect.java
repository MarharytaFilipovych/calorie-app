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
public class GrpcServerLoggingAspect {

    @Around("execution(* com.margosha.kse.calories.presentation.grpc.services.*.*(..))")
    public Object logGrpcServiceOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        if (methodName.toLowerCase().contains("stream")) return handleStreamingMethod(joinPoint, className, methodName, args);
        return handleRegularMethod(joinPoint, className, methodName, args);
    }

    private Object handleStreamingMethod(ProceedingJoinPoint joinPoint, String className, String methodName, Object[] args) throws Throwable {
        String streamingInfo = extractStreamingInfo(args);
        log.info("gRPC streaming started: {}.{}{}", className, methodName, streamingInfo);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            log.info("📡 gRPC streaming initiated: {}.{} in {}ms",
                    className, methodName, stopWatch.getTotalTimeMillis());
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("💥 gRPC streaming failed to start: {}.{} in {}ms, error: {} - {}",
                    className, methodName, stopWatch.getTotalTimeMillis(),
                    e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    private Object handleRegularMethod(ProceedingJoinPoint joinPoint, String className, String methodName, Object[] args) throws Throwable {
        log.info("📥 gRPC request: {}.{} with args: {}", className, methodName, formatArgs(args));
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            log.info("📤 gRPC response: {}.{} completed in {}ms, result: {}",
                    className, methodName, stopWatch.getTotalTimeMillis(), formatResult(result));
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("💥 gRPC error: {}.{} failed in {}ms, error: {} - {}",
                    className, methodName, stopWatch.getTotalTimeMillis(),
                    e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    private String extractStreamingInfo(Object[] args) {
        if (args == null || args.length == 0) return "";
        StringBuilder info = new StringBuilder(" with ");
        for (Object arg : args) {
            if (arg == null) continue;
            String className = arg.getClass().getSimpleName();
            if (className.equals("StreamProductsRequest")) {
                try {
                    String nameFilter = (String) arg.getClass().getMethod("getNameFilter").invoke(arg);
                    int batchSize = (Integer) arg.getClass().getMethod("getBatchSize").invoke(arg);
                    info.append(String.format("filter='%s', batch_size=%d",
                            nameFilter.isEmpty() ? "none" : nameFilter, batchSize));
                } catch (Exception e) {
                    info.append("StreamProductsRequest");
                }
                break;
            }
            if (className.contains("StreamRequest")) {
                info.append(className);
                break;
            }
        }

        return info.toString();
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) return "none";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            Object arg = args[i];
            if (arg == null) sb.append("null");
            else {
                String className = arg.getClass().getSimpleName();
                if (className.equals("IdRequest")) {
                    try {
                        String id = (String) arg.getClass().getMethod("getId").invoke(arg);
                        sb.append("IdRequest[id=").append(id).append("]");
                    } catch (Exception e) {
                        sb.append("IdRequest");
                    }
                } else if (className.contains("Request")) sb.append(className);
                else if (className.equals("StreamObserver")) sb.append("StreamObserver");
                else sb.append(className);
            }
        }
        return sb.toString();
    }

    private String formatResult(Object result) {
        if (result == null) return "void";
        String className = result.getClass().getSimpleName();
        try {
            switch (className) {
                case "User" -> {
                    String email = (String) result.getClass().getMethod("getEmail").invoke(result);
                    return "User[email=" + email + "]";
                }
                case "Product" -> {
                    String name = (String) result.getClass().getMethod("getName").invoke(result);
                    return "Product[name=" + name + "]";
                }
                case "Brand" -> {
                    String name = (String) result.getClass().getMethod("getName").invoke(result);
                    return "Brand[name=" + name + "]";
                }
                case "Record" -> {
                    Object mealType = result.getClass().getMethod("getMealType").invoke(result);
                    return "Record[mealType=" + mealType + "]";
                }
            }
        } catch (Exception e) {
            // Ignore reflection errors
        }
        return className;
    }

    public static void logStreamingProgress(String productName, int totalStreamed) {
        log.debug("📦 Streamed product: {} (total: {})", productName, totalStreamed);
    }

    public static void logStreamingCompleted(int totalStreamed) {
        log.info("✅ Product streaming completed. Total products streamed: {}", totalStreamed);
    }

    public static void logStreamingCancelled(int totalStreamed) {
        log.warn("⚠️ Client cancelled product streaming at product {}", totalStreamed);
    }

    public static void logStreamingError(Exception e) {
        log.error("❌ Error during product streaming", e);
    }

    public static void logStreamingInterrupted() {
        log.warn("⚠️ Product streaming interrupted");
    }
}
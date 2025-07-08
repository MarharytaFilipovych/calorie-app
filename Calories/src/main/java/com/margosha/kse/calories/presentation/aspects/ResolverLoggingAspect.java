package com.margosha.kse.calories.presentation.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class ResolverLoggingAspect {

    @Around("@annotation(org.springframework.graphql.data.method.annotation.QueryMapping) || " +
            "@annotation(org.springframework.graphql.data.method.annotation.MutationMapping) || " +
            "@annotation(org.springframework.graphql.data.method.annotation.SchemaMapping) || " +
            "@annotation(org.springframework.graphql.data.method.annotation.BatchMapping)")
    public Object logResolverExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        String resolverType = getResolverType(method);
        String operationName = getOperationName(method, resolverType);
        Object[] args = joinPoint.getArgs();
        String argsInfo = formatArguments(args, resolverType);

        log.info("=== {} {} {} CALLED{} ===", className, resolverType,
                operationName.toUpperCase(), argsInfo);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            Object result = joinPoint.proceed();
            stopWatch.stop();
            String resultInfo = formatResult(result, resolverType);
            log.info("=== {} {} COMPLETED{} in {}ms ===",
                    resolverType, operationName.toUpperCase(),
                    resultInfo, stopWatch.getTotalTimeMillis());
            return result;
        } catch (Exception e) {
            stopWatch.stop();
            log.error("=== {} {} FAILED in {}ms: {} ===",
                    resolverType, operationName.toUpperCase(),
                    stopWatch.getTotalTimeMillis(), e.getMessage());
            throw e;
        }
    }

    private String getResolverType(Method method) {
        if (method.isAnnotationPresent(QueryMapping.class)) return "QUERY";
        else if (method.isAnnotationPresent(MutationMapping.class)) return "MUTATION";
        else if (method.isAnnotationPresent(SchemaMapping.class)) return "SCHEMA";
        else if (method.isAnnotationPresent(BatchMapping.class)) return "BATCH";
        return "RESOLVER";
    }

    private String getOperationName(Method method, String resolverType) {
        if ("QUERY".equals(resolverType)) {
            QueryMapping annotation = method.getAnnotation(QueryMapping.class);
            return annotation.value().isEmpty() ? method.getName() : annotation.value();
        } else if ("MUTATION".equals(resolverType)) {
            MutationMapping annotation = method.getAnnotation(MutationMapping.class);
            return annotation.value().isEmpty() ? method.getName() : annotation.value();
        } else if ("SCHEMA".equals(resolverType)) {
            SchemaMapping annotation = method.getAnnotation(SchemaMapping.class);
            return annotation.field().isEmpty() ? method.getName() : annotation.field();
        } else if ("BATCH".equals(resolverType)) {
            BatchMapping annotation = method.getAnnotation(BatchMapping.class);
            String typeName = annotation.typeName().isEmpty() ? "Unknown" : annotation.typeName();
            String fieldName = annotation.field().isEmpty() ? method.getName() : annotation.field();
            return typeName + "." + fieldName;
        }
        return method.getName();
    }

    private String formatArguments(Object[] args, String resolverType) {
        if (args == null || args.length == 0) return "";

        StringBuilder sb = new StringBuilder(" with ");

        if ("BATCH".equals(resolverType) && args[0] instanceof Collection<?> batchItems) {
            sb.append("batch of ").append(batchItems.size()).append(" items");
            if (args.length > 1) {
                sb.append(" and ");
                for (int i = 1; i < args.length; i++) {
                    Object arg = args[i];
                    formatSingleArgument(sb, arg);
                    if (i < args.length - 1) sb.append(", ");
                }
            }
        } else {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                formatSingleArgument(sb, arg);
                if (i < args.length - 1) sb.append(", ");
            }
        }

        return sb.toString();
    }

    private void formatSingleArgument(StringBuilder sb, Object arg) {
        if (arg == null) sb.append("null");
        else if (arg instanceof String) sb.append("'").append(arg).append("'");
        else if (arg instanceof Collection) sb.append(((Collection<?>) arg).size()).append(" items");
        else sb.append(arg.getClass().getSimpleName());
    }

    private String formatResult(Object result, String resolverType) {
        if (result == null) return " -> null";
        if ("BATCH".equals(resolverType) && result instanceof Map<?, ?> batchResult) {
            return String.format(" -> Map[%d mappings]", batchResult.size());
        } else if (result instanceof Page<?> page) {
            return String.format(" -> Page[%d items, %d total]",
                    page.getContent().size(), page.getTotalElements());
        } else if (result instanceof Collection) {
            return String.format(" -> %d items", ((Collection<?>) result).size());
        } else if (result instanceof Boolean) return " -> " + result;
        else if (result instanceof Map) {
            return String.format(" -> Map[%d entries]", ((Map<?, ?>) result).size());
        } else return " -> " + result.getClass().getSimpleName();
    }
}
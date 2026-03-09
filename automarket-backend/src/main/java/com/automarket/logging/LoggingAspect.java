package com.automarket.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * AOP aspect that wraps all {@code @Service} methods to:
 * - Log method entry with sanitized arguments
 * - Log method exit with duration
 * - Log exceptions with full context before re-throwing
 *
 * Sensitive parameters annotated with {@code @Sensitive} are masked in logs.
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("within(@org.springframework.stereotype.Service *)")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        String args = buildArgsString(signature.getParameterNames(), joinPoint.getArgs(), method);

        log.debug("[{}#{}] called with args: [{}]", className, methodName, args);

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            log.debug("[{}#{}] completed in {}ms", className, methodName, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            log.error("[{}#{}] failed after {}ms with {}: {}",
                    className, methodName, duration,
                    e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    private String buildArgsString(String[] paramNames, Object[] args, Method method) {
        if (args == null || args.length == 0) return "";

        var parameters = method.getParameters();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");

            boolean sensitive = parameters[i].isAnnotationPresent(Sensitive.class);
            String paramName = (paramNames != null && i < paramNames.length) ? paramNames[i] : "arg" + i;

            if (sensitive) {
                sb.append(paramName).append("=[REDACTED]");
            } else {
                sb.append(paramName).append("=").append(sanitize(args[i]));
            }
        }
        return sb.toString();
    }

    private String sanitize(Object arg) {
        if (arg == null) return "null";
        // Truncate long strings to avoid flooding logs
        String str = arg.toString();
        return str.length() > 200 ? str.substring(0, 200) + "..." : str;
    }
}

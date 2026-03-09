package com.automarket.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a service method parameter as sensitive.
 * Parameters annotated with this will be logged as [REDACTED] by {@link LoggingAspect}.
 *
 * Usage:
 * <pre>
 *   public TokenPair login(String email, @Sensitive String password) { ... }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {
}

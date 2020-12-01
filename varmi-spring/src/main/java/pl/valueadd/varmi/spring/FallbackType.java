package pl.valueadd.varmi.spring;

import pl.valueadd.varmi.spring.annotation.VarmiFallback;

/**
 * Fallback types
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 */
public enum FallbackType {
    /**
     * No fallback strategy, timeout will be thrown.
     */
    NONE,
    /**
     * If method was already executed, fallback will return last known value. Otherwise timeout will be thrown.
     */
    LAST_KNOWN,
    /**
     * Looks up for an bean that implements same interface and is annotated with {@code VarmiFallback} and executes is.
     * @see VarmiFallback
     */
    BEAN
}

package pl.valueadd.varmi.spring.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * Annotation that contains configuration to initiate VarmiServer instance.
 *
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface VarmiServer {
    /**
     * Interface or Class that server candidate will be searched by.
     */
    Class<?> value();
    /**
     * Sever scoped interceptor classes, currently not used;
     */
    Class<?>[] interceptor() default {};
    /**
     * Server threads. Currently not supported
     */
    int threads() default 1;
}

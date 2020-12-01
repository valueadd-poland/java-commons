package pl.valueadd.varmi.spring.annotation;

import pl.valueadd.varmi.spring.FallbackType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static pl.valueadd.varmi.spring.FallbackType.NONE;

/**
 * Annotation that contains configuration to initiate VarmiClient instance.
 *
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface VarmiClient {

    /**
     * Interface on the basis of which the client instance will be created and then registered as a bean definition
     * NOTE: Classes are prohibited here.
     * NOTE2: Class given here has to have public access :(
     */
    Class<?> value();

    /**
     * Client scoped interceptor classes, currently not used
     */
    Class<?>[] interceptor() default {};

    /**
     * currently not used
     */
    int timeout() default 15;

    /**
     * currently not used
     */
    FallbackType fallbackType() default NONE;

}

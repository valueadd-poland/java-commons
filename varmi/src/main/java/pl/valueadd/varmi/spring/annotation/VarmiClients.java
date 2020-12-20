package pl.valueadd.varmi.spring.annotation;

import org.springframework.context.annotation.Import;
import pl.valueadd.varmi.spring.VarmiAutoConfiguration;
import pl.valueadd.varmi.spring.VarmiClientsRegistrar;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Enables
 * Configures component scanning directives for use with
 * {@link org.springframework.context.annotation.Configuration}
 * <code>@Configuration</code> classes.
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 * @see VarmiClient
 * @see pl.valueadd.varmi.spring.VarmiClientsRegistrar
 */
@Inherited
@Import({
        VarmiClientsRegistrar.class,
        VarmiAutoConfiguration.class
})
@Retention(RetentionPolicy.RUNTIME)
public @interface VarmiClients {
    /**
     *  Varmi clients declarations
     */
    VarmiClient[] value() default {};
    /**
     * Global interceptors
     * currently not used
     */
    Class<?>[] interceptor() default {};
}

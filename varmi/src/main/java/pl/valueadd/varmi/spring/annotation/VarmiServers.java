package pl.valueadd.varmi.spring.annotation;

import org.springframework.context.annotation.Import;
import pl.valueadd.varmi.spring.VarmiAutoConfiguration;
import pl.valueadd.varmi.spring.VarmiClientsRegistrar;
import pl.valueadd.varmi.spring.VarmiServerRegistrar;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Initiate all servers that are declared in value property.
 * Configures component scanning directives for use with
 * {@link org.springframework.context.annotation.Configuration}
 * <code>@Configuration</code> classes.
 *
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 * @see VarmiServer
 * @see pl.valueadd.varmi.spring.VarmiServerRegistrar
 */
@Inherited
@Import({
        VarmiServerRegistrar.class,
        VarmiAutoConfiguration.class
})
@Retention(RetentionPolicy.RUNTIME)
public @interface VarmiServers {
    /**
     *  Varmi server declarations
     */
    VarmiServer[] value() default {};

    /**
     * Global interceptors
     * currently not used
     */
    Class<?>[] interceptor() default {};
}

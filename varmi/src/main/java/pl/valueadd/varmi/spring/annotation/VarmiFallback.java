package pl.valueadd.varmi.spring.annotation;

import org.springframework.context.annotation.Import;
import pl.valueadd.varmi.spring.VarmiClientsRegistrar;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface VarmiFallback {
}

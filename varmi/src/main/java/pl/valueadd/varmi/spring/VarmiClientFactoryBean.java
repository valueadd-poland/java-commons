package pl.valueadd.varmi.spring;

import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 * @see VarmiClientsRegistrar
 * @see VarmiContext
 */
@Component
class VarmiClientFactoryBean implements FactoryBean<Object>, ApplicationContextAware {

    @Setter
    private Class<?> type;

    @Setter
    private ApplicationContext applicationContext;

    @Override
    public Object getObject() throws Exception {
        VarmiContext context = applicationContext.getBean(
                VarmiContext.class);
        return context.createClient(type);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }
}

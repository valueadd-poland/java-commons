package pl.valueadd.varmi.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import pl.valueadd.varmi.spring.annotation.VarmiClient;
import pl.valueadd.varmi.spring.annotation.VarmiClients;

import java.util.Map;

/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 * @see VarmiClients
 * @see VarmiClient
 * @see VarmiClientFactoryBean
 */
public class VarmiClientsRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        registerClients(metadata, registry);
    }

    private void registerClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {


        Map<String, Object> attrs = metadata.getAnnotationAttributes(VarmiClients.class.getName());
        if(attrs == null){
            return;
        }
        AnnotationAttributes[] clients = (AnnotationAttributes[]) attrs.get("value");
        for (AnnotationAttributes client : clients) {
            registerClient((Class)client.get("value"), registry);
        }
    }

    private void registerClient(Class clazz, BeanDefinitionRegistry registry) {
        var className = clazz.getSimpleName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(VarmiClientFactoryBean.class);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        definition.addPropertyValue("type", clazz.getName());

        AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
        beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);

        BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[] {});
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }
}


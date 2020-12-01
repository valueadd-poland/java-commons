package pl.valueadd.varmi.spring;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import pl.valueadd.varmi.spring.annotation.VarmiClient;
import pl.valueadd.varmi.spring.annotation.VarmiClients;
import pl.valueadd.varmi.spring.annotation.VarmiServer;
import pl.valueadd.varmi.spring.annotation.VarmiServers;

import java.util.Map;
/**
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 * @see VarmiServers
 * @see VarmiServer
 */
@Component
public class VarmiServerRegistrar implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        GenericApplicationContext context = (GenericApplicationContext)event.getApplicationContext();
        context.getBeansWithAnnotation(VarmiServers.class).forEach((key, clazz) ->{
            var meta = AnnotationMetadata.introspect(clazz.getClass());
            registerServers(context, meta);
        });

    }

    private void registerServers(GenericApplicationContext context, AnnotationMetadata metadata) {
        Map<String, Object> attrs = metadata.getAnnotationAttributes(VarmiServers.class.getName());
        if(attrs == null){
            return;
        }
        AnnotationAttributes[] clients = (AnnotationAttributes[]) attrs.get("value");
        for (AnnotationAttributes client : clients) {
            var clazz= (Class)client.get("value");
            context.getBean(VarmiContext.class).registerServer(context.getBean(clazz));
        }
    }
}


package pl.valueadd.varmi.spring;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.valueadd.varmi.VarmiConnection;
import pl.valueadd.varmi.VarmiSession;
import pl.valueadd.varmi.spring.transport.SpringAmqpRabbitMQVarmiTransport;
import pl.valueadd.varmi.transport.VarmiInterceptorDecoratorTransport;
import pl.valueadd.varmi.transport.interceptor.VarmiRequestInterceptor;
import pl.valueadd.varmi.transport.interceptor.VarmiResponseInterceptor;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class VarmiAutoConfiguration {

    @Bean
    public VarmiContext varmiContext(
            RabbitTemplate rabbitTemplate,
            List<VarmiRequestInterceptor> requestInterceptors,
            List<VarmiResponseInterceptor> responseInterceptors
    ) {
        return new VarmiContext(
                new VarmiConnection(
                        new VarmiSession(
                                new VarmiInterceptorDecoratorTransport(
                                        new SpringAmqpRabbitMQVarmiTransport(rabbitTemplate),
                                        requestInterceptors,
                                        responseInterceptors
                                )

                        )
                )
        );
    }
}

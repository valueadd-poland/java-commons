package pl.valueadd.varmi.spring;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.valueadd.varmi.VarmiConnection;
import pl.valueadd.varmi.VarmiSession;
import pl.valueadd.varmi.spring.transport.SpringAmqpRabbitMQVarmiTransport;

@Configuration(proxyBeanMethods = false)
class VarmiAutoConfiguration {

    @Bean
    public VarmiContext varmiContext(
            RabbitTemplate rabbitTemplate
    ) {
        return new VarmiContext(
                new VarmiConnection(
                        new VarmiSession(
                                new SpringAmqpRabbitMQVarmiTransport(rabbitTemplate)

                        )
                )
        );
    }
}

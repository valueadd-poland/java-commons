package pl.valueadd.varmi.spring.transport;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.utils.SerializationUtils;
import pl.valueadd.varmi.dto.VarmiRequest;
import pl.valueadd.varmi.dto.VarmiResponse;
import pl.valueadd.varmi.transport.VarmiTransport;

import java.util.Optional;

import static java.util.Optional.ofNullable;

/**
 * VarmiTransport implementation for RabbitMQ
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 */
public class SpringAmqpRabbitMQVarmiTransport implements VarmiTransport {

    private RabbitTemplate rabbitTemplate;

    private AmqpAdmin amqpAdmin;


    public SpringAmqpRabbitMQVarmiTransport(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = new RabbitAdmin(rabbitTemplate);
    }

    @Override
    public void receiveAndReply(String routing, Reply reply) {
        amqpAdmin.declareQueue(new Queue(routing, true));
        rabbitTemplate.receiveAndReply(
                routing,
                (ReceiveAndReplyMessageCallback) rawPayload -> map(reply.process(mapRequest(rawPayload)))
        );
    }

    @Override
    public Optional<VarmiResponse> sendAndReceive(String routing, VarmiRequest message) {
        return ofNullable(
                rabbitTemplate.sendAndReceive(routing, map(message))
        ).map(this::mapResponse);
    }

    private Message map(VarmiRequest message){
        return new Message(
                SerializationUtils.serialize(message),
                new MessageProperties()
        );
    }
    private Message map(VarmiResponse message){
        return new Message(
                SerializationUtils.serialize(message),
                new MessageProperties()
        );
    }
    private VarmiRequest mapRequest(Message message){
        return (VarmiRequest)SerializationUtils.deserialize(message.getBody());
    }
    private VarmiResponse mapResponse(Message message){
        return (VarmiResponse)SerializationUtils.deserialize(message.getBody());
    }
}

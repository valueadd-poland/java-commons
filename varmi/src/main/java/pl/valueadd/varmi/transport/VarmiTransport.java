package pl.valueadd.varmi.transport;

import pl.valueadd.varmi.dto.VarmiRequest;
import pl.valueadd.varmi.dto.VarmiResponse;

import java.lang.annotation.Documented;
import java.util.Optional;

/**
 * Abstraction layer for messaging, currently not really needed, in future we might want to add kafka support.
 * @author Jakub Trzcinski kuba@valueadd.pl
 * @version 1.0
 */
public interface VarmiTransport {
    /**
     * Callback interface for asynchronous procesing of VarmiRequest and returning VarmiResponse
     * @see VarmiRequest
     * @see VarmiResponse
     * @author Jakub Trzcinski kuba@valueadd.pl
     * @version 1.0
     */
    @FunctionalInterface
    interface Reply {
        VarmiResponse process(VarmiRequest message);
    }

    /**
     * Receive a message if there is one from provided queue, invoke provided
     * {@link Reply} and send reply message, if the {@code callback}
     *
     * @param routing Routing key, basicly queue name
     * @param reply a user-provided {@link Reply} implementation to
     * process received message and return a reply message.
     */
    void receiveAndReply(String routing, Reply reply);

    /**
     * Basic RPC pattern. Send a message to a default exchange with a specific routing key
     * and attempt to receive a response. Implementations will normally set the reply-to
     * header to an exclusive queue and wait up for some time limited by a timeout.
     *
     * @param routing Routing key, basicly queue name
     * @param message a message to send
     * @return the response if there is one
     */
    Optional<VarmiResponse> sendAndReceive(String routing, VarmiRequest message);
}

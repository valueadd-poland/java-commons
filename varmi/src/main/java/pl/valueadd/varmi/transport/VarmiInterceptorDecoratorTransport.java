package pl.valueadd.varmi.transport;

import lombok.RequiredArgsConstructor;
import pl.valueadd.varmi.dto.VarmiRequest;
import pl.valueadd.varmi.dto.VarmiResponse;
import pl.valueadd.varmi.transport.interceptor.VarmiRequestInterceptor;
import pl.valueadd.varmi.transport.interceptor.VarmiResponseInterceptor;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.EMPTY_LIST;

@RequiredArgsConstructor
public class VarmiInterceptorDecoratorTransport implements VarmiTransport {

    private final VarmiTransport toDecorte;

    private final List<VarmiRequestInterceptor> requestInterceptors;

    private final List<VarmiResponseInterceptor> responseInterceptors;

    public VarmiInterceptorDecoratorTransport(VarmiTransport toDecorte) {
        this.toDecorte = toDecorte;
        requestInterceptors = EMPTY_LIST;
        responseInterceptors = EMPTY_LIST;
    }

    public void receiveAndReply(String routing, Reply reply) {
        this.toDecorte.receiveAndReply(routing, message -> {
            for (VarmiRequestInterceptor interceptor : requestInterceptors) {
                message = interceptor.receive(message);
            }
            var ret = reply.process(message);
            for (VarmiResponseInterceptor interceptor : responseInterceptors) {
                ret = interceptor.send(ret);
            }
            return ret;
        });
    }

    public Optional<VarmiResponse> sendAndReceive(String routing, VarmiRequest message) {
        for (VarmiRequestInterceptor interceptor : requestInterceptors) {
            message = interceptor.send(message);
        }
        var response = this.toDecorte.sendAndReceive(routing, message);
        for (VarmiResponseInterceptor interceptor : responseInterceptors) {
            response = response.map(interceptor::recieve);
        }
        return response;
    }
}

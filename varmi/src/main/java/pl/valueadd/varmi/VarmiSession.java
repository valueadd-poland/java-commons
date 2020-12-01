package pl.valueadd.varmi;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pl.valueadd.varmi.dto.VarmiInvocation;
import pl.valueadd.varmi.dto.VarmiRequest;
import pl.valueadd.varmi.dto.VarmiResponse;
import pl.valueadd.varmi.exception.InvocationException;
import pl.valueadd.varmi.transport.VarmiTransport;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Slf4j
@RequiredArgsConstructor
public class VarmiSession implements Closeable {

    private final VarmiTransport transport;

    private List<Thread> listeners = new LinkedList<>();


    public void registerListener(Object object) {
        final String id = getId(object);
        final Thread thread = new Thread(() -> {
            while (true) {
                transport.receiveAndReply(id, rawPayload -> process(object, rawPayload));
            }
        });
        listeners.add(thread);
        thread.start();

    }

    @Override
    public void close()  {
        listeners.forEach(Thread::stop);
    }

    @SneakyThrows
    public Object handle(Object object, String name, Object[] args) {
        if (name.equals("hashCode")) {
            return System.identityHashCode(object);
        }
        final String id = getId(object);

        final var request = new VarmiRequest(
                new VarmiInvocation(name, args)
        );
        final var response = transport.sendAndReceive(id, request).orElseThrow(
                () -> new InvocationException(format("Could not fetch data from %s. But the connection is established. Listener did not respond in time.", id + "#" + name))
        );
        var ret = response.getResponse();

        if (getMethod(object, name, args).getReturnType().isAssignableFrom(Optional.class)) {
            ret = Optional.ofNullable(ret);
        }
        return ret;
    }

    private String getId(Object object) {
        return object.getClass().getGenericInterfaces()[0].getTypeName();
    }

    @SneakyThrows
    private VarmiResponse process(Object invokedOn, VarmiRequest message) {
        final var args = message.getInvocation().getArgs();
        final var methodName = message.getInvocation().getMethodName();
        Object content;
        try {
            content = getMethod(invokedOn, methodName, args)
                    .invoke(invokedOn, args);
        } catch (Exception e){
            content = e;
        }
        if (content instanceof Optional) {
            content = ((Optional<?>) content).orElse(null);
        }
        return new VarmiResponse(content);
    }

    private Method getMethod(Object object, String name, Object[] rawArgs) {
        if (rawArgs == null) rawArgs = new Object[]{};
        final var args = rawArgs;
        final Class[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        try {
            return Arrays.stream(object.getClass().getMethods())
                    .filter(e -> e.getParameterCount() == args.length)
                    .filter(e -> e.getName().equals(name))
                    .filter(e -> assertArgs(args, e.getParameters()))
                    .findFirst()
                    .orElseThrow(NoSuchMethodException::new);
        } catch (NoSuchMethodException e) {
            throw new InvocationException(String.format(
                    "Could not resolve target method to invoke, does the app is up-to-date? Method %s in %s",
                    name,
                    object.getClass()
            ));
        }
    }

    private boolean assertArgs(Object[] rawArgs, Parameter[] parameters) {
        for (int i = 0; i < rawArgs.length; i++) {
            final var arg = rawArgs[i];
            final var type = parameters[i].getType();

            if (!type.isAssignableFrom(arg.getClass())) {
                return false;
            }
        }

        return true;
    }
}

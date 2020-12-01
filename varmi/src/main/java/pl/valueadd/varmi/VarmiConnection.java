package pl.valueadd.varmi;

import lombok.RequiredArgsConstructor;
import pl.valueadd.varmi.exception.NotPublicException;

import java.io.Closeable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

@RequiredArgsConstructor
public class VarmiConnection implements Closeable {

    protected final VarmiSession session;

    public void registerServer(Object object) {
        session.registerListener(object);
    }

    public <T> T createClient(Class<T> clazz) {
        validateClient(clazz);
        InvocationHandler handler = new RrmiInvocationHandler();
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class[]{clazz},
                handler);
    }

    private void validateClient(Class clazz) {

        for (Method method : clazz.getMethods()) {
            if (!Modifier.isPublic(method.getReturnType().getModifiers())) {
                throw new NotPublicException(String.format("Return type of method %s in class %s is not public",
                        method.getName(),
                        clazz.getSimpleName()
                ));
            }
        }
    }

    public void close() {
        session.close();
    }

    private class RrmiInvocationHandler implements InvocationHandler {
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return session.handle(proxy, method.getName(), args);
        }
    }
}


package pl.valueadd.varmi.transport.interceptor;

import pl.valueadd.varmi.dto.VarmiResponse;

public interface VarmiResponseInterceptor {
    VarmiResponse send(VarmiResponse response);
    VarmiResponse recieve(VarmiResponse response);
}

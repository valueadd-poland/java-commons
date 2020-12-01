package pl.valueadd.varmi.transport.interceptor;

import pl.valueadd.varmi.dto.VarmiRequest;

public interface VarmiRequestInterceptor {
    VarmiRequest send(VarmiRequest response);
    VarmiRequest receive(VarmiRequest response);
}

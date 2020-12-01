package pl.valueadd.varmi.spring;

import lombok.experimental.Delegate;
import pl.valueadd.varmi.VarmiConnection;

class VarmiContext {

    @Delegate
    private VarmiConnection connection;

    public VarmiContext(VarmiConnection connection) {
        this.connection = connection;
    }
}

package pl.valueadd.varmi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VarmiRequest implements Serializable {
    private VarmiInvocation invocation;
    private Map<String, Object> map = new HashMap<>();

    public VarmiRequest(VarmiInvocation VarmiInvocation) {
        this.invocation = VarmiInvocation;
    }
}

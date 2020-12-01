package pl.valueadd.varmi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VarmiInvocation implements Serializable {
    private String methodName;
    private Object[] args;

    public Object[] getArgs() {
        if(args == null){
            return new Object[]{};
        }
        return args;
    }
}

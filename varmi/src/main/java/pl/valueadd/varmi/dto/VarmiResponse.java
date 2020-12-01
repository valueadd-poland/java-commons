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
public class VarmiResponse implements Serializable {
    private Object response;
    private Map<String, Object> map = new HashMap<>();

    public VarmiResponse(Object response) {
        this.response = response;
    }
}

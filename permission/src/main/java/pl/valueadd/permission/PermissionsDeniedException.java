package pl.valueadd.permission;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PermissionsDeniedException extends RuntimeException {
    public PermissionsDeniedException(String message) {
        super(message);
    }
}

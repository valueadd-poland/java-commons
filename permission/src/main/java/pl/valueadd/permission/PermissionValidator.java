package pl.valueadd.permission;


public class PermissionValidator {

    public boolean validate(Permission permission) {
        if (permission.isCan()) {
            return true;
        }
        permission.findMessage().ifPresent(msg -> {
            throw new PermissionsDeniedException(msg);
        });

        return false;
    }
}

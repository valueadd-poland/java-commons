package pl.valueadd.permission;

import java.io.Serializable;
import java.util.Optional;

public class Permission implements Serializable {
    public Permission(boolean can, String message) {
        this.can = can;
        this.message = message;
    }

    public Permission() {
    }

    public boolean isCan() {
        return this.can;
    }

    public String getMessage() {
        return this.message;
    }

    public interface Next {
        Permission get();
    }
    public interface Callback {
        void exec();
    }
    private static Permission ALLOWED = new Permission(true, "");
    private static Permission DISALLOWED = new Permission(false, "");

    private boolean can;

    private String message;

    public static Permission deny(String message) {
        return new Permission(false, message);
    }

    public static Permission deny() {
        return DISALLOWED;
    }

    public static Permission disallow() {
        return DISALLOWED;
    }

    public static Permission allow() {
        return ALLOWED;
    }

    public static Permission allow(String but) {
        return new Permission(true, but);
    }
    public static Permission ofBoolean(boolean can) {
        return can ? ALLOWED : DISALLOWED;
    }

    public Optional<String> findMessage() {
        return Optional.ofNullable(message);
    }

    public Permission or(Permission next) {
        if(this.can){
            return this;
        }
        if(!next.can) {
            return new Permission(false,  next.message);
        }
        return next;
    }

    public Permission or(Next lazy) {
        if(this.can){
            return this;
        }
        Permission next = lazy.get();
        return or(next);
    }

    public Permission and(Permission next) {
        if(this.can && next.can){
            return next;
        }
        return next;
    }

    public Permission and(Next lazy) {
        if(this.can){
            return this;
        }
        Permission next = lazy.get();
        return and(next);
    }

    public void ifTrue(Callback c) {
        if(this.can){
            c.exec();
        }
    }
}

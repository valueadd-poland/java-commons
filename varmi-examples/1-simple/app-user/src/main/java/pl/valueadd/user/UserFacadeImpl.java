package pl.valueadd.user;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class UserFacadeImpl implements UserFacade {
    @Override
    public List<User> findAll() {
        return new LinkedList<>(List.of(
                new User("John")
        ));
    }
}

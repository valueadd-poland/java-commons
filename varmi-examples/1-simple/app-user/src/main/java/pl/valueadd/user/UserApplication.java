package pl.valueadd.user;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import pl.valueadd.varmi.spring.annotation.VarmiServer;
import pl.valueadd.varmi.spring.annotation.VarmiServers;

@VarmiServers({
        @VarmiServer(UserFacade.class)
})
@SpringBootApplication(
        scanBasePackages = "pl.valueadd"
)
public class UserApplication implements CommandLineRunner {

    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder(UserApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception {
    }
}

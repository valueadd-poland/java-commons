package pl.valueadd.report;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import pl.valueadd.user.UserFacade;
import pl.valueadd.varmi.spring.annotation.VarmiClient;
import pl.valueadd.varmi.spring.annotation.VarmiClients;

@VarmiClients({
        @VarmiClient(UserFacade.class)
})
@SpringBootApplication(scanBasePackages = "pl.valueadd")
public class ReportingApplication implements CommandLineRunner {

    @Autowired
    private UserFacade userFacade;

    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder(ReportingApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        var users = userFacade.findAll();
        System.out.println(users);
    }
}


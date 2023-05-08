package gamein2.team;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@EnableEmailTools
public class BuildingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuildingApplication.class, args);
	}

}

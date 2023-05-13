package gamein2.team;

import it.ozimov.springboot.mail.configuration.EnableEmailTools;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages = {"gamein2.team"}, exclude = { SecurityAutoConfiguration.class })
@EnableWebMvc
@EnableEmailTools
@EnableScheduling
public class BuildingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuildingApplication.class, args);
	}

}

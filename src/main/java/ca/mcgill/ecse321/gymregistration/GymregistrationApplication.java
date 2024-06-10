package ca.mcgill.ecse321.gymregistration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class GymregistrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymregistrationApplication.class, args);
	}

	@RequestMapping("/")
	public String greeting() {
		return "Hello world!";
	}

}

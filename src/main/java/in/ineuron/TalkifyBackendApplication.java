package in.ineuron;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TalkifyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalkifyBackendApplication.class, args);
	}

}

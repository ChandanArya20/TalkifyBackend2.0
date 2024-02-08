package in.ineuron.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
public class BCryptEncoder {

	@Bean
	public BCryptPasswordEncoder getBCryptEncoder() {

		return new BCryptPasswordEncoder();
	}

}

package br.com.fiap.campus_gigs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class CampusGigsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CampusGigsApplication.class, args);
	}

}
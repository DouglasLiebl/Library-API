package io.github.douglasliebl.msbooks;

import io.github.douglasliebl.msbooks.api.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class MsBooksApplication {

	@Autowired
	private EmailService emailService;

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public CommandLineRunner runner() {
		return args -> {
			List<String> mail = List.of("liebldouglas@gmail.com");
			emailService.sendMail("LIBRARY API SUCCESSFULLY STARTED!", mail);
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(MsBooksApplication.class, args);
	}

}

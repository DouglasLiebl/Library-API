package io.github.douglasliebl.msbooks;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class MsBooksApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Scheduled(cron = "0 19 11 1/1 * ?")
	public void teste() {
		System.out.println("Teste");
	}

	public static void main(String[] args) {
		SpringApplication.run(MsBooksApplication.class, args);
	}

}

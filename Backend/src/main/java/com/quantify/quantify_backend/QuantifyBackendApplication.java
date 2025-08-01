package com.quantify.quantify_backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QuantifyBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuantifyBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner cmd(String [] args) {
		return (String[] args1) -> {
			// This is where you can add any startup logic if needed
			System.out.println("Quantify Backend Application has started successfully!");
		};
	}
}

package com.example.officeappbackend;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OfficeAppBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OfficeAppBackendApplication.class, args);
	}

	@Bean
	public ModelMapper getMapper() {
		return new ModelMapper();
	}
}

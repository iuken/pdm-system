package ru.aziattsev.pdm_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class PdmSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(PdmSystemApplication.class, args);
	}
}
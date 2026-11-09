package com.aivideochat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AiVideoChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiVideoChatApplication.class, args);
	}

}

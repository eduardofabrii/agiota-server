package com.agiota.bank;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgiotaBankApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AgiotaBankApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user1 = new User();
	}
}

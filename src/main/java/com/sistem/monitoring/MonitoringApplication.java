package com.sistem.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
public class MonitoringApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();

		System.setProperty("DB_URL", dotenv.get("DB_URL"));
		System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
		System.setProperty("ADMIN_USERNAME", dotenv.get("ADMIN_USERNAME"));
		System.setProperty("ADMIN_PASSWORD", dotenv.get("ADMIN_PASSWORD"));
		System.setProperty("ADMIN_EMAIL", dotenv.get("ADMIN_EMAIL"));
		System.setProperty("ADMIN_FULLNAME", dotenv.get("ADMIN_FULLNAME"));

		SpringApplication.run(MonitoringApplication.class, args);
	}

}

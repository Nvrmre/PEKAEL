package com.sistem.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableScheduling
public class MonitoringApplication {


	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()  
				.ignoreIfMalformed()
				.load();

		setPropFromEnvOrDotenv(dotenv, "DB_URL");
		setPropFromEnvOrDotenv(dotenv, "DB_USERNAME");
		setPropFromEnvOrDotenv(dotenv, "DB_PASSWORD");
		setPropFromEnvOrDotenv(dotenv, "ADMIN_USERNAME");
		setPropFromEnvOrDotenv(dotenv, "ADMIN_PASSWORD");
		setPropFromEnvOrDotenv(dotenv, "ADMIN_EMAIL");
		setPropFromEnvOrDotenv(dotenv, "ADMIN_FULLNAME");

		SpringApplication.run(MonitoringApplication.class, args);
	}

	private static void setPropFromEnvOrDotenv(Dotenv dotenv, String key) {

		String value = System.getenv(key);

		if ( (value == null || value.isEmpty()) && dotenv != null ) {
			value = dotenv.get(key);
		}

	
		if (value != null && !value.isEmpty()) {
			System.setProperty(key, value);
		}
	}
}

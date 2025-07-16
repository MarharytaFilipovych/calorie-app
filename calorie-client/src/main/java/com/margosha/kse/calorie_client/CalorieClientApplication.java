package com.margosha.kse.calorie_client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CalorieClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(CalorieClientApplication.class, args);
	}

}

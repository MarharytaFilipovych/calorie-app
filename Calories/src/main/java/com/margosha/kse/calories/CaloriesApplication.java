package com.margosha.kse.calories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.margosha.kse.calories.data.repository")
@EntityScan("com.margosha.kse.calories.data.entity")
@EnableJpaAuditing
public class CaloriesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaloriesApplication.class, args);
	}

}

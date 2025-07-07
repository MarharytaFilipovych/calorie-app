package com.margosha.kse.CaloriesConsumer;

import com.margosha.kse.CaloriesConsumer.client.GraphQLSubscriptionClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class CaloriesConsumerApplication implements CommandLineRunner {

	private final GraphQLSubscriptionClient graphQLSubscriptionClient;

	public CaloriesConsumerApplication(GraphQLSubscriptionClient graphQLSubscriptionClient) {
		this.graphQLSubscriptionClient = graphQLSubscriptionClient;
	}

	@Override
	public void run(String... args) {
		graphQLSubscriptionClient.recordEvents()
				.doOnNext(r -> log.info("Received event: {}", r))
				.doOnError(e -> log.error("Error occurred ... {}", e.getMessage()))
				.subscribe();
	}

	public static void main(String[] args) {
		SpringApplication.run(CaloriesConsumerApplication.class, args);
	}
}


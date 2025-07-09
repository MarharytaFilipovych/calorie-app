package com.margosha.kse.calories.user_graphql_subgraph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.margosha.kse.calories.user_graphql_subgraph.data.repository")
@EnableJpaAuditing
public class UserGraphqlSubgraphApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserGraphqlSubgraphApplication.class, args);
	}

}

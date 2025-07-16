package com.margosha.kse.calorie_client.clients;

import com.margosha.kse.calorie_client.dto.User;
import com.margosha.kse.calorie_client.model.IdResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
@Slf4j
public class UserClient {
    @Value("${client.user}")
    private String resource;
    private final WebClient webClient;

    public UserClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<UUID> createUser(@NotNull User user){
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(resource).build())
                .body(Mono.just(user), User.class)
                .retrieve()
                .bodyToMono(IdResponse.class)
                .map(IdResponse::getId)
                .doOnNext(id -> log.info("🌷Received user ID: {}", id));
    }

    public Mono<User> getUser(@NotNull UUID id){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(resource).pathSegment(id.toString()).build())
                .retrieve()
                .bodyToMono(User.class)
                .doOnNext(user -> log.info("🌷Received user: {}", user));
    }
}

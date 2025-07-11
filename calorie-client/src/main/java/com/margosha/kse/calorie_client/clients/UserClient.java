package com.margosha.kse.calorie_client.clients;

import com.margosha.kse.calorie_client.dto.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Component
public class UserClient {
    private final String resource = "/users";
    private final WebClient webClient;

    public UserClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<UUID> createUser(@NotNull User user){
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(resource).build())
                .body(Mono.just(user), User.class)
                .retrieve()
                .bodyToMono(UUID.class);
    }

    public Mono<User> getUser(@NotNull UUID id){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(resource).pathSegment(id.toString()).build())
                .retrieve()
                .bodyToMono(User.class);
    }
}

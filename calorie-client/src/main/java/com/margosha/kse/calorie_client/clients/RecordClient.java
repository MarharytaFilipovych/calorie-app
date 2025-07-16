package com.margosha.kse.calorie_client.clients;

import com.margosha.kse.calorie_client.dto.PaginatedResponse;
import com.margosha.kse.calorie_client.dto.Pagination;
import com.margosha.kse.calorie_client.dto.RecordRequest;
import com.margosha.kse.calorie_client.dto.RecordResponse;
import com.margosha.kse.calorie_client.model.IdResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class RecordClient {
    private final String resource = "/users/{userId}/records";
    private final WebClient webClient;

    public RecordClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<PaginatedResponse<RecordResponse>> getRecords(@NotNull Pagination pagination, @NotNull UUID userId, LocalDate date) {
        return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path(resource)
                            .queryParam("limit", pagination.getLimit())
                            .queryParam("offset", pagination.getOffset());
                    if (date != null) {
                        builder.queryParam("date", date.toString());
                    }
                    return builder.build(userId);
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }

    public Mono<PaginatedResponse<RecordResponse>> getRecords(@NotNull Pagination pagination, @NotNull UUID userId) {
        return getRecords(pagination, userId, null);
    }

    public Mono<RecordResponse> getRecord(@NotNull UUID userId, @NotNull UUID id){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(resource + "/{recordId}")
                        .build(userId, id))
                .retrieve()
                .bodyToMono(RecordResponse.class);
    }

    public Mono<Void> deleteRecord(@NotNull UUID userId, @NotNull UUID id){
        return webClient.delete()
                .uri(uriBuilder -> uriBuilder.path(resource + "/{recordId}")
                        .build(userId, id))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Void> updateRecord(@NotNull UUID userId, @NotNull UUID id, @NotNull RecordRequest recordRequest){
        return webClient.put()
                .uri(uriBuilder -> uriBuilder.path(resource + "/{recordId}")
                        .build(userId, id))
                .body(Mono.just(recordRequest), RecordRequest.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<UUID> createRecord(@NotNull UUID userId, @NotNull RecordRequest recordRequest){
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(resource).build(userId))
                .body(Mono.just(recordRequest), RecordRequest.class)
                .retrieve()
                .bodyToMono(IdResponse.class)
                .map(IdResponse::getId);
    }
}

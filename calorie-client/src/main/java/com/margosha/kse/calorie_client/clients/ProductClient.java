package com.margosha.kse.calorie_client.clients;

import com.margosha.kse.calorie_client.dto.PaginatedResponse;
import com.margosha.kse.calorie_client.dto.Pagination;
import com.margosha.kse.calorie_client.dto.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

@Component
public class ProductClient {
    private final String resource = "/products";
    private final WebClient webClient;

    public ProductClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<PaginatedResponse<Product>> getProducts(@NotNull Pagination pagination, String name){
        return webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder
                            .path(resource)
                            .queryParam("limit", pagination.getLimit())
                            .queryParam("offset", pagination.getOffset());
                    if(name != null && !name.isBlank())builder.queryParam("name", name);
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {
                });
    }

    public Mono<PaginatedResponse<Product>> getProducts(@NotNull Pagination pagination){
        return getProducts(pagination, null);
    }
}

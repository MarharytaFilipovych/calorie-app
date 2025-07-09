package com.margosha.kse.calories.subscription_graphql_client.client;

import com.margosha.kse.calories.subscription_graphql_client.dto.RecordEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;
import java.time.Duration;

@Slf4j
@Service
public class SubscriptionClient {
    private WebSocketGraphQlClient client;

    public SubscriptionClient(@Value("${graphql.sub.url}") String url) {
        log.info("Initializing GraphQL WebSocket client with URL: {}", url);
        this.client = WebSocketGraphQlClient.builder(url, new ReactorNettyWebSocketClient()).build();
    }

    public Flux<RecordEventDto> recordEvents(){
        final String subscription = """
            subscription { 
                recordEvents { 
                    id 
                    eventType 
                    when 
                    record { 
                        id 
                        mealType 
                        consumedAt 
                        caloriesConsumed 
                        totalQuantity 
                        totalProteins 
                        totalFats 
                        totalCarbohydrates 
                        products { 
                            quantity 
                            product { 
                                id 
                                name 
                                barcode 
                                proteins 
                                fats 
                                carbohydrates 
                                water 
                                salt 
                                sugar 
                                fiber 
                                alcohol 
                                description 
                                calories 
                                measurementUnit 
                                archived 
                                brand { 
                                    id 
                                    name 
                                    description 
                                } 
                            } 
                        } 
                    } 
                } 
            }""";
        return client.document(subscription)
                .retrieveSubscription("recordEvents")
                .toEntity(RecordEventDto.class)
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(5))
                        .doBeforeRetry(retrySignal ->
                                log.warn("Retrying GraphQL subscription connection, attempt: {}",
                                        retrySignal.totalRetries() + 1)))
                .doOnError(error -> log.error("Failed to establish GraphQL subscription", error));
    }
}



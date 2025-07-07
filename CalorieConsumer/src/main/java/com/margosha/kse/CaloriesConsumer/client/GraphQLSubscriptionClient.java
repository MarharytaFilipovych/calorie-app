package com.margosha.kse.CaloriesConsumer.client;

import com.margosha.kse.CaloriesConsumer.dto.RecordEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class GraphQLSubscriptionClient {
    private WebSocketGraphQlClient client;

    @Value("${graphql.sub.url}")
    private String url;

    public GraphQLSubscriptionClient(){
        this.client = WebSocketGraphQlClient.builder(url, new ReactorNettyWebSocketClient()).build();
    }

    public Flux<RecordEventDto> recordEvents(){
        String subscription = "subscription { recordEvents { id eventType when entityData { id mealType caloriesConsumed totalQuantity } } }";
        return client.document(subscription)
                .retrieveSubscription("recordEvents")
                .toEntity(RecordEventDto.class);
    }
}



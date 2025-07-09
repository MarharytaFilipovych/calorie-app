package com.margosha.kse.calories.subscription_graphql_client.service;

import com.margosha.kse.calories.subscription_graphql_client.client.SubscriptionClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SubscriptionService {

    private final SubscriptionClient graphQLSubscriptionClient;

    public SubscriptionService(SubscriptionClient graphQLSubscriptionClient) {
        this.graphQLSubscriptionClient = graphQLSubscriptionClient;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application is ready, starting GraphQL subscription...");
        startSubscription();
    }

    private void startSubscription() {
        graphQLSubscriptionClient.recordEvents()
            .doOnSubscribe(subscription -> log.info("📡 GraphQL subscription started successfully!"))
            .doOnNext(r -> log.info("Received subscribed graphQL event😘😘😘😘😘😘😘🌷🌷🌷: {}", r))
            .doOnError(e -> log.error("Error occurred👻 ... {}", e.getMessage()))
            .subscribe();
    }
}
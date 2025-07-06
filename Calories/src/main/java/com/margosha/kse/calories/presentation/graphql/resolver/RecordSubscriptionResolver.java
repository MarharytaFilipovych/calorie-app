package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.business.dto.RecordEventDto;
import com.margosha.kse.calories.business.service.GraphQLEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Component;
import org.reactivestreams.Publisher;

@Component
@Slf4j
public class RecordSubscriptionResolver {
    private final GraphQLEventPublisher eventPublisher;

    public RecordSubscriptionResolver(GraphQLEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @SubscriptionMapping
    public Publisher<RecordEventDto> recordEvents(){
        log.info("New client subscribed to record events");
        return eventPublisher.getEventStream()
                .doOnSubscribe(subscription -> log.info("Client subscribed to recordEvents"))
                .doOnCancel(() -> log.info("Client unsubscribed from recordEvents"))
                .doOnError(error -> log.error("Error in recordEvents subscription", error));
    }
}

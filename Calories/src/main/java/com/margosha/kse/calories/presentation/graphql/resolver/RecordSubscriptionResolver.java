package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.business.dto.RecordEventDto;
import com.margosha.kse.calories.business.service.GraphQLEventPublisherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
@Slf4j
public class RecordSubscriptionResolver {
    private final GraphQLEventPublisherService eventPublisher;

    public RecordSubscriptionResolver(GraphQLEventPublisherService eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @SubscriptionMapping
    public Flux<RecordEventDto> recordEvents(){
        log.info("New client subscribed to record events");
        return eventPublisher.getEventStream()
                .doOnSubscribe(subscription -> log.info("Client subscribed to recordEvents"))
                .doOnCancel(() -> log.info("Client unsubscribed from recordEvents"))
                .doOnError(error -> log.error("Error in recordEvents subscription", error));
    }
}

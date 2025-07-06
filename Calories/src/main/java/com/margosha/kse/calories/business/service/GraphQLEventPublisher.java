package com.margosha.kse.calories.business.service;

import com.margosha.kse.calories.business.dto.RecordEventDto;
import com.margosha.kse.calories.presentation.enums.EventType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@Slf4j
public class GraphQLEventPublisher {

    private final Sinks.Many<RecordEventDto> eventSink;
    @Getter
    private final Flux<RecordEventDto> eventStream;

    public GraphQLEventPublisher(){
        this.eventSink = Sinks.many().multicast().onBackpressureBuffer();
        this.eventStream = eventSink.asFlux().share();
    }

    public void publishEvent(RecordEventDto event){
        log.debug("Publishing GraphQL subscription event: {}", event.getId());
        Sinks.EmitResult result = eventSink.tryEmitNext(event);
        if(result.isFailure()){
            log.warn("Failed to publish event: {} - {}", event.getId(), result);
        }
    }
}
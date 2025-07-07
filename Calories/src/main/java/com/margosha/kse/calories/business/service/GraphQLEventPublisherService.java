package com.margosha.kse.calories.business.service;

import com.margosha.kse.calories.business.dto.RecordEventDto;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
@Slf4j
public class GraphQLEventPublisherService {

    private final Sinks.Many<RecordEventDto> eventSink = Sinks.many().multicast().onBackpressureBuffer();;
    @Getter
    private final Flux<RecordEventDto> eventStream = eventSink.asFlux().cache(0);

    public void publishEvent(RecordEventDto event){
        log.debug("Publishing GraphQL subscription event: {}", event.getId());
        Sinks.EmitResult result = this.eventSink.tryEmitNext(event);
        if(result.isFailure()){
            log.warn("Failed to publish event: {} - {}", event.getId(), result);
        }
    }
}
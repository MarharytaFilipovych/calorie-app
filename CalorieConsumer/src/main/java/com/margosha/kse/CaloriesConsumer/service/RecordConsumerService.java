package com.margosha.kse.CaloriesConsumer.service;

import com.margosha.kse.CaloriesConsumer.ConsumptionException;
import com.margosha.kse.CaloriesConsumer.dto.RecordEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
public class RecordConsumerService {

    @RabbitListener(queues = "${queue-name}")
    public void consumeRecordEvent(RecordEventDto event){
        log.info("Consuming the following record event: {} at {}", event.toString(), LocalDateTime.now());
        if (new Random().nextInt(5) > 3) throw new ConsumptionException(event);
        log.info("😘Successfully eaten {} at {}", event, LocalDateTime.now());
    }

    @RabbitListener(queues = "${queue-name}.dlq")
    public void processFailedMessage(RecordEventDto event){
        log.warn("👻We have received a failed event {} at {}", event, LocalDateTime.now());
    }

}

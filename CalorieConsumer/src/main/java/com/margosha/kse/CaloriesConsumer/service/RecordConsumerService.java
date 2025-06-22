package com.margosha.kse.CaloriesConsumer.service;

import com.margosha.kse.CaloriesConsumer.dto.RecordEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@Slf4j
public class RecordConsumerService {

    @RabbitListener(queues = "#{rabbitSettings.queueName}")
    public void consumeRecordEvent(RecordEventDto event){
        if (new Random().nextInt(5) > 3) {
            log.error("FAILED to process {}", event);
            throw new AmqpRejectAndDontRequeueException("Processing failed for event: " + event.getId());
        }
        log.info("SUCCESSFULLY eaten {}", event);
    }

    @RabbitListener(queues = "#{rabbitSettings.queueName}.dlq")
    public void processFailedMessage(RecordEventDto event){
        log.warn("Failed event comes to DLQ {}", event);
    }
}
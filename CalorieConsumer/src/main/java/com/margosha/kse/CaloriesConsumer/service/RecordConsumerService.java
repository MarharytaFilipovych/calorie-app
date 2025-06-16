package com.margosha.kse.CaloriesConsumer.service;

import com.margosha.kse.CaloriesConsumer.ConsumptionException;
import com.margosha.kse.CaloriesConsumer.dto.RecordEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@Slf4j
public class RecordConsumerService {

    private static final String BLUE = "\u001B[34m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    @RabbitListener(queues = "#{rabbitSettings.queueName}")
    public void consumeRecordEvent(RecordEventDto event){
        if (new Random().nextInt(5) > 3) {
            log.error(RED + "💥Failed to process {}" + RESET, event);
            throw new AmqpRejectAndDontRequeueException("Processing failed for event: " + event.getId());
        }
        log.info(BLUE + "😘Successfully eaten {}" + RESET, event);
    }

    @RabbitListener(queues = "#{rabbitSettings.queueName}.dlq")
    public void processFailedMessage(RecordEventDto event){
        log.warn(MAGENTA + "👻Failed event {}" + RESET, event);
    }
}
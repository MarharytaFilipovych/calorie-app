package com.margosha.kse.CaloriesConsumer.service;

import com.margosha.kse.CaloriesConsumer.config.ColorsSettings;
import com.margosha.kse.CaloriesConsumer.dto.RecordEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@Slf4j
public class RecordConsumerService {
    private final String successTemplate;
    private final String failureTemplate;
    private final String dlqTemplate;

    public RecordConsumerService(ColorsSettings colorsSettings) {
        this.successTemplate = colorsSettings.getBlue() + "😘Successfully eaten {}" + colorsSettings.getReset();
        this.failureTemplate = colorsSettings.getRed() + "💥Failed to process {}" + colorsSettings.getReset();
        this.dlqTemplate = colorsSettings.getMagenta() + "👻Failed event {}" + colorsSettings.getReset();
    }

    @RabbitListener(queues = "#{rabbitSettings.queueName}")
    public void consumeRecordEvent(RecordEventDto event){
        if (new Random().nextInt(5) > 3) {
            log.error(failureTemplate, event);
            throw new AmqpRejectAndDontRequeueException("Processing failed for event: " + event.getId());
        }
        log.info(successTemplate, event);
    }

    @RabbitListener(queues = "#{rabbitSettings.queueName}.dlq")
    public void processFailedMessage(RecordEventDto event){
        log.warn(dlqTemplate, event);
    }
}
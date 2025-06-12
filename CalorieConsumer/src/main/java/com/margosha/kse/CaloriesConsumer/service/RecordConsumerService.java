package com.margosha.kse.CaloriesConsumer.service;

import com.margosha.kse.CaloriesConsumer.dto.RecordEventDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RecordConsumerService {

    @RabbitListener(queues = "${queue-name}")
    public void consumeRecordEvent(RecordEventDto event){

    }

}

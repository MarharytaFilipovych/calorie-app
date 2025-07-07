package com.margosha.kse.calories.business.service;

import com.margosha.kse.calories.business.dto.RecordEventDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.data.repository.RecordOutboxRepository;
import com.margosha.kse.calories.presentation.enums.EventType;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@EnableAsync
@Service
@Slf4j
public class RecordOutboxService {

    private final RecordOutboxRepository recordOutboxRepository;
    private final RecordService recordService;
    private final RabbitTemplate rabbitTemplate;
    private final GraphQLEventPublisherService graphQLEventPublisher;

    @Value("${batch-size}")
    private int batchSize;

    public RecordOutboxService(RecordOutboxRepository recordOutboxRepository, RecordService recordService, RabbitTemplate rabbitTemplate, GraphQLEventPublisherService graphQLEventPublisher) {
        this.recordOutboxRepository = recordOutboxRepository;
        this.recordService = recordService;
        this.rabbitTemplate = rabbitTemplate;
        this.graphQLEventPublisher = graphQLEventPublisher;
    }

    @Scheduled(fixedRateString = "${rate-time}")
    @Transactional
    public void publish(){
        List<UUID> recordIds = recordOutboxRepository.findDistinctRecordsIds(PageRequest.of(0, batchSize));
        if(recordIds.isEmpty()){
            log.info("The batch is empty at {}", LocalDateTime.now());
            return;
        }
        List<RecordResponseDto> existingRecords = recordService.getAllRecordsWithProducts(recordIds);
        Set<UUID> existingRecordsIds = existingRecords.stream().map(RecordResponseDto::getId).collect(Collectors.toSet());
        existingRecords.forEach(this::processRecord);
        recordIds.stream().filter(id -> !existingRecordsIds.contains(id)).forEach(this::processDeleteEvent);
    }

    @Transactional
    public void processDeleteEvent(UUID id){
        RecordEventDto event = new RecordEventDto(null, id,
                EventType.DELETED, LocalDateTime.now(ZoneOffset.UTC));
        rabbitTemplate.convertAndSend(event);
        graphQLEventPublisher.publishEvent(event);
        recordOutboxRepository.deleteAllByRecordId(id);
    }

    @Transactional
    public void processRecord(RecordResponseDto record){
        RecordEventDto event = new RecordEventDto(record, record.getId(),
                EventType.CREATED_UPDATED, LocalDateTime.now(ZoneOffset.UTC));
        rabbitTemplate.convertAndSend(event);
        graphQLEventPublisher.publishEvent(event);
        recordOutboxRepository.deleteAllByRecordId(record.getId());
    }
}

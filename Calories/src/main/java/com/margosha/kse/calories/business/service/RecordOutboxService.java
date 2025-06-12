package com.margosha.kse.calories.business.service;

import com.margosha.kse.calories.business.dto.RecordEventDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.data.repository.RecordOutboxRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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
public class RecordOutboxService {

    private final RecordOutboxRepository recordOutboxRepository;
    private final RecordService recordService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${batch-size}")
    private int batchSize;

    public RecordOutboxService(RecordOutboxRepository recordOutboxRepository, RecordService recordService, RabbitTemplate rabbitTemplate) {
        this.recordOutboxRepository = recordOutboxRepository;
        this.recordService = recordService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedRateString = "${rate-time}")
    public void publish(){
        List<UUID> recordIds = recordOutboxRepository.findDistinctRecordsIds(batchSize);
        List<RecordResponseDto> existingRecords = recordService.getAllRecordsWithProducts(recordIds);
        Set<UUID> existingRecordsIds = existingRecords.stream().map(RecordResponseDto::getId).collect(Collectors.toSet());
        Set<UUID> notfoundRecordIds = recordIds.stream().filter(id -> !existingRecordsIds.contains(id)).collect(Collectors.toSet());
        existingRecords.forEach(this::processRecord);
        recordOutboxRepository.deleteAllByRecordIdIn(notfoundRecordIds);
    }

    @Transactional
    public void processRecord(RecordResponseDto record){
        RecordEventDto event = new RecordEventDto(record, LocalDateTime.now(ZoneOffset.UTC));
        rabbitTemplate.convertAndSend(event);
        recordOutboxRepository.deleteByRecordId(record.getId());
    }
}

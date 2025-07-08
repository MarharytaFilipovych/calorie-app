package com.margosha.kse.calories.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.presentation.enums.EventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordEventDto {
    @JsonProperty("record")
    private RecordResponseDto entityData;

    private UUID id;

    @JsonProperty("event_type")
    private EventType eventType;

    @JsonProperty("when")
    private LocalDateTime when;
}

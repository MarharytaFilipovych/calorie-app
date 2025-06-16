package com.margosha.kse.CaloriesConsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.CaloriesConsumer.annotations.CorrectEnum;
import com.margosha.kse.CaloriesConsumer.enums.EventType;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    private UUID id;

    @JsonProperty("event_type")
    @NotNull
    @CorrectEnum(enumClass = EventType.class)
    private EventType eventType;

    @JsonProperty("when")
    @NotNull
    private LocalDateTime timestamp;
}


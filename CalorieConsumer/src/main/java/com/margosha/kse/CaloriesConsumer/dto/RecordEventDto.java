package com.margosha.kse.CaloriesConsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.CaloriesConsumer.dto.enums.EventType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordEventDto {
    private RecordResponseDto record;

    @NotNull
    private UUID id;

    @JsonProperty("event_type")
    @NotNull
    private EventType eventType;

    @NotNull
    @Past
    private LocalDateTime when;
}

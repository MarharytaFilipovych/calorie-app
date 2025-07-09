package com.margosha.kse.calories.subscription_graphql_client.dto;

import com.margosha.kse.calories.subscription_graphql_client.annotation.CorrectEnum;
import com.margosha.kse.calories.subscription_graphql_client.dto.enums.EventType;
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

    @NotNull
    @CorrectEnum(enumClass = EventType.class)
    private EventType eventType;

    @NotNull
    @Past
    private LocalDateTime when;
}

package com.margosha.kse.calories.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordEventDto {
    @JsonProperty("record")
    private RecordResponseDto entityData;
    @JsonProperty("when")
    private LocalDateTime timestamp;
}

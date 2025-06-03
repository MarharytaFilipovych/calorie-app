package com.margosha.kse.calories.business.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.business.service.dto.subdto.ProductRecordInResponseDto;
import com.margosha.kse.calories.presentation.enums.MealType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class RecordResponseDto {
    private UUID id;

    @JsonProperty("meal_type")
    private MealType mealType;

    @JsonProperty("consumed_at")
    private LocalDateTime consumedAt;

    private List<ProductRecordInResponseDto> products;

    @JsonProperty("calories_consumed")
    private int caloriesConsumed;

    @JsonProperty("total_quantity")
    private double totalQuantity;
}
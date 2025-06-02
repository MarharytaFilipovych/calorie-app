package com.margosha.kse.calories.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.data.enums.MealType;
import com.margosha.kse.calories.presentation.dto.subdto.ProductRecordInResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class RecordResponse {
    private UUID id;

    @JsonProperty("meal_type")
    private MealType mealType;

    @JsonProperty("consumed_at")
    private LocalDateTime consumedAt;

    private List<ProductRecordInResponse> products;

    @JsonProperty("calories_consumed")
    private int caloriesConsumed;

    @JsonProperty("total_quantity")
    private double totalQuantity;
}
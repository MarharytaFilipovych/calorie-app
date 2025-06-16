package com.margosha.kse.CaloriesConsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.CaloriesConsumer.enums.MealType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordResponseDto {
    private UUID id;

    @JsonProperty("meal_type")
    private MealType mealType;

    @JsonProperty("consumed_at")
    private LocalDateTime consumedAt;

    private Set<ProductRecordInResponseDto> products;

    @JsonProperty("calories_consumed")
    private int caloriesConsumed;

    @JsonProperty("total_quantity")
    private double totalQuantity;

    @JsonProperty("total_proteins")
    private double totalProteins;

    @JsonProperty("total_fats")
    private double totalFats;

    @JsonProperty("total_carbohydrates")
    private double totalCarbohydrates;
}
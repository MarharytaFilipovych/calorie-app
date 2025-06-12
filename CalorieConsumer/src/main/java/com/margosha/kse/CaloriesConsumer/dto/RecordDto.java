package com.margosha.kse.CaloriesConsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.CaloriesConsumer.enums.MealType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class RecordDto {
    @NotNull
    private UUID id;

    @JsonProperty("meal_type")
    private MealType mealType;

    @JsonProperty("consumed_at")
    private LocalDateTime consumedAt;

    @Valid
    private Set<ProductRecordDto> products;

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
package com.margosha.kse.calorie_client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calorie_client.dto.subdto.ProductRecordInResponse;
import com.margosha.kse.calorie_client.enums.MealType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class RecordResponse {
    private UUID id;

    @JsonProperty("meal_type")
    private MealType mealType;

    @JsonProperty("consumed_at")
    private LocalDateTime consumedAt;

    private Set<ProductRecordInResponse> products;

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
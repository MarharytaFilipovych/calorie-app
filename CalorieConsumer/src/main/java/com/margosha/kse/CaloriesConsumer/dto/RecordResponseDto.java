package com.margosha.kse.CaloriesConsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.CaloriesConsumer.dto.enums.MealType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private UUID id;

    @JsonProperty("meal_type")
    @NotNull
    private MealType mealType;

    @JsonProperty("consumed_at")
    @NotNull
    private LocalDateTime consumedAt;

    @Valid
    private Set<ProductRecordInResponseDto> products;

    @JsonProperty("calories_consumed")
    @NotNull
    @Min(0)
    private int caloriesConsumed;

    @JsonProperty("total_quantity")
    @NotNull
    @Min(0)
    private double totalQuantity;

    @JsonProperty("total_proteins")
    @NotNull
    @Min(0)
    private double totalProteins;

    @JsonProperty("total_fats")
    @NotNull
    @Min(0)
    private double totalFats;

    @JsonProperty("total_carbohydrates")
    @NotNull
    @Min(0)
    private double totalCarbohydrates;
}
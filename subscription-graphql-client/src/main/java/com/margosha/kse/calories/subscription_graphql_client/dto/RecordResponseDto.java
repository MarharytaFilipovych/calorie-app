package com.margosha.kse.calories.subscription_graphql_client.dto;

import com.margosha.kse.calories.subscription_graphql_client.annotation.CorrectEnum;
import com.margosha.kse.calories.subscription_graphql_client.dto.enums.MealType;
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

    @NotNull
    @CorrectEnum(enumClass = MealType.class)
    private MealType mealType;

    @NotNull
    private LocalDateTime consumedAt;

    @Valid
    private Set<ProductRecordInResponseDto> products;

    @NotNull
    @Min(0)
    private int caloriesConsumed;

    @NotNull
    @Min(0)
    private double totalQuantity;

    @NotNull
    @Min(0)
    private double totalProteins;

    @NotNull
    @Min(0)
    private double totalFats;

    @NotNull
    @Min(0)
    private double totalCarbohydrates;
}
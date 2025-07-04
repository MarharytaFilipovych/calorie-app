package com.margosha.kse.calories.presentation.graphql.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.presentation.annotations.CorrectEnum;
import com.margosha.kse.calories.presentation.enums.MealType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class RecordInput {
    @NotNull(message = "Products list is required")
    @NotEmpty(message = "Products list cannot be empty")
    @Valid
    @JsonProperty("products")
    private Set<ProductRecordInput> productRecords;

    @NotNull(message = "Meal type property is obligatory!")
    @CorrectEnum(enumClass = MealType.class)
    @JsonProperty("meal_type")
    private MealType mealType;
}

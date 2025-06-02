package com.margosha.kse.calories.presentation.dto;

import com.margosha.kse.calories.presentation.dto.subdto.ProductRecordInRequest;
import com.margosha.kse.calories.presentation.enums.MealType;
import com.margosha.kse.calories.presentation.validation.CorrectEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RecordRequest {
    @NotNull(message = "Products list is required")
    @NotEmpty(message = "Products list cannot be empty")
    @Valid
    private List<ProductRecordInRequest> products;

    @NotNull(message = "Meal type property is obligatory!")
    @CorrectEnum(enumClass = MealType.class)
    @JsonProperty("meal_type")
    private MealType mealType;
}

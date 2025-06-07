package com.margosha.kse.calories.business.dto;

import com.margosha.kse.calories.business.dto.subdto.ProductRecordInRequestDto;
import com.margosha.kse.calories.presentation.enums.MealType;
import com.margosha.kse.calories.presentation.annotations.CorrectEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;

@Data
public class RecordRequestDto {
    @NotNull(message = "Products list is required")
    @NotEmpty(message = "Products list cannot be empty")
    @Valid
    private Set<ProductRecordInRequestDto> productRecords;

    @NotNull(message = "Meal type property is obligatory!")
    @CorrectEnum(enumClass = MealType.class)
    @JsonProperty("meal_type")
    private MealType mealType;
}

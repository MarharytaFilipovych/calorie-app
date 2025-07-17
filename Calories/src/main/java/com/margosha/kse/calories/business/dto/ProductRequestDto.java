package com.margosha.kse.calories.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.presentation.annotations.CorrectEnum;
import com.margosha.kse.calories.presentation.annotations.CorrectName;
import com.margosha.kse.calories.presentation.annotations.Nutrient;
import com.margosha.kse.calories.presentation.enums.MeasurementUnit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.UUID;

@Data
public class ProductRequestDto {
    @CorrectName
    @ToolParam(description = "Product name")
    private String name;

    @ToolParam(description = "Product barcode (20-255 characters)", required = false)
    @Size(min = 20, max = 255, message = "Barcode must be between 20 and 255 characters")
    private String barcode;

    @ToolParam(description = "Protein content per 100g")
    @Nutrient(nutrient = "Protein")
    private Double proteins;

    @ToolParam(description = "Fat content per 100g")
    @Nutrient(nutrient = "Fats")
    private Double fats;

    @ToolParam(description = "Carbohydrates content per 100g")
    @Nutrient(nutrient = "Carbohydrates")
    private Double carbohydrates;

    @ToolParam(description = "Water content percentage")
    @Nutrient(nutrient = "Water", measurement = "%")
    private Double water;

    @ToolParam(description = "Salt content per 100g")
    @Nutrient(nutrient = "Salt")
    private Double salt;

    @ToolParam(description = "Sugar content per 100g")
    @Nutrient(nutrient = "Sugar")
    private Double sugar;

    @ToolParam(description = "Fiber content per 100g")
    @Nutrient(nutrient = "Fiber")
    private Double fiber;

    @ToolParam(description = "Alcohol content per 100g")
    @Nutrient(nutrient = "Alcohol")
    private Double alcohol;

    @Length(max = 255)
    @ToolParam(description = "Product description", required = false)
    private String description;

    @Min(value = 0, message = "Calories cannot be negative")
    @ToolParam(description = "Calories per 100g (will be calculated if not provided)", required = false)
    private Integer calories;

    @NotNull(message = "Measurement unit is required")
    @CorrectEnum(enumClass = MeasurementUnit.class)
    @JsonProperty("measurement_unit")
    @ToolParam(description = "Measurement unit (GRAMS, MILLILITERS, PIECES, etc.)")
    private MeasurementUnit measurementUnit;

    @JsonProperty("brand_id")
    @ToolParam(description = "Brand ID (UUID)", required = false)
    private UUID brandId;
}

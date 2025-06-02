package com.margosha.kse.calories.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.presentation.enums.MeasurementUnit;
import com.margosha.kse.calories.presentation.validation.CorrectEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;


@Data
public class Product {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 150, message = "Name must be between 2 and 150 characters")
    private String name;

    @Size(min = 20, max = 255, message = "Barcode must be between 20 and 255 characters")
    private String barcode;

    @NotNull(message = "Proteins value is required")
    @DecimalMin(value = "0.0", message = "Proteins cannot be negative")
    @DecimalMax(value = "100.0", message = "Proteins cannot exceed 100")
    private Double proteins;

    @NotNull(message = "Fats value is required")
    @DecimalMin(value = "0.0", message = "Fats cannot be negative")
    @DecimalMax(value = "100.0", message = "Fats cannot exceed 100")
    private Double fats;

    @NotNull(message = "Carbohydrates value is required")
    @DecimalMin(value = "0.0", message = "Carbohydrates cannot be negative")
    @DecimalMax(value = "100.0", message = "Carbohydrates cannot exceed 100")
    private Double carbohydrates;

    @NotNull(message = "Water value is required")
    @DecimalMin(value = "0.0", message = "Water cannot be negative")
    @DecimalMax(value = "100.0", message = "Water cannot exceed 100")
    private Double water;

    @NotNull(message = "Salt value is required")
    @DecimalMin(value = "0.0", message = "Salt cannot be negative")
    @DecimalMax(value = "100.0", message = "Salt cannot exceed 100")
    private Double salt;

    @NotNull(message = "Sugar value is required")
    @DecimalMin(value = "0.0", message = "Sugar cannot be negative")
    @DecimalMax(value = "100.0", message = "Sugar cannot exceed 100")
    private Double sugar;

    @NotNull(message = "Fiber value is required")
    @DecimalMin(value = "0.0", message = "Fiber cannot be negative")
    @DecimalMax(value = "100.0", message = "Fiber cannot exceed 100")
    private Double fiber;

    @NotNull(message = "Alcohol value is required")
    @DecimalMin(value = "0.0", message = "Alcohol cannot be negative")
    @DecimalMax(value = "100.0", message = "Alcohol cannot exceed 100")
    private Double alcohol;

    private String description;

    @Min(value = 0, message = "Calories cannot be negative")
    private Integer calories;

    @NotNull(message = "Measurement unit is required")
    @CorrectEnum(enumClass = MeasurementUnit.class)
    private MeasurementUnit measurementUnit;
}

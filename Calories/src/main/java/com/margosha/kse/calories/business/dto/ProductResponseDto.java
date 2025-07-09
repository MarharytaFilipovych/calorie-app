package com.margosha.kse.calories.business.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.presentation.enums.MeasurementUnit;
import com.margosha.kse.calories.presentation.annotations.CorrectEnum;
import com.margosha.kse.calories.presentation.annotations.CorrectName;
import com.margosha.kse.calories.presentation.annotations.Nutrient;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

@Data
public class ProductResponseDto {
    private UUID id;
    private String name;
    private String barcode;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private Double water;
    private Double salt;
    private Double sugar;
    private Double fiber;
    private Double alcohol;
    private String description;
    private Integer calories;
    @JsonProperty("measurement_unit")
    private MeasurementUnit measurementUnit;
    private BrandDto brand;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean archived;
}

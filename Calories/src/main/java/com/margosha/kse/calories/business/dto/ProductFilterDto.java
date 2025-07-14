package com.margosha.kse.calories.business.dto;

import com.margosha.kse.calories.presentation.annotations.CorrectEnum;
import com.margosha.kse.calories.presentation.annotations.CorrectName;
import com.margosha.kse.calories.presentation.enums.MeasurementUnit;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ProductFilterDto {
    @CorrectName(required = false)
    private String name;

    @CorrectName(required = false)
    private String brandName;

    @Min(value = 0, message = "Calories cannot be negative")
    private Integer minCalories;

    @Min(value = 0, message = "Calories cannot be negative")
    private Integer maxCalories;

    @CorrectEnum(enumClass = MeasurementUnit.class, required = false)
    private MeasurementUnit measurementUnit;
}

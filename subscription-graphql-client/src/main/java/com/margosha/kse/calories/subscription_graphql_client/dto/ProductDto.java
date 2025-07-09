package com.margosha.kse.calories.subscription_graphql_client.dto;

import com.margosha.kse.calories.subscription_graphql_client.annotation.CorrectEnum;
import com.margosha.kse.calories.subscription_graphql_client.annotation.CorrectName;
import com.margosha.kse.calories.subscription_graphql_client.annotation.Nutrient;
import com.margosha.kse.calories.subscription_graphql_client.dto.enums.MeasurementUnit;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private UUID id;

    @CorrectName
    private String name;

    @Size(min = 20, max = 255, message = "Barcode must be between 20 and 255 characters")
    private String barcode;

    @Nutrient(nutrient = "Protein")
    private Double proteins;

    @Nutrient(nutrient = "Fats")
    private Double fats;

    @Nutrient(nutrient = "Carbohydrates")
    private Double carbohydrates;

    @Nutrient(nutrient = "Water", measurement = "%")
    private Double water;

    @Nutrient(nutrient = "Salt")
    private Double salt;

    @Nutrient(nutrient = "Sugar")
    private Double sugar;

    @Nutrient(nutrient = "Fiber")
    private Double fiber;

    @Nutrient(nutrient = "Alcohol")
    private Double alcohol;

    @Length(max = 255)
    private String description;

    @Min(value = 0, message = "Calories cannot be negative")
    private Integer calories;

    @NotNull(message = "Measurement unit is required")
    @CorrectEnum(enumClass = MeasurementUnit.class)
    private MeasurementUnit measurementUnit;

    private boolean archived;
}
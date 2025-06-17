package com.margosha.kse.CaloriesConsumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.CaloriesConsumer.enums.MeasurementUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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

    private boolean archived;
}

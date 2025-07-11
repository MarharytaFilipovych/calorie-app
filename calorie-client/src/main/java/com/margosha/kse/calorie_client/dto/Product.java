package com.margosha.kse.calorie_client.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calorie_client.enums.MeasurementUnit;
import lombok.Data;
import java.util.UUID;

@Data
public class Product {
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

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean archived;
}

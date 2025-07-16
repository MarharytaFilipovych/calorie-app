package com.margosha.kse.calorie_client.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
public class ProductRecord {
    @JsonPropertyDescription("Product's unique identifier (UUID)")
    private String productId;

    @JsonPropertyDescription("Quantity consumed (minimum 0.1)")
    private Double quantity;
}
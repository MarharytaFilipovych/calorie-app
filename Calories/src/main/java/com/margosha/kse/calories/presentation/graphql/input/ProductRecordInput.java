package com.margosha.kse.calories.presentation.graphql.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class ProductRecordInput {
    @NotNull(message = "Product id is obligatory!")
    @JsonProperty("product_id")
    private UUID productId;

    @NotNull(message = "Quantity is obligatory!")
    @DecimalMin(value = "0.1", message = "Quantity cannot go below 0.1 :((")
    private Double quantity;
}

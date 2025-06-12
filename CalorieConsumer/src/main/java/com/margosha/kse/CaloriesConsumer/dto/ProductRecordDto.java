package com.margosha.kse.CaloriesConsumer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRecordDto {
    @NotNull(message = "Product is required")
    @Valid
    private ProductDto product;

    @Min(value = 0, message = "quantity cannot be less than 0")
    private double quantity;
}

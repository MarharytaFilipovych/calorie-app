package com.margosha.kse.CaloriesConsumer.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRecordInResponseDto {
    @Valid
    @NotNull
    private ProductDto product;
    @NotNull
    private double quantity;
}

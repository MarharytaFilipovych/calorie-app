package com.margosha.kse.CaloriesConsumer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRecordInResponseDto {
    private ProductDto product;
    private double quantity;
}

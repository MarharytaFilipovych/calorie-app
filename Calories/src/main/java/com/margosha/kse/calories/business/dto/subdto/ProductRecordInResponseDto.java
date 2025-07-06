package com.margosha.kse.calories.business.dto.subdto;

import com.margosha.kse.calories.business.dto.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRecordInResponseDto {
    private ProductResponseDto product;
    private double quantity;
}

package com.margosha.kse.calories.business.service.dto.subdto;

import com.margosha.kse.calories.business.service.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRecordInResponseDto {
    private ProductDto product;
    private double quantity;
}

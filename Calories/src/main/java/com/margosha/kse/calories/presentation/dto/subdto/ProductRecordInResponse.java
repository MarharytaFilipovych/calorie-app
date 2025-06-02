package com.margosha.kse.calories.presentation.dto.subdto;

import com.margosha.kse.calories.presentation.dto.Product;
import lombok.Data;

@Data
public class ProductRecordInResponse {
    private Product product;
    private double quantity;
}

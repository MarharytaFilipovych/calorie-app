package com.margosha.kse.calorie_client.dto.subdto;

import com.margosha.kse.calorie_client.dto.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRecordInResponse {
    private Product product;
    private double quantity;
}

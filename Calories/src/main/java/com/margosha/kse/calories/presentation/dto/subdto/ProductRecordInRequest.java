package com.margosha.kse.calories.presentation.dto.subdto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
public class ProductRecordInRequest {

    @NotNull(message = "Product id is obligatory!")
    @UUID(message = "Product id must be a correct uuid!")
    private java.util.UUID productId;

    @NotNull(message = "Quantity is obligatory!")
    @DecimalMin(value = "0.1", message = "Quantity cannot go below 0.1 :((")
    private Double quantity;
}

package com.margosha.kse.calorie_client.dto.subdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ProductRecordInRequest {
    @JsonProperty("product_id")
    private UUID productId;

    private Double quantity;
}

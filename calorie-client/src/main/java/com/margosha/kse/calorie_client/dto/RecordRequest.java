package com.margosha.kse.calorie_client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calorie_client.dto.subdto.ProductRecordInRequest;
import com.margosha.kse.calorie_client.enums.MealType;
import lombok.Data;
import java.util.Set;

@Data
public class RecordRequest {

    @JsonProperty("products")
    private Set<ProductRecordInRequest> productRecords;

    @JsonProperty("meal_type")
    private MealType mealType;
}

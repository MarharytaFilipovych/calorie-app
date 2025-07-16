package com.margosha.kse.calorie_client.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import java.util.Set;

@Data
@JsonClassDescription("Update an existing food consumption record")
public class UpdateFoodRecord {
    @JsonPropertyDescription("User ID who owns the record")
    private String userId;

    @JsonPropertyDescription("Record ID to update")
    private String recordId;

    @JsonPropertyDescription("Meal type: BREAKFAST, LUNCH, DINNER, SNACK")
    private String mealType;

    @JsonPropertyDescription("List of products with quantities")
    private Set<ProductRecord> products;
}
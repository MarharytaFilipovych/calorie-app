package com.margosha.kse.calorie_client.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;
import java.util.Set;

@Data
@JsonClassDescription("Record food consumption for a user")
public class CreateFoodRecord {
    @JsonPropertyDescription("User's unique identifier (UUID)")
    private String userId;

    @JsonPropertyDescription("Type of meal: BREAKFAST, LUNCH, DINNER, FIRST_SNACK, SECOND_SNACK, THIRD_SNACK")
    private String mealType;

    @JsonPropertyDescription("List of products consumed")
    private Set<ProductRecord> products;
}
package com.margosha.kse.calorie_client.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
@JsonClassDescription("Delete a food consumption record")
public class DeleteFoodRecord {
    @JsonPropertyDescription("User ID who owns the record")
    private String userId;

    @JsonPropertyDescription("Record ID to delete")
    private String recordId;
}
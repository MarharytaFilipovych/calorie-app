package com.margosha.kse.calorie_client.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
@JsonClassDescription("Get a specific food consumption record by ID")
public class GetSingleRecord {
    @JsonPropertyDescription("User ID who owns the record")
    private String userId;

    @JsonPropertyDescription("Record ID to retrieve")
    private String recordId;
}
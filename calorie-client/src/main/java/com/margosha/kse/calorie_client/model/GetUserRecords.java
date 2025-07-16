package com.margosha.kse.calorie_client.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
@JsonClassDescription("Get user's food consumption records with optional date filtering")
public class GetUserRecords {
    @JsonPropertyDescription("User's unique identifier (UUID)")
    private String userId;

    @JsonPropertyDescription("Filter by date (YYYY-MM-DD format, optional)")
    private String date;

    @JsonPropertyDescription("Number of records to return (default: 20, max: 100)")
    private Integer limit = 20;

    @JsonPropertyDescription("Page number (default: 1)")
    private Integer page = 1;
}

package com.margosha.kse.calorie_client.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
@JsonClassDescription("Search for food products by name with pagination")
public class SearchProducts {
    @JsonPropertyDescription("Product name to search for (optional)")
    private String name;

    @JsonPropertyDescription("Number of results to return (default: 20, max: 100)")
    private Integer limit = 20;

    @JsonPropertyDescription("Page number (default: 1)")
    private Integer page = 1;
}
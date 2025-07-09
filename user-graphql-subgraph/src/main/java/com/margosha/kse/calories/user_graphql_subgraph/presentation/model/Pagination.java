package com.margosha.kse.calories.user_graphql_subgraph.presentation.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class Pagination {
    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 100, message = "Limit cannot exceed 100")
    private int limit = 20;

    @Min(value = 1, message = "Page must be at least 1")
    private int offset = 1;
}

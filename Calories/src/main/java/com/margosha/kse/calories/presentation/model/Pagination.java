package com.margosha.kse.calories.presentation.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class Pagination {
    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 100, message = "Limit cannot exceed 100")
    private final int limit = 20;
    @Min(value = 1, message = "Offset must be at least 1")
    private final int offset = 1;
}

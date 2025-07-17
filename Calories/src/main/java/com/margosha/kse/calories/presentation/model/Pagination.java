package com.margosha.kse.calories.presentation.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pagination {
    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 100, message = "Limit cannot exceed 100")
    @ToolParam(description = "Limit number of results (default: 10)", required = false)
    private Integer limit = 20;

    @Min(value = 1, message = "Page must be at least 1")
    @ToolParam(description = "Offset for pagination (default: 0)", required = false)
    private Integer offset = 1;
}

package com.margosha.kse.calories.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.presentation.annotations.CorrectName;
import lombok.Builder;
import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.UUID;

@Data
@Builder
public class BrandDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @CorrectName
    @ToolParam(description = "Brand name")
    private String name;

    @ToolParam(description = "Brand description", required = false)
    private String description;
}

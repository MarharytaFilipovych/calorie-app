package com.margosha.kse.calories.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.presentation.annotations.CorrectName;
import lombok.Data;
import java.util.UUID;

@Data
public class BrandDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @CorrectName
    private String name;

    private String description;
}

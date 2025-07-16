package com.margosha.kse.calorie_client.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@JsonClassDescription("Get user information by ID")
@Data
public class GetUser {
    @JsonPropertyDescription("User's unique identifier (UUID)")
    private String userId;
}
// Replace your existing CreateUser.java with this version
package com.margosha.kse.calorie_client.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

@Data
@JsonClassDescription("Create a new user profile for calorie tracking")
public class CreateUser {
    @JsonPropertyDescription("User's email address")
    private String email = "user@example.com";

    @JsonPropertyDescription("User's first name")
    private String firstName = "John";

    @JsonPropertyDescription("User's last name")
    private String lastName = "Doe";

    @JsonPropertyDescription("Birth date in YYYY-MM-DD format")
    private String birthDate = "1990-01-01";

    @JsonPropertyDescription("User's gender: MALE, FEMALE, OTHER")
    private String gender = "OTHER";

    @JsonPropertyDescription("Weight in kilograms")
    private Double weight = 70.0;

    @JsonPropertyDescription("Height in centimeters")
    private Double height = 170.0;

    @JsonPropertyDescription("Daily activity level: LOW, MODERATE, HIGH")
    private String activityLevel = "MODERATE";

    @JsonPropertyDescription("Weight goal: LOSE, MAINTAIN, GAIN")
    private String goal = "MAINTAIN";

    @JsonPropertyDescription("Target weight in kg")
    private Double targetWeight = 70.0;

    @JsonPropertyDescription("Phone number (optional)")
    private String telephone;
}
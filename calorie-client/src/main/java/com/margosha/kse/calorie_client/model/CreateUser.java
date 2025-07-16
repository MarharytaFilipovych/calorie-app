package com.margosha.kse.calorie_client.model;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonClassDescription("Create a new user profile for calorie tracking")
public class CreateUser {
    @JsonPropertyDescription("User's email address")
    private String email;

    @JsonPropertyDescription("User's first name")
    private String firstName;

    @JsonPropertyDescription("User's last name")
    private String lastName;

    @JsonPropertyDescription("Birth date in YYYY-MM-DD format")
    private String birthDate;

    @JsonPropertyDescription("User's gender: MALE or FEMALE")
    private String gender;

    @JsonPropertyDescription("Weight in kilograms (20-250)")
    private Double weight;

    @JsonPropertyDescription("Height in centimeters (100-230)")
    private Double height;

    @JsonPropertyDescription("Daily activity level: SEDENTARY, LOW, MODERATE, HIGH, VERY_HIGH")
    private String activityLevel;

    @JsonPropertyDescription("Weight goal: LOSE, MAINTAIN, GAIN")
    private String goal;

    @JsonPropertyDescription("Target weight in kg (optional, 30-120)")
    private Double targetWeight;

    @JsonPropertyDescription("Phone number (optional). Telephone must be in format +[country code][number] (e.g., +380123456789), regexp -> ^\\+\\d{1,4}\\d{6,14}$")
    private String telephone;
}
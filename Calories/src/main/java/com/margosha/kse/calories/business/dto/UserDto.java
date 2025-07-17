package com.margosha.kse.calories.business.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.presentation.enums.ActivityLevel;
import com.margosha.kse.calories.presentation.enums.Gender;
import com.margosha.kse.calories.presentation.enums.Goal;
import com.margosha.kse.calories.presentation.annotations.CorrectEnum;
import com.margosha.kse.calories.presentation.annotations.CorrectName;
import jakarta.validation.constraints.*;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.ai.tool.annotation.ToolParam;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Length(max = 255)
    @ToolParam(description = "User email address")
    private String email;

    @CorrectName
    @JsonProperty("first_name")
    @ToolParam(description = "First name")
    private String firstName;

    @CorrectName
    @JsonProperty("last_name")
    @ToolParam(description = "Last name")
    private String lastName;

    @Pattern(regexp = "^\\+\\d{1,4}\\d{6,14}$", message = "Telephone must be in format +[country code][number] (e.g., +380123456789)")
    @ToolParam(description = "Phone number in format +[country code][number]. Pattern: ^\\+\\d{1,4}\\d{6,14}$", required = false)
    private String telephone;

    @NotNull(message = "Birth date is required")
    @Past(message = "You cannot be born in the future!")
    @JsonProperty("birth_date")
    @ToolParam(description = "Birth date in YYYY-MM-DD format")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    @CorrectEnum(enumClass = Gender.class)
    @ToolParam(description = "Gender (MALE or FEMALE)")
    private Gender gender;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "20", message = "Weight must be at least 20 kg")
    @DecimalMax(value = "250", message = "Weight cannot exceed 250 kg")
    @ToolParam(description = "Weight in kg (20-250)")
    private Double weight;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "100", message = "Height must be at least 100 cm")
    @DecimalMax(value = "230", message = "Height cannot exceed 230 cm")
    @ToolParam(description = "Height in cm (100-230)")
    private Double height;

    @NotNull(message = "Activity level is required")
    @CorrectEnum(enumClass = ActivityLevel.class)
    @JsonProperty("activity_level")
    @ToolParam(description = "Activity level (SEDENTARY, LOW, MODERATE, HIGH, VERY_HIGH)")
    private ActivityLevel activityLevel;

    @NotNull(message = "Goal is required")
    @CorrectEnum(enumClass = Goal.class)
    @ToolParam(description = "Goal (LOSE, MAINTAIN, GAIN)")
    private Goal goal;

    @DecimalMin(value = "30", message = "Target weight must be at least 30 kg")
    @DecimalMax(value = "120", message = "Target weight cannot exceed 120 kg")
    @JsonProperty("target_weight")
    @ToolParam(description = "Target weight in kg (30-120, optional)", required = false)
    private Double targetWeight;
}

package com.margosha.kse.calories.presentation.graphql.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calories.presentation.annotations.CorrectEnum;
import com.margosha.kse.calories.presentation.annotations.CorrectName;
import com.margosha.kse.calories.presentation.enums.ActivityLevel;
import com.margosha.kse.calories.presentation.enums.Gender;
import com.margosha.kse.calories.presentation.enums.Goal;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import java.time.LocalDate;

@Data
public class UserInput {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Length(max = 255)
    private String email;

    @CorrectName
    @JsonProperty("first_name")
    private String firstName;

    @CorrectName
    @JsonProperty("last_name")
    private String lastName;

    @Pattern(regexp = "^\\+\\d{1,4}\\d{6,14}$", message = "Telephone must be in format +[country code][number] (e.g., +380123456789)")
    private String telephone;

    @NotNull(message = "Birth date is required")
    @Past(message = "You cannot be born in the future!")
    @JsonProperty("birth_date")
    private LocalDate birthDate;

    @NotNull(message = "Gender is required")
    @CorrectEnum(enumClass = Gender.class)
    private Gender gender;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "20", message = "Weight must be at least 20 kg")
    @DecimalMax(value = "250", message = "Weight cannot exceed 250 kg")
    private Double weight;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "100", message = "Height must be at least 100 cm")
    @DecimalMax(value = "230", message = "Height cannot exceed 230 cm")
    private Double height;

    @NotNull(message = "Activity level is required")
    @CorrectEnum(enumClass = ActivityLevel.class)
    @JsonProperty("activity_level")
    private ActivityLevel activityLevel;

    @NotNull(message = "Goal is required")
    @CorrectEnum(enumClass = Goal.class)
    private Goal goal;

    @DecimalMin(value = "30", message = "Target weight must be at least 30 kg")
    @DecimalMax(value = "120", message = "Target weight cannot exceed 120 kg")
    @JsonProperty("target_weight")
    private Double targetWeight;
}
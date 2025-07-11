package com.margosha.kse.calorie_client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.margosha.kse.calorie_client.enums.ActivityLevel;
import com.margosha.kse.calorie_client.enums.Gender;
import com.margosha.kse.calorie_client.enums.Goal;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class User {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    private String email;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String telephone;

    @JsonProperty("birth_date")
    private LocalDate birthDate;

    private Gender gender;

    private Double weight;

    private Double height;

    @JsonProperty("activity_level")
    private ActivityLevel activityLevel;

    private Goal goal;

    @JsonProperty("target_weight")
    private Double targetWeight;
}
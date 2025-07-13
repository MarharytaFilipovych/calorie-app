package com.margosha.kse.calories.presentation.grpc.mapper;

import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.presentation.enums.ActivityLevel;
import com.margosha.kse.calories.presentation.enums.Gender;
import com.margosha.kse.calories.presentation.enums.Goal;
import com.margosha.kse.calories.proto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CommonGrpcMapper.class})
public interface UserGrpcMapper {

    @Mapping(target = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "email", qualifiedByName = "nullToEmptyString")
    @Mapping(target = "firstName", qualifiedByName = "nullToEmptyString")
    @Mapping(target = "lastName", qualifiedByName = "nullToEmptyString")
    @Mapping(target = "telephone", qualifiedByName = "nullToEmptyString")
    @Mapping(target = "weight", qualifiedByName = "nullToZeroDouble")
    @Mapping(target = "height", qualifiedByName = "nullToZeroDouble")
    @Mapping(target = "targetWeight", qualifiedByName = "nullToZeroDouble")
    User toProto(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    UserDto fromProtoInput(UserInput userInput);

    default com.margosha.kse.calories.proto.Gender genderToProto(Gender gender) {
        if (gender == null) return com.margosha.kse.calories.proto.Gender.MALE;
        return switch (gender) {
            case MALE -> com.margosha.kse.calories.proto.Gender.MALE;
            case FEMALE -> com.margosha.kse.calories.proto.Gender.FEMALE;
        };
    }

    default Gender protoToGender(com.margosha.kse.calories.proto.Gender gender) {
        if (gender == null) return Gender.MALE;
        return switch (gender) {
            case MALE -> Gender.MALE;
            case FEMALE -> Gender.FEMALE;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Unknown gender: " + gender);
        };
    }

    default com.margosha.kse.calories.proto.ActivityLevel activityLevelToProto(ActivityLevel activityLevel) {
        if (activityLevel == null) {
            return com.margosha.kse.calories.proto.ActivityLevel.SEDENTARY;
        }
        return switch (activityLevel) {
            case SEDENTARY -> com.margosha.kse.calories.proto.ActivityLevel.SEDENTARY;
            case LOW -> com.margosha.kse.calories.proto.ActivityLevel.LOW;
            case MODERATE -> com.margosha.kse.calories.proto.ActivityLevel.MODERATE;
            case HIGH -> com.margosha.kse.calories.proto.ActivityLevel.HIGH;
            case VERY_HIGH -> com.margosha.kse.calories.proto.ActivityLevel.VERY_HIGH;
        };
    }

    default ActivityLevel protoToActivityLevel(com.margosha.kse.calories.proto.ActivityLevel activityLevel) {
        if (activityLevel == null) {
            return ActivityLevel.SEDENTARY;
        }
        return switch (activityLevel) {
            case SEDENTARY -> ActivityLevel.SEDENTARY;
            case LOW -> ActivityLevel.LOW;
            case MODERATE -> ActivityLevel.MODERATE;
            case HIGH -> ActivityLevel.HIGH;
            case VERY_HIGH -> ActivityLevel.VERY_HIGH;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Unknown activity level: " + activityLevel);
        };
    }

    default com.margosha.kse.calories.proto.Goal goalToProto(Goal goal) {
        if (goal == null) return com.margosha.kse.calories.proto.Goal.MAINTAIN;
        return switch (goal) {
            case LOSE -> com.margosha.kse.calories.proto.Goal.LOSE;
            case MAINTAIN -> com.margosha.kse.calories.proto.Goal.MAINTAIN;
            case GAIN -> com.margosha.kse.calories.proto.Goal.GAIN;
        };
    }

    default Goal protoToGoal(com.margosha.kse.calories.proto.Goal goal) {
        if (goal == null) return Goal.MAINTAIN;
        return switch (goal) {
            case LOSE -> Goal.LOSE;
            case MAINTAIN -> Goal.MAINTAIN;
            case GAIN -> Goal.GAIN;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Unknown goal: " + goal);
        };
    }
}
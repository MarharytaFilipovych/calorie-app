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
    @Mapping(target = "telephone", source = "telephone", qualifiedByName = "nullToEmptyString")
    User toProto(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    UserDto toDto(UserInput userInput);

    default com.margosha.kse.calories.proto.Gender genderToProto(Gender gender) {
        if (gender == null) return com.margosha.kse.calories.proto.Gender.MALE;
        return com.margosha.kse.calories.proto.Gender.valueOf(gender.name());
    }

    default Gender protoToGender(com.margosha.kse.calories.proto.Gender gender) {
        if (gender == null) return Gender.MALE;
        return Gender.valueOf(gender.name());
    }

    default com.margosha.kse.calories.proto.ActivityLevel activityLevelToProto(ActivityLevel activityLevel) {
        if (activityLevel == null) return com.margosha.kse.calories.proto.ActivityLevel.SEDENTARY;
        return com.margosha.kse.calories.proto.ActivityLevel.valueOf(activityLevel.name());
    }

    default ActivityLevel protoToActivityLevel(com.margosha.kse.calories.proto.ActivityLevel activityLevel) {
        if (activityLevel == null) return ActivityLevel.SEDENTARY;
        return ActivityLevel.valueOf(activityLevel.name());
    }

    default com.margosha.kse.calories.proto.Goal goalToProto(Goal goal) {
        if (goal == null) return com.margosha.kse.calories.proto.Goal.MAINTAIN;
        return com.margosha.kse.calories.proto.Goal.valueOf(goal.name());
    }

    default Goal protoToGoal(com.margosha.kse.calories.proto.Goal goal) {
        if (goal == null) return Goal.MAINTAIN;
        return Goal.valueOf(goal.name());
    }
}
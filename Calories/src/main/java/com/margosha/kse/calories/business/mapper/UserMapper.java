package com.margosha.kse.calories.business.mapper;

import com.margosha.kse.calories.data.entity.User;
import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.presentation.enums.ActivityLevel;
import com.margosha.kse.calories.presentation.enums.Gender;
import com.margosha.kse.calories.presentation.enums.Goal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    default Gender map(com.margosha.kse.calories.data.enums.Gender entityGender) {
        return Gender.valueOf(entityGender.name());
    }

    default com.margosha.kse.calories.data.enums.Gender map(Gender dtoGender) {
        return com.margosha.kse.calories.data.enums.Gender.valueOf(dtoGender.name());
    }

    default ActivityLevel map(com.margosha.kse.calories.data.enums.ActivityLevel entityActivityLevel) {
        return ActivityLevel.valueOf(entityActivityLevel.name());
    }

    default com.margosha.kse.calories.data.enums.ActivityLevel map(ActivityLevel dtoActivityLevel) {
        return com.margosha.kse.calories.data.enums.ActivityLevel.valueOf(dtoActivityLevel.name());
    }

    default Goal map(com.margosha.kse.calories.data.enums.Goal entityGoal) {
        return Goal.valueOf(entityGoal.name());
    }

    default com.margosha.kse.calories.data.enums.Goal map(Goal dtoGoal) {
        return com.margosha.kse.calories.data.enums.Goal.valueOf(dtoGoal.name());
    }
}
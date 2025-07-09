package com.margosha.kse.calories.user_graphql_subgraph.business.mapper;

import com.margosha.kse.calories.user_graphql_subgraph.business.dto.UserDto;
import com.margosha.kse.calories.user_graphql_subgraph.data.entity.User;
import com.margosha.kse.calories.user_graphql_subgraph.presentation.enums.ActivityLevel;
import com.margosha.kse.calories.user_graphql_subgraph.presentation.enums.Gender;
import com.margosha.kse.calories.user_graphql_subgraph.presentation.enums.Goal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    default Gender map(com.margosha.kse.calories.user_graphql_subgraph.data.entity.enums.Gender entityGender) {
        return Gender.valueOf(entityGender.name());
    }

    default com.margosha.kse.calories.user_graphql_subgraph.data.entity.enums.Gender map(Gender dtoGender) {
        return com.margosha.kse.calories.user_graphql_subgraph.data.entity.enums.Gender.valueOf(dtoGender.name());
    }

    default ActivityLevel map(com.margosha.kse.calories.user_graphql_subgraph.data.entity.enums.ActivityLevel entityActivityLevel) {
        return ActivityLevel.valueOf(entityActivityLevel.name());
    }

    default com.margosha.kse.calories.user_graphql_subgraph.data.entity.enums.ActivityLevel map(ActivityLevel dtoActivityLevel) {
        return com.margosha.kse.calories.user_graphql_subgraph.data.entity.enums.ActivityLevel.valueOf(dtoActivityLevel.name());
    }

    default Goal map(com.margosha.kse.calories.user_graphql_subgraph.data.entity.enums.Goal entityGoal) {
        return Goal.valueOf(entityGoal.name());
    }

    default com.margosha.kse.calories.user_graphql_subgraph.data.entity.enums.Goal map(Goal dtoGoal) {
        return com.margosha.kse.calories.user_graphql_subgraph.data.entity.enums.Goal.valueOf(dtoGoal.name());
    }
}
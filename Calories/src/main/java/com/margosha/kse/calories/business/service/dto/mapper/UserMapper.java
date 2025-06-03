package com.margosha.kse.calories.business.service.dto.mapper;

import com.margosha.kse.calories.data.entity.User;
import com.margosha.kse.calories.business.service.dto.UserDto;
import com.margosha.kse.calories.presentation.enums.ActivityLevel;
import com.margosha.kse.calories.presentation.enums.Gender;
import com.margosha.kse.calories.presentation.enums.Goal;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setTelephone(user.getTelephone());
        dto.setBirthDate(user.getBirthDate());
        dto.setGender(Gender.valueOf(user.getGender().name()));
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setActivityLevel(ActivityLevel.valueOf(user.getActivityLevel().name()));
        dto.setGoal(Goal.valueOf(user.getGoal().name()));
        dto.setTargetWeight(user.getTargetWeight());
        return dto;
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setTelephone(dto.getTelephone());
        user.setBirthDate(dto.getBirthDate());
        user.setGender(com.margosha.kse.calories.data.enums.Gender.valueOf(dto.getGender().name()));
        user.setWeight(dto.getWeight());
        user.setHeight(dto.getHeight());
        user.setActivityLevel(com.margosha.kse.calories.data.enums.ActivityLevel.valueOf(dto.getActivityLevel().name()));
        user.setGoal(com.margosha.kse.calories.data.enums.Goal.valueOf(dto.getGoal().name()));
        user.setTargetWeight(dto.getTargetWeight());
        return user;
    }

}

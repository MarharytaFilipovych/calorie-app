package com.margosha.kse.calories.user_graphql_subgraph.business.service;

import com.margosha.kse.calories.user_graphql_subgraph.business.dto.UserDto;
import com.margosha.kse.calories.user_graphql_subgraph.business.mapper.UserMapper;
import com.margosha.kse.calories.user_graphql_subgraph.data.entity.User;
import com.margosha.kse.calories.user_graphql_subgraph.data.entity.enums.Gender;
import com.margosha.kse.calories.user_graphql_subgraph.data.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public Page<UserDto> getAllUsers(int limit, int offset){
        return userRepository.findAll(PageRequest.of(offset - 1, limit)).map(userMapper::toDto);
    }

    public UserDto createUser(UserDto dto){
        if(userRepository.existsByEmail(dto.getEmail()))throw new IllegalArgumentException("User with email " + dto.getEmail() + " already exits!");
        User user = userRepository.save(userMapper.toEntity(dto));
        return userMapper.toDto(user);
    }

    public UserDto getUserById(UUID id){
        return userRepository.findById(id).map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public UserDto getUserByEmail(String email){
        return userRepository.findByEmail(email).map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + email + " was not found!"));
    }

    public UserDto updateUser(UserDto dto, UUID id){
        if (!userRepository.existsById(id)) throw new EntityNotFoundException(id.toString());
        User updatedUser = userMapper.toEntity(dto);
        updatedUser.setId(id);
        return userMapper.toDto(userRepository.save(updatedUser));
    }

    public boolean deleteUser(UUID id){
        if(!userRepository.existsById(id))return false;
        userRepository.deleteById(id);
        return true;
    }

    private int calculateDailyTarget(User user){
        int age = Period.between(user.getBirthDate(), LocalDate.now()).getYears();
        boolean male = user.getGender().equals(Gender.MALE);
        double dailyCalories = getDailyCalories(user, age, male);
        if(male && dailyCalories < 1500) return 1500;
        else if(dailyCalories < 1200) return 1200;
        return (int)dailyCalories;
    }

    public Map<UUID, Integer> getDailyTargets(List<UUID> userIds){
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, this::calculateDailyTarget));
    }

    private double getDailyCalories(User user, int age, boolean male) {
        double dailyCalories =(10 * user.getWeight()) + (6.25 * user.getHeight()) - (5 * age);
        dailyCalories = male ? dailyCalories + 5 : dailyCalories - 161;
        switch(user.getActivityLevel()){
            case SEDENTARY -> dailyCalories *= 1.2;
            case LOW -> dailyCalories *= 1.375;
            case MODERATE -> dailyCalories *= 1.55;
            case HIGH -> dailyCalories *= 1.725;
            case VERY_HIGH -> dailyCalories *= 1.9;
        }
        switch (user.getGoal()){
            case LOSE -> dailyCalories -= 500;
            case GAIN -> dailyCalories += 500;
        }
        return dailyCalories;
    }
}

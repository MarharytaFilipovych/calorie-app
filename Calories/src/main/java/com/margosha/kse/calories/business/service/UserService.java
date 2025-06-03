package com.margosha.kse.calories.business.service;

import com.margosha.kse.calories.business.mapper.UserMapper;
import com.margosha.kse.calories.data.entity.User;
import com.margosha.kse.calories.data.repository.UserRepository;
import com.margosha.kse.calories.business.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserDto> getAll(int limit, int offset){
        return userRepository.findAll(PageRequest.of(offset - 1, limit)).map(UserMapper::toDto);
    }

    public UUID create(UserDto dto){
        User user = userRepository.save(UserMapper.toEntity(dto));
        return user.getId();
    }

    public UserDto getById(UUID id){
        return userRepository.findById(id).map(UserMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public void update(UserDto dto, UUID id){
        if (!userRepository.existsById(id)) throw new EntityNotFoundException(id.toString());
        User updatedUser = UserMapper.toEntity(dto);
        updatedUser.setId(id);
        userRepository.save(updatedUser);
    }

    public void delete(UUID id){
        userRepository.deleteById(id);
    }
}

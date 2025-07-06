package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.business.service.UserService;
import com.margosha.kse.calories.presentation.model.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class UserResolver {
    private final UserService userService;

    public UserResolver(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public UserDto user(@Argument @org.hibernate.validator.constraints.UUID String id){
        return userService.getUserById(UUID.fromString(id));
    }

    @QueryMapping
    public UserDto userByEmail(@Argument @Email String email){
        return userService.getUserByEmail(email);
    }

    @QueryMapping
    public Page<UserDto> users(@Argument @Valid Pagination pagination){
        return userService.getAllUsers(pagination.getLimit(), pagination.getOffset());
    }

    @MutationMapping
    public UserDto createUser(@Argument @Valid UserDto input){
        return userService.createUser(input);
    }

    @MutationMapping
    public UserDto updateUser(@Argument @org.hibernate.validator.constraints.UUID String id, @Argument @Valid UserDto input){
        return userService.updateUser(input, UUID.fromString(id));
    }

    @MutationMapping
    public Boolean deleteUser(@Argument @org.hibernate.validator.constraints.UUID String id){
        return userService.deleteUser(UUID.fromString(id));
    }
}

package com.margosha.kse.calories.presentation.controller;

import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.business.service.UserService;
import com.margosha.kse.calories.presentation.model.Meta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 100, message = "Limit cannot exceed 100")
            Integer limit,

            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Offset must be at least 1")
            Integer offset) {
        Page<UserDto> result = userService.getAll(limit, offset);
        return ResponseEntity.ok(Map.of(
                "meta" , new Meta(offset, result.getTotalElements(), limit, result.getTotalPages()),
                "users", result.getContent()
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> createUser(@Valid @RequestBody UserDto userDto){
        UUID id = userService.create(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        UserDto dto = userService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable UUID id ){
        userService.update(userDto, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id ){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

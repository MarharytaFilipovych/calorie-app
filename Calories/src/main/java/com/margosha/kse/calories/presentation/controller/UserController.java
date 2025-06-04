package com.margosha.kse.calories.presentation.controller;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.business.service.UserService;
import com.margosha.kse.calories.presentation.model.Meta;
import com.margosha.kse.calories.presentation.model.Pagination;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Validated
@Tag(name = "Users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers(
            @Parameter(description = "Pagination parameters")
            @ParameterObject Pagination pagination) {
        Page<UserDto> result = userService.getAllUsers(pagination.getLimit(), pagination.getOffset());
        return ResponseEntity.ok(Map.of(
                "meta" , new Meta(result),
                "users", result.getContent()
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> createUser(
            @Parameter(description = "User registration data")
            @Valid @RequestBody UserDto userDto){
        UUID id = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID id) {
        UserDto dto = userService.getUserById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(
            @Parameter(description = "User email")
            @PathVariable @Email(message = "Please, provide valid email!") String email) {
        UserDto dto = userService.getUserByEmail(email);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @Parameter(description = "Updated user information")
            @Valid @RequestBody UserDto userDto,
            @Parameter(description = "User unique identifier")
            @PathVariable UUID id ){
        userService.updateUser(userDto, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID id ){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/daily-target")
    public ResponseEntity<Map<String, Integer>> calculateDailyCalorieTarget(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID id){
        return ResponseEntity.ok(Map.of("daily_calorie_target", userService.getDailyTarget(id)));
    }

    @GetMapping("/{id}/records")
    public ResponseEntity<Map<String, Object>> getRecords(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID id,
            @Parameter(description = "Pagination parameters")
            @ParameterObject Pagination pagination,
            @Parameter(description = "Filter by consumption date (must be today or in the past)")
            @RequestParam(required = false)
            @PastOrPresent(message = "This date cannot point to the future!")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        Page<RecordResponseDto> result = userService.getRecords(id, pagination.getLimit(), pagination.getOffset(), date);
        return ResponseEntity.ok(Map.of(
                "meta" , new Meta(result),
                "records", result.getContent()
        ));
    }

    @GetMapping("/{userId}/records/{id}")
    public ResponseEntity<Map<String, RecordResponseDto>> getConsumption(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID userId,
            @Parameter(description = "Record unique identifier")
            @PathVariable UUID id){
        return ResponseEntity.ok(Map.of("record", userService.getConsumption(userId, id)));
    }

    @PostMapping("/{id}/records")
    public ResponseEntity<Map<String, UUID>> recordConsumption(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID id,
            @Parameter(description = "Consumption record with products and quantities")
            @Valid @RequestBody RecordRequestDto recordRequestDto){
        UUID recordId = userService.createRecord(id, recordRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", recordId));
    }

    @PutMapping("/{userId}/records/{id}")
    public ResponseEntity<Void> updateRecord(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID userId,
            @Parameter(description = "Record unique identifier")
            @PathVariable UUID id,
            @Parameter(description = "Updated consumption record data")
            @Valid @RequestBody RecordRequestDto recordRequestDto){
        userService.updateRecord(userId, id, recordRequestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/records/{id}")
    public ResponseEntity<Void> deleteRecord(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID userId,
            @Parameter(description = "Record unique identifier")
            @PathVariable UUID id){
        userService.deleteRecord(userId, id);
        return ResponseEntity.noContent().build();
    }
}
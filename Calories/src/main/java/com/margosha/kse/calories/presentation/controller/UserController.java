package com.margosha.kse.calories.presentation.controller;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.business.service.UserService;
import com.margosha.kse.calories.presentation.model.Meta;
import com.margosha.kse.calories.presentation.model.Pagination;
import jakarta.validation.Valid;
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
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers(@Validated Pagination pagination) {
        Page<UserDto> result = userService.getAllUsers(pagination.getLimit(), pagination.getOffset());
        return ResponseEntity.ok(Map.of(
                "meta" , new Meta(pagination.getOffset(), result.getTotalElements(), pagination.getLimit(), result.getTotalPages()),
                "users", result.getContent()
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> createUser(@Valid @RequestBody UserDto userDto){
        UUID id = userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        UserDto dto = userService.getUserById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable UUID id ){
        userService.updateUser(userDto, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id ){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/daily-target")
    public ResponseEntity<Map<String, Integer>> calculateDailyCalorieTarget(@PathVariable UUID id){
        return ResponseEntity.ok(Map.of("daily_calorie_target", userService.getDailyTarget(id)));
    }

    @GetMapping("/{id}/records")
    public ResponseEntity<Map<String, Object>> getRecords(
            @PathVariable UUID id, @Validated Pagination pagination,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        Page<RecordResponseDto> result = userService.getRecords(id, pagination.getLimit(), pagination.getOffset(), date);
        return ResponseEntity.ok(Map.of(
                "meta" , new Meta(pagination.getOffset(), result.getTotalElements(), pagination.getLimit(), result.getTotalPages()),
                "records", result.getContent()
        ));
    }

    @GetMapping("/{userId}/records/{id}")
    public ResponseEntity<Map<String, RecordResponseDto>> getConsumption(@PathVariable UUID userId, @PathVariable UUID id){
        return ResponseEntity.ok(Map.of("id", userService.getConsumption(userId, id)));
    }

    @PostMapping("/{id}/records")
    public ResponseEntity<Map<String, UUID>> recordConsumption(
            @PathVariable UUID id,
            @Valid @RequestBody RecordRequestDto recordRequestDto){
        UUID recordId = userService.createRecord(id, recordRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", recordId));
    }

    @PutMapping("/{userId}/records/{id}")
    public ResponseEntity<Void> updateRecord(
            @PathVariable UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody RecordRequestDto recordRequestDto){
        userService.updateRecord(userId, id, recordRequestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/records/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable UUID userId, @PathVariable UUID id){
        userService.deleteRecord(userId, id);
        return ResponseEntity.noContent().build();
    }
}

package com.margosha.kse.calories.presentation.controller;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.business.service.UserService;
import com.margosha.kse.calories.presentation.model.Meta;
import com.margosha.kse.calories.presentation.model.Pagination;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users/{userId}/records")
@Tag(name = "Consumption records")
public class ConsumptionController {

    private final UserService userService;

    public ConsumptionController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getRecords(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID userId,
            @Parameter(description = "Pagination parameters")
            @ParameterObject Pagination pagination,
            @Parameter(description = "Filter by consumption date (must be today or in the past)")
            @RequestParam(required = false)
            @PastOrPresent(message = "This date cannot point to the future!")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        Page<RecordResponseDto> result = userService.getRecords(userId, pagination.getLimit(), pagination.getPage(), date);
        return ResponseEntity.ok(Map.of(
                "meta" , new Meta(result),
                "records", result.getContent()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, RecordResponseDto>> getConsumption(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID userId,
            @Parameter(description = "Record unique identifier")
            @PathVariable UUID id){
        return ResponseEntity.ok(Map.of("record", userService.getConsumption(userId, id)));
    }

    @PostMapping()
    public ResponseEntity<Map<String, UUID>> recordConsumption(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID userId,
            @Parameter(description = "Consumption record with products and quantities")
            @Valid @RequestBody RecordRequestDto recordRequestDto){
        UUID recordId = userService.createRecord(userId, recordRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", recordId));
    }

    @PutMapping("/{id}")
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(
            @Parameter(description = "User unique identifier")
            @PathVariable UUID userId,
            @Parameter(description = "Record unique identifier")
            @PathVariable UUID id){
        userService.deleteRecord(userId, id);
        return ResponseEntity.noContent().build();
    }
}

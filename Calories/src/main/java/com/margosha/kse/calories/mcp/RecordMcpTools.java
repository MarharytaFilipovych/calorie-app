package com.margosha.kse.calories.mcp;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.business.service.RecordService;
import com.margosha.kse.calories.presentation.model.PageResponse;
import com.margosha.kse.calories.presentation.model.Pagination;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RecordMcpTools {
    
    private final RecordService recordService;

    @Tool(description = "Get consumption records for a specific user with optional date filtering")
    public PageResponse<RecordResponseDto> getUserRecords(
            @ToolParam(description = "User unique identifier (UUID)") UUID userId,
            @ToolParam(description = "Pagination parameters with limit and offset", required = false)
            Pagination pagination,
            @ToolParam(description = "Optional filter by date (YYYY-MM-DD format)", required = false)
            String date) {
        if(pagination ==  null)pagination = new Pagination();
        LocalDate filterDate = date != null ? LocalDate.parse(date) : null;
        Page<RecordResponseDto> page = recordService.getRecords(userId, pagination.getLimit(), pagination.getOffset(), filterDate, true);
        return PageResponse.from(page);
    }
    
    @Tool(description = "Get detailed information about a specific consumption record")
    public RecordResponseDto getConsumptionRecord(
        @ToolParam(description = "User unique identifier (UUID)") UUID userId,
        @ToolParam(description = "Record unique identifier (UUID)") UUID recordId) {
        return recordService.getConsumption(userId, recordId);
    }
    
    @Tool(description = "Create a new consumption record for a user")
    public RecordResponseDto createConsumptionRecord(
        @ToolParam(description = "User unique identifier (UUID)") UUID userId,
        @ToolParam(description = "Consumption record data with meal type and products list") 
        RecordRequestDto recordRequest) {
        return recordService.createRecord(userId, recordRequest);
    }
    
    @Tool(description = "Update an existing consumption record")
    public RecordResponseDto updateConsumptionRecord(
        @ToolParam(description = "User unique identifier (UUID)") UUID userId,
        @ToolParam(description = "Record unique identifier (UUID)") UUID recordId,
        @ToolParam(description = "Updated consumption record data") RecordRequestDto recordRequest) {
        return recordService.updateRecord(userId, recordId, recordRequest);
    }
    
    @Tool(description = "Delete a consumption record")
    public boolean deleteConsumptionRecord(
        @ToolParam(description = "User unique identifier (UUID)") UUID userId,
        @ToolParam(description = "Record unique identifier (UUID)") UUID recordId) {
        return recordService.deleteRecord(userId, recordId);
    }
}
package com.margosha.kse.calories.business.mapper;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.data.enums.MealType;
import com.margosha.kse.calories.data.entity.Record;
import com.margosha.kse.calories.business.dto.RecordResponseDto;

import java.util.UUID;

public class RecordMapper {

    public static Record toEntity(RecordRequestDto dto, UUID userId){
        Record record = new Record();
        record.setMealType(MealType.valueOf(dto.getMealType().name()));
        return record;

    }

    public static RecordResponseDto toDto(Record record){
        RecordResponseDto dto = new RecordResponseDto();
        dto.setId(record.getId());
        dto.setMealType(com.margosha.kse.calories.presentation.enums.MealType.valueOf(record.getMealType().name()));
        dto.setConsumedAt(record.getConsumedAt());
        return dto;

    }
}

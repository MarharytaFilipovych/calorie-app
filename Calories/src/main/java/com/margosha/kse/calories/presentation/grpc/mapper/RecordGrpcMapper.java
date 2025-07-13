package com.margosha.kse.calories.presentation.grpc.mapper;

import com.margosha.kse.calories.business.dto.*;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.presentation.enums.MealType;
import com.margosha.kse.calories.proto.Record;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {CommonGrpcMapper.class, ProductGrpcMapper.class})
public interface RecordGrpcMapper {

    @Mapping(target = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "productsList", source = "products")
    com.margosha.kse.calories.proto.Record toProto(RecordResponseDto recordDto);

    @Mapping(target = "productRecords", source = "productsList")
    RecordRequestDto fromProtoInput(RecordInput recordInput);

    default com.margosha.kse.calories.proto.MealType mealTypeToProto(MealType mealType) {
        if (mealType == null) return com.margosha.kse.calories.proto.MealType.BREAKFAST;
        return switch (mealType) {
            case BREAKFAST -> com.margosha.kse.calories.proto.MealType.BREAKFAST;
            case LUNCH -> com.margosha.kse.calories.proto.MealType.LUNCH;
            case DINNER -> com.margosha.kse.calories.proto.MealType.DINNER;
            case FIRST_SNACK -> com.margosha.kse.calories.proto.MealType.FIRST_SNACK;
            case SECOND_SNACK -> com.margosha.kse.calories.proto.MealType.SECOND_SNACK;
            case THIRD_SNACK -> com.margosha.kse.calories.proto.MealType.THIRD_SNACK;
        };
    }

    default List<Record> toProtoRecords(List<RecordResponseDto> records) {
        if (records == null || records.isEmpty()) return new ArrayList<>();
        return records.stream()
                .map(this::toProto)
                .toList();
    }


    default MealType protoToMealType(com.margosha.kse.calories.proto.MealType mealType) {
        if (mealType == null) return MealType.BREAKFAST;
        return switch (mealType) {
            case BREAKFAST -> MealType.BREAKFAST;
            case LUNCH -> MealType.LUNCH;
            case DINNER -> MealType.DINNER;
            case FIRST_SNACK -> MealType.FIRST_SNACK;
            case SECOND_SNACK -> MealType.SECOND_SNACK;
            case THIRD_SNACK -> MealType.THIRD_SNACK;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Unknown meal type: " + mealType);
        };
    }
}
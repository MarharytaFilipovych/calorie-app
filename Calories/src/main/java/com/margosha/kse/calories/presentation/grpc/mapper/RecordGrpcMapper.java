package com.margosha.kse.calories.presentation.grpc.mapper;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.presentation.enums.MealType;
import com.margosha.kse.calories.proto.Record;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecordGrpcMapper {

    private final CommonGrpcMapper commonGrpcMapper;
    private final ProductGrpcMapper productGrpcMapper;


    public Record toProto(RecordResponseDto dto) {
        if (dto == null) return null;

        Record.Builder builder = Record.newBuilder()
                .setId(commonGrpcMapper.uuidToString(dto.getId()))
                .setConsumedAt(commonGrpcMapper.localDateTimeToTimestamp(dto.getConsumedAt()))
                .setMealType(mealTypeToProto(dto.getMealType()))
                .setCaloriesConsumed(dto.getCaloriesConsumed())
                .setTotalQuantity(dto.getTotalQuantity())
                .setTotalProteins(dto.getTotalProteins())
                .setTotalFats(dto.getTotalFats())
                .setTotalCarbohydrates(dto.getTotalCarbohydrates());

        if (dto.getProducts() != null && !dto.getProducts().isEmpty()) {
            List<ProductRecordResponse> protoProducts =
                    productGrpcMapper.toProtoProductRecords(dto.getProducts());
            builder.addAllProducts(protoProducts);
        }
        return builder.build();
    }

    public List<Record> toProtoRecords(List<RecordResponseDto> records) {
        if (records == null || records.isEmpty()) return new ArrayList<>();
        return records.stream().map(this::toProto).collect(Collectors.toList());
    }

    public RecordRequestDto toDto(RecordInput input) {
        if (input == null) return null;
        RecordRequestDto dto = new RecordRequestDto();
        dto.setProductRecords(productGrpcMapper.fromProtoProductRecords(input.getProductsList()));
        dto.setMealType(protoToMealType(input.getMealType()));
        return dto;
    }

    public com.margosha.kse.calories.proto.MealType mealTypeToProto(MealType mealType) {
        if (mealType == null) return com.margosha.kse.calories.proto.MealType.BREAKFAST;
        return com.margosha.kse.calories.proto.MealType.valueOf(mealType.name());
    }

    public MealType protoToMealType(com.margosha.kse.calories.proto.MealType mealType) {
        if (mealType == null) return MealType.BREAKFAST;
        return MealType.valueOf(mealType.name());
    }
}

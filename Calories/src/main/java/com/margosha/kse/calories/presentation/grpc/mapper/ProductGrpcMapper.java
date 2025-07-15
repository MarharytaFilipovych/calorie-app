package com.margosha.kse.calories.presentation.grpc.mapper;

import com.margosha.kse.calories.business.dto.*;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInRequestDto;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInResponseDto;
import com.margosha.kse.calories.proto.*;
import com.margosha.kse.calories.presentation.enums.MeasurementUnit;
import com.margosha.kse.calories.proto.Record;
import org.mapstruct.*;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {CommonGrpcMapper.class, BrandGrpcMapper.class})
public interface ProductGrpcMapper {

    @Mapping(target = "id", qualifiedByName = "uuidToString")
    @Mapping(target = "brand", source = "brand", qualifiedByName = "nullToDefaultBrand")
    @Mapping(target = "barcode", source = "barcode", qualifiedByName = "nullToEmptyString")
    Product toProto(ProductResponseDto productDto);

    @Mapping(target = "brandId", source = "brandId", qualifiedByName = "stringToUuid")
    ProductRequestDto toDto(ProductInput productInput);

    ProductRecordResponse toProtoProductRecord(ProductRecordInResponseDto productRecord);

    @Mapping(target = "productId", source = "productId", qualifiedByName = "stringToUuid")
    ProductRecordInRequestDto fromProtoProductRecord(ProductRecord productRecord);

    @Mapping(target = "brandName", source = "brand")
    ProductFilterDto toDto(StreamProductsRequest request);

    @Named("toProtoProductRecords")
    default List<ProductRecordResponse> toProtoProductRecords(Set<ProductRecordInResponseDto> productRecords) {
        if (productRecords == null || productRecords.isEmpty()) return new ArrayList<>();
        return productRecords.stream()
                .map(this::toProtoProductRecord)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    default Set<ProductRecordInRequestDto> fromProtoProductRecords(List<ProductRecord> productRecords) {
        if (productRecords == null || productRecords.isEmpty()) return new HashSet<>();
        return productRecords.stream()
                .map(this::fromProtoProductRecord)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    default com.margosha.kse.calories.proto.MeasurementUnit measurementUnitToProto(MeasurementUnit unit) {
        if (unit == null) return com.margosha.kse.calories.proto.MeasurementUnit.GRAMS;
        return com.margosha.kse.calories.proto.MeasurementUnit.valueOf(unit.name());
    }

    default MeasurementUnit protoToMeasurementUnit(com.margosha.kse.calories.proto.MeasurementUnit unit) {
        if (unit == null) return MeasurementUnit.GRAMS;
        return MeasurementUnit.valueOf(unit.name());
    }

}
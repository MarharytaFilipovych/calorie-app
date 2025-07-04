package com.margosha.kse.calories.presentation.graphql.mapper;

import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInRequestDto;
import com.margosha.kse.calories.presentation.graphql.input.UserInput;
import com.margosha.kse.calories.presentation.graphql.input.ProductInput;
import com.margosha.kse.calories.presentation.graphql.input.RecordInput;
import com.margosha.kse.calories.presentation.graphql.input.ProductRecordInput;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GraphQLInputMapper {
    
    @Mapping(target = "id", ignore = true)
    UserDto toDto(UserInput input);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "archived", ignore = true)
    ProductDto toDto(ProductInput input);
    
    RecordRequestDto toDto(RecordInput input);
    
    ProductRecordInRequestDto toDto(ProductRecordInput input);
}
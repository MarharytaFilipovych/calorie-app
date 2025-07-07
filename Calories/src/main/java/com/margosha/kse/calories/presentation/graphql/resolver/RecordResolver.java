package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInResponseDto;
import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.business.service.RecordService;
import com.margosha.kse.calories.data.entity.ProductRecord;
import com.margosha.kse.calories.data.entity.Record;
import com.margosha.kse.calories.presentation.model.Pagination;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
@Slf4j
@Component
public class RecordResolver {
    private final RecordService recordService;
    private final ProductService productService;

    public RecordResolver(RecordService recordService, ProductService productService) {
        this.recordService = recordService;
        this.productService = productService;
    }

    @QueryMapping
    public RecordResponseDto record(@Argument @org.hibernate.validator.constraints.UUID String userId,
                                    @Argument @org.hibernate.validator.constraints.UUID String id){
        return recordService.getConsumption(UUID.fromString(userId), UUID.fromString(id));
    }

    @QueryMapping
    public Page<RecordResponseDto> records(@Argument @org.hibernate.validator.constraints.UUID String userId,
                                           @Argument LocalDate date,
                                           @Argument @Valid Pagination pagination){
        return recordService.getRecords(UUID.fromString(userId), pagination.getLimit(),
                pagination.getOffset(), date, true);
    }

    /*@BatchMapping(typeName = "Record", field = "products")
    public Map<RecordResponseDto, List<ProductRecordInResponseDto>> products(List<RecordResponseDto> records) {
        log.debug("Batch loading products for {} records", records.size());
        Map<RecordResponseDto, List<ProductRecordInResponseDto>> result = productService.getProductsForRecords(records);
        log.debug("Loaded {} product records across {} records", result.size(), records.size());
        return result;
    }*/

    @MutationMapping
    public RecordResponseDto createRecord(@Argument @org.hibernate.validator.constraints.UUID String userId,
                                          @Argument @Valid RecordRequestDto input){
        return recordService.createRecord(UUID.fromString(userId), input);
    }

    @MutationMapping
    public RecordResponseDto updateRecord(@Argument @org.hibernate.validator.constraints.UUID String id,
                                          @Argument @org.hibernate.validator.constraints.UUID String userId,
                                          @Argument @Valid RecordRequestDto input){
        return recordService.updateRecord(UUID.fromString(userId), UUID.fromString(id), input);
    }

    @MutationMapping
    public Boolean deleteRecord(@Argument @org.hibernate.validator.constraints.UUID String id,
                                          @Argument @org.hibernate.validator.constraints.UUID String userId){
        return recordService.deleteRecord(UUID.fromString(userId), UUID.fromString(id));
    }
}

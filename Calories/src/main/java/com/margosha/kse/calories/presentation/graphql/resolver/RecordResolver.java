package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.business.dto.RecordRequestDto;
import com.margosha.kse.calories.business.dto.RecordResponseDto;
import com.margosha.kse.calories.business.dto.subdto.ProductRecordInResponseDto;
import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.business.service.RecordService;
import com.margosha.kse.calories.data.entity.User;
import com.margosha.kse.calories.presentation.model.Meta;
import com.margosha.kse.calories.presentation.model.Pagination;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class RecordResolver {
    private final RecordService recordService;
    private final ProductService productService;

    public RecordResolver(RecordService recordService, ProductService productService) {
        this.recordService = recordService;
        this.productService = productService;
    }

    @QueryMapping
    public RecordResponseDto record(@Argument UUID userId, @Argument UUID id){
        return recordService.getConsumption(userId, id);
    }

    @QueryMapping
    public Page<RecordResponseDto> records(@Argument @org.hibernate.validator.constraints.UUID String userId,
                                           @Argument LocalDate date,
                                           @Argument @Valid Pagination pagination){
        return recordService.getRecords(UUID.fromString(userId), pagination.getLimit(),
                pagination.getOffset(), date, false);
    }

    @SchemaMapping(typeName = "RecordPage", field = "content")
    public List<RecordResponseDto> content(Page<RecordResponseDto> page) {
        return page.getContent();
    }

    @SchemaMapping(typeName = "RecordPage", field = "meta")
    public Meta totalElements(Page<RecordResponseDto> page) {
        return new Meta(page);
    }

    @BatchMapping(typeName = "Record", field = "products")
    public Map<RecordResponseDto, List<ProductRecordInResponseDto>> products(List<RecordResponseDto> records) {
        return productService.getProductsForRecords(records);
    }

    @MutationMapping
    public RecordResponseDto createRecord(@Argument UUID userId, @Argument @Valid RecordRequestDto input){
        return recordService.createRecord(userId, input);
    }

    @MutationMapping
    public RecordResponseDto updateRecord(@Argument UUID id, @Argument UUID userId,
                                          @Argument @Valid RecordRequestDto input){
        return recordService.updateRecord(userId, id, input);
    }

    @MutationMapping
    public Boolean deleteRecord(@Argument UUID id, @Argument UUID userId){
        return recordService.deleteRecord(userId, id);
    }
}

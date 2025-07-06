package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.business.dto.ProductRequestDto;
import com.margosha.kse.calories.business.dto.ProductResponseDto;
import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.presentation.model.Pagination;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ProductResolver {
    private final ProductService productService;

    public ProductResolver(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public Page<ProductResponseDto> products(@Argument String name, @Argument @Valid Pagination pagination){
        return productService.getAll(name, pagination.getLimit(), pagination.getOffset());
    }

    @QueryMapping
    public ProductResponseDto product(@Argument @org.hibernate.validator.constraints.UUID String id){
        return productService.getById(UUID.fromString(id));
    }

    @MutationMapping
    public ProductResponseDto createProduct(@Argument @Valid ProductRequestDto input){
        return productService.create(input);
    }

    @MutationMapping
    public ProductResponseDto updateProduct(@Argument @org.hibernate.validator.constraints.UUID String id, @Argument @Valid ProductRequestDto input){
        return productService.updateProduct(input, UUID.fromString(id));
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument @org.hibernate.validator.constraints.UUID String id){
        return productService.delete(UUID.fromString(id));
    }
}

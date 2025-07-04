package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.presentation.graphql.input.ProductInput;
import com.margosha.kse.calories.presentation.graphql.mapper.GraphQLInputMapper;
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
    private final GraphQLInputMapper mapper;
    private final ProductService productService;

    public ProductResolver(GraphQLInputMapper mapper, ProductService productService) {
        this.mapper = mapper;
        this.productService = productService;
    }

    @QueryMapping
    public Page<ProductDto> products(@Argument String name, @Argument @Valid Pagination pagination){
        return productService.getAll(name, pagination.getLimit(), pagination.getOffset());
    }

    @QueryMapping
    public ProductDto product(@Argument @org.hibernate.validator.constraints.UUID String id){
        return productService.getById(UUID.fromString(id));
    }

    @MutationMapping
    public ProductDto createProduct(@Argument @Valid ProductInput input){
        return productService.create(mapper.toDto(input));
    }

    @MutationMapping
    public ProductDto updateProduct(@Argument @org.hibernate.validator.constraints.UUID String id, @Argument @Valid ProductInput input){
        return productService.updateProduct(mapper.toDto(input), UUID.fromString(id));
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument @org.hibernate.validator.constraints.UUID String id){
        return productService.delete(UUID.fromString(id));
    }
}

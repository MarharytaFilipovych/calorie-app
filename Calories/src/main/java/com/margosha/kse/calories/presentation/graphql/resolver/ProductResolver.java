package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.business.dto.ProductRequestDto;
import com.margosha.kse.calories.business.dto.ProductResponseDto;
import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.presentation.model.Meta;
import com.margosha.kse.calories.presentation.model.Pagination;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
public class ProductResolver {
    private final ProductService productService;

    public ProductResolver(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public ProductResponseDto product(@Argument UUID id) {
        return productService.getById(id);
    }

    @QueryMapping
    public Page<ProductResponseDto> products(@Argument String name, @Argument Pagination pagination) {
        if (pagination == null) pagination = new Pagination();
        return productService.getAll(name, pagination.getLimit(), pagination.getOffset());
    }

    @SchemaMapping(typeName = "ProductPage", field = "content")
    public List<ProductResponseDto> content(Page<ProductResponseDto> page) {
        return page.getContent();
    }

    @SchemaMapping(typeName = "ProductPage", field = "meta")
    public Meta totalElements(Page<ProductResponseDto> page) {
        return new Meta(page);
    }

    @MutationMapping
    public ProductResponseDto createProduct(@Argument @Valid ProductRequestDto input) {
        return productService.create(input);
    }

    @MutationMapping
    public ProductResponseDto updateProduct(@Argument UUID id, @Argument @Valid ProductRequestDto input) {
        return productService.updateProduct(input, id);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument UUID id) {
        return productService.delete(id);
    }
}
package com.margosha.kse.calories.presentation.controller;

import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.presentation.model.Meta;
import com.margosha.kse.calories.presentation.model.Pagination;
import com.margosha.kse.calories.presentation.annotations.CorrectName;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@Validated
@Tag(name = "Products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> createProduct(
            @Parameter(description = "Product information including nutritional values")
            @Valid @RequestBody ProductDto productDto){
        UUID id = productService.create(productDto).getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @Parameter(description = "Search products by name (case-insensitive partial match)")
            @RequestParam(required = false) @CorrectName(required = false) String name,
            @Parameter(description = "Pagination parameters")
            @ParameterObject Pagination pagination) {
        Page<ProductDto> result = productService.getAll(name, pagination.getLimit(), pagination.getOffset());
        return ResponseEntity.ok(Map.of(
                "meta" , new Meta(result),
                "products", result.getContent()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(
            @Parameter(description = "Product unique identifier")
            @PathVariable UUID id) {
        ProductDto dto = productService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(
            @Parameter(description = "Updated product information")
            @Valid @RequestBody ProductDto productDto,
            @Parameter(description = "Product unique identifier")
            @PathVariable UUID id ){
        productService.updateProduct(productDto, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product unique identifier")
            @PathVariable UUID id ){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
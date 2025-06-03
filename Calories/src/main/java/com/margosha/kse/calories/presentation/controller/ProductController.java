package com.margosha.kse.calories.presentation.controller;

import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.presentation.model.Meta;
import com.margosha.kse.calories.presentation.model.Pagination;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(required = false) String name, @Validated Pagination pagination) {
        Page<ProductDto> result = productService.getAll(name, pagination.getLimit(), pagination.getOffset());
        return ResponseEntity.ok(Map.of(
                "meta" , new Meta(pagination.getLimit(), result.getTotalElements(), pagination.getOffset(), result.getTotalPages()),
                "products", result.getContent()
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> createProduct(@Valid @RequestBody ProductDto productDto){
        UUID id = productService.create(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        ProductDto dto = productService.getById(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateProduct(@Valid @RequestBody ProductDto productDto, @PathVariable UUID id ){
        productService.updateProduct(productDto, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id ){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }}

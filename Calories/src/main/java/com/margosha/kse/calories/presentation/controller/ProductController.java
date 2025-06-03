package com.margosha.kse.calories.presentation.controller;

import com.margosha.kse.calories.business.service.ProductService;
import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.presentation.model.Meta;
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
            @RequestParam(required = false) String name,

            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 100, message = "Limit cannot exceed 100")
            Integer limit,

            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Offset must be at least 1")
            Integer offset) {
        Page<ProductDto> result = productService.getAll(name, limit, offset);
        return ResponseEntity.ok(Map.of(
                "meta" , new Meta(offset, result.getTotalElements(), limit, result.getTotalPages()),
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
        productService.uodate(productDto, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id ){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }}

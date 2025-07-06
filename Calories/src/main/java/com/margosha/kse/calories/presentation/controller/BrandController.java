package com.margosha.kse.calories.presentation.controller;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.business.dto.UserDto;
import com.margosha.kse.calories.business.service.BrandService;
import com.margosha.kse.calories.business.service.UserService;
import com.margosha.kse.calories.presentation.annotations.CorrectName;
import com.margosha.kse.calories.presentation.model.Meta;
import com.margosha.kse.calories.presentation.model.Pagination;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/brands")
@Validated
@Tag(name = "Brands")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getBrands(
            @Parameter(description = "Pagination parameters")
            @ParameterObject Pagination pagination) {
        Page<BrandDto> result = brandService.getAllBrands(pagination.getLimit(), pagination.getOffset());
        return ResponseEntity.ok(Map.of(
                "meta" , new Meta(result),
                "users", result.getContent()
        ));
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> createBrand(
            @Parameter(description = "Brand data")
            @Valid @RequestBody BrandDto brandDto){
        UUID id = brandService.createBrand(brandDto).getId();
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandDto> getBrandById(
            @Parameter(description = "Brand unique identifier")
            @PathVariable UUID id) {
        BrandDto dto = brandService.getBrandById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<BrandDto> getBrandByName(
            @Parameter(description = "Brand name")
            @PathVariable @CorrectName(message = "Please, provide valid name!") String name) {
        BrandDto dto = brandService.getBrandByName(name);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateBrand(
            @Parameter(description = "Updated brand information")
            @Valid @RequestBody BrandDto brandDto,
            @Parameter(description = "Brand unique identifier")
            @PathVariable UUID id ){
        brandService.updateBrand(brandDto, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(
            @Parameter(description = "Brand unique identifier")
            @PathVariable UUID id ){
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}

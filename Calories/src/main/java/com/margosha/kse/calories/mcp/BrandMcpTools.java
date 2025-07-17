package com.margosha.kse.calories.mcp;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.business.service.BrandService;
import com.margosha.kse.calories.presentation.model.PageResponse;
import com.margosha.kse.calories.presentation.model.Pagination;
import lombok.AllArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@AllArgsConstructor
public class BrandMcpTools {
    
    private final BrandService brandService;

    @Tool(description = "Get a list of all brands with pagination")
    public PageResponse<BrandDto> getBrands(
            @ToolParam(description = "Pagination parameters with limit and offset", required = false)
            Pagination pagination) {
        if(pagination == null)pagination = new Pagination();
        Page<BrandDto> page = brandService.getAllBrands(pagination.getLimit(), pagination.getOffset());
        return PageResponse.from(page);
    }
    
    @Tool(description = "Get detailed information about a specific brand by ID")
    public BrandDto getBrandById(
            @ToolParam(description = "Brand unique identifier (UUID)") UUID brandId) {
        return brandService.getBrandById(brandId);
    }
    
    @Tool(description = "Get brand information by name")
    public BrandDto getBrandByName(@ToolParam(description = "Brand name") String name) {
        return brandService.getBrandByName(name);
    }
    
    @Tool(description = "Create a new brand")
    public BrandDto createBrand(
        @ToolParam(description = "Brand information with name and optional description") 
        BrandDto brandRequest) {
        return brandService.createBrand(brandRequest);
    }
    
    @Tool(description = "Update an existing brand")
    public BrandDto updateBrand(
        @ToolParam(description = "Brand unique identifier (UUID)") UUID brandId,
        @ToolParam(description = "Updated brand information") BrandDto brandRequest) {
        return brandService.updateBrand(brandRequest, brandId);
    }
    
    @Tool(description = "Delete a brand from the system")
    public boolean deleteBrand(
            @ToolParam(description = "Brand unique identifier (UUID)") UUID brandId) {
        return brandService.deleteBrand(brandId);
    }
}
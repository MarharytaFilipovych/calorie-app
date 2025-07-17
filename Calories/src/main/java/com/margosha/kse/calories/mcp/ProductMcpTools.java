package com.margosha.kse.calories.mcp;

import com.margosha.kse.calories.business.dto.ProductRequestDto;
import com.margosha.kse.calories.business.dto.ProductResponseDto;
import com.margosha.kse.calories.business.service.ProductService;
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
public class ProductMcpTools {

    private final ProductService productService;

    @Tool(description = "Get a list of products with optional name filtering and pagination")
    public PageResponse<ProductResponseDto> getProducts(
            @ToolParam(description = "Pagination parameters with limit and offset", required = false) Pagination pagination,
            @ToolParam(description = "Optional product name filter", required = false) String name) {
        if(pagination == null)pagination = new Pagination();
        Page<ProductResponseDto> page = productService.getAll(name, pagination.getLimit(), pagination.getOffset());
        return PageResponse.from(page);
    }

    @Tool(description = "Get detailed information about a specific product by ID")
    public ProductResponseDto getProductById(
            @ToolParam(description = "Product unique identifier (UUID)") UUID productId) {
        return productService.getById(productId);
    }

    @Tool(description = "Create a new product in the system with full nutritional information")
    public ProductResponseDto createProduct(
            @ToolParam(description = "Product data including name, nutritional info, measurement unit, and optional brand ID")
            ProductRequestDto productRequest) {
        return productService.create(productRequest);
    }

    @Tool(description = "Update an existing product with new information")
    public ProductResponseDto updateProduct(
            @ToolParam(description = "Product unique identifier (UUID)") UUID productId,
            @ToolParam(description = "Updated product information (provide any fields you want to update)")
            ProductRequestDto productRequest) {
        return productService.updateProduct(productRequest, productId);
    }

    @Tool(description = "Delete a product from the system")
    public boolean deleteProduct(
            @ToolParam(description = "Product unique identifier (UUID)") UUID productId) {
        return productService.delete(productId);
    }
}
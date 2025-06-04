package com.margosha.kse.calories.business.service;

import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.business.mapper.ProductMapper;
import com.margosha.kse.calories.data.entity.Product;
import com.margosha.kse.calories.data.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public Page<ProductDto> getAll(String name, int limit, int offset){
        Pageable pageable = PageRequest.of(offset - 1, limit);
        Page<Product> productPage;
        if(name == null || name.isBlank())productPage = productRepository.findAll(pageable);
        else productPage =  productRepository.findProductByNameContainingIgnoreCase(name, pageable);
        return productPage.map(productMapper::toDto);
    }

    public UUID create(ProductDto dto){
        if(dto.getBarcode() != null && productRepository.existsByBarcode(dto.getBarcode()))
            throw new IllegalArgumentException("Product with a barcode " + dto.getBarcode() + " already exits!");
        Product product = productRepository.save(productMapper.toEntity(dto));
        return product.getId();
    }

    public ProductDto getById(UUID id){
        return productRepository.findById(id).map(productMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public void updateProduct(ProductDto dto, UUID id){
        if (!productRepository.existsById(id)) throw new EntityNotFoundException(id.toString());
        Product updatedProduct = productMapper.toEntity(dto);
        updatedProduct.setId(id);
        productRepository.save(updatedProduct);
    }

    public void delete(UUID id){
        productRepository.deleteById(id);
    }
}

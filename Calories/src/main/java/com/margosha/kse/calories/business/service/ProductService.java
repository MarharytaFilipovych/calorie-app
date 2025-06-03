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

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<ProductDto> getAll(String name, int limit, int offset){
        Pageable pageable = PageRequest.of(offset - 1, limit);
        Page<Product> productPage;
        if(name == null || name.isBlank())productPage = productRepository.findAll(pageable);
        else productPage =  productRepository.findProductByNameContainingIgnoreCase(name, pageable);
        return productPage.map(ProductMapper::toDto);
    }

    public UUID create(ProductDto dto){
        Product product = productRepository.save(ProductMapper.toEntity(dto));
        return product.getId();
    }

    public ProductDto getById(UUID id){
        return productRepository.findById(id).map(ProductMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public void uodate(ProductDto dto, UUID id){
        if (!productRepository.existsById(id)) throw new EntityNotFoundException(id.toString());
        Product updatedProduct = ProductMapper.toEntity(dto);
        updatedProduct.setId(id);
        productRepository.save(updatedProduct);
    }

    public void delete(UUID id){
        productRepository.deleteById(id);
    }
}

package com.margosha.kse.calories.business.service;

import com.margosha.kse.calories.business.dto.ProductDto;
import com.margosha.kse.calories.business.mapper.ProductMapper;
import com.margosha.kse.calories.data.entity.Product;
import com.margosha.kse.calories.data.repository.ProductRecordRepository;
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
    private final ProductRecordRepository productRecordRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, ProductRecordRepository productRecordRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productRecordRepository = productRecordRepository;
    }

    public Page<ProductDto> getAll(String name, int limit, int offset){
        Pageable pageable = PageRequest.of(offset - 1, limit);
        Page<Product> productPage;
        if(name == null || name.isBlank())productPage = productRepository.findByArchivedFalse(pageable);
        else productPage =  productRepository.findProductByNameContainingIgnoreCaseAndArchivedIsFalse(name, pageable);
        return productPage.map(productMapper::toDto);
    }

    public ProductDto create(ProductDto dto){
        if(dto.getBarcode() != null && productRepository.existsByBarcodeAndArchivedIsFalse(dto.getBarcode()))
            throw new IllegalArgumentException("Product with a barcode " + dto.getBarcode() + " already exits!");
        Product product = productRepository.save(productMapper.toEntity(dto));
        return productMapper.toDto(product);
    }

    public ProductDto getById(UUID id){
        return productRepository.findByIdAndArchivedIsFalse(id).map(productMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public ProductDto updateProduct(ProductDto dto, UUID id){
        if (!productRepository.existsByIdAndArchivedIsFalse(id)) throw new EntityNotFoundException(id.toString());
        Product updatedProduct = productMapper.toEntity(dto);
        updatedProduct.setId(id);
        return productMapper.toDto(productRepository.save(updatedProduct));
    }

    public boolean delete(UUID id){
        if(!productRepository.existsById(id))return false;
        if(productRecordRepository.existsByProduct_Id(id))
        {
            productRepository.findById(id).ifPresent(product -> {
                product.setArchived(true);
                productRepository.save(product);
            });
        }
        else productRepository.deleteById(id);
        return true;
    }
}

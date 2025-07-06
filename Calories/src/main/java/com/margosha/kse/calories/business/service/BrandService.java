package com.margosha.kse.calories.business.service;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.business.mapper.BrandMapper;
import com.margosha.kse.calories.data.entity.Brand;
import com.margosha.kse.calories.data.repository.BrandRepository;
import com.margosha.kse.calories.data.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    private final ProductRepository productRepository;

    public BrandService(BrandRepository brandRepository, BrandMapper brandMapper, ProductRepository productRepository) {
        this.brandRepository = brandRepository;
        this.brandMapper = brandMapper;
        this.productRepository = productRepository;
    }

    public Page<BrandDto> getAllBrands(int limit, int offset){
        return brandRepository.findAll(PageRequest.of(offset - 1, limit)).map(brandMapper::toDto);
    }

    public BrandDto createBrand(BrandDto dto){
        if(brandRepository.existsByName((dto.getName())))throw new IllegalArgumentException("Brand with a name " + dto.getName() + " already exits!");
        Brand user = brandRepository.save(brandMapper.toEntity(dto));
        return brandMapper.toDto(user);
    }

    public BrandDto getBrandById(UUID id){
        return brandRepository.findById(id).map(brandMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(id.toString()));
    }

    public BrandDto getBrandByName(String name){
        return brandRepository.getBrandByName(name).map(brandMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Brand with email " + name + " was not found!"));
    }

    public BrandDto updateBrand(BrandDto dto, UUID id){
        if (!brandRepository.existsById(id)) throw new EntityNotFoundException(id.toString());
        Brand updatedBrand = brandMapper.toEntity(dto);
        updatedBrand.setId(id);
        return brandMapper.toDto(brandRepository.save(updatedBrand));
    }

    @Transactional
    public boolean deleteBrand(UUID id){
        Optional<Brand> brand = brandRepository.findById(id);
        if(brand.isEmpty())return false;
        brand.get().getProducts().forEach(product -> {
            product.setBrand(null);
            productRepository.save(product);
        });
        brandRepository.deleteById(id);
        return true;
    }
}

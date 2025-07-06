package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.business.service.BrandService;
import com.margosha.kse.calories.presentation.model.Pagination;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class BrandResolver {
    private final BrandService brandService;

    public BrandResolver(BrandService brandService) {
        this.brandService = brandService;
    }

    @QueryMapping
    public Page<BrandDto> brands(@Argument @Valid Pagination pagination){
        return brandService.getAllBrands(pagination.getLimit(), pagination.getOffset());
    }

    @QueryMapping
    public BrandDto brand(@Argument @org.hibernate.validator.constraints.UUID String id){
        return brandService.getBrandById(UUID.fromString(id));
    }

    @QueryMapping
    public BrandDto brandByName(@Argument String name){
        return brandService.getBrandByName(name);
    }

    @MutationMapping
    public BrandDto createBrand(@Argument @Valid BrandDto input){
        return brandService.createBrand(input);
    }

    @MutationMapping
    public BrandDto updateBrand(@Argument @org.hibernate.validator.constraints.UUID String id, @Argument @Valid BrandDto input){
        return brandService.updateBrand(input, UUID.fromString(id));
    }

    @MutationMapping
    public Boolean deleteBrand(@Argument @org.hibernate.validator.constraints.UUID String id){
        return brandService.deleteBrand(UUID.fromString(id));
    }
}

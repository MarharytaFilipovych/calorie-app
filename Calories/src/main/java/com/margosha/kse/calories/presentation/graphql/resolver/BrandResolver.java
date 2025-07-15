package com.margosha.kse.calories.presentation.graphql.resolver;

import com.margosha.kse.calories.business.dto.BrandDto;
import com.margosha.kse.calories.business.service.BrandService;
import com.margosha.kse.calories.presentation.model.Meta;
import com.margosha.kse.calories.presentation.model.Pagination;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
public class BrandResolver {
    private final BrandService brandService;

    public BrandResolver(BrandService brandService) {
        this.brandService = brandService;
    }

    @QueryMapping
    public Page<BrandDto> brands(@Argument @Valid Pagination pagination){
        if (pagination == null) pagination = new Pagination();
        return brandService.getAllBrands(pagination.getLimit(), pagination.getOffset());
    }

    @SchemaMapping(typeName = "BrandPage", field = "meta")
    public Meta meta(Page<BrandDto> page) {
        return new Meta(page);
    }

    @SchemaMapping(typeName = "BrandPage", field = "content")
    public List<BrandDto> content(Page<BrandDto> page) {
        return page.getContent();
    }

    @QueryMapping
    public BrandDto brand(@Argument UUID id){
        return brandService.getBrandById(id);
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
    public BrandDto updateBrand(@Argument UUID id, @Argument @Valid BrandDto input){
        return brandService.updateBrand(input, id);
    }

    @MutationMapping
    public Boolean deleteBrand(@Argument UUID id){
        return brandService.deleteBrand(id);
    }
}

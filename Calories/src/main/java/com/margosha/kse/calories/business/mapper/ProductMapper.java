package com.margosha.kse.calories.business.mapper;

import com.margosha.kse.calories.data.entity.Product;
import com.margosha.kse.calories.data.enums.MeasurementUnit;
import com.margosha.kse.calories.business.dto.ProductDto;

public class ProductMapper {

    public static Product toEntity(ProductDto productDto){
        if(productDto == null)return null;
        Product product =  new Product();
        product.setName(productDto.getName());
        product.setBarcode(productDto.getBarcode());
        product.setProteins(productDto.getProteins());
        product.setFats(productDto.getFats());
        product.setCarbohydrates(productDto.getCarbohydrates());
        product.setWater(productDto.getWater());
        product.setSalt(productDto.getSalt());
        product.setSugar(productDto.getSugar());
        product.setFiber(productDto.getFiber());
        product.setAlcohol(productDto.getAlcohol());
        product.setDescription(productDto.getDescription());
        product.setCalories(productDto.getCalories());
        product.setMeasurementUnit(MeasurementUnit.valueOf(productDto.getMeasurementUnit().name()));
        return product;
    }

    public static ProductDto toDto(Product product) {
        if(product == null) return null;
        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setBarcode(product.getBarcode());
        productDto.setProteins(product.getProteins());
        productDto.setFats(product.getFats());
        productDto.setCarbohydrates(product.getCarbohydrates());
        productDto.setWater(product.getWater());
        productDto.setSalt(product.getSalt());
        productDto.setSugar(product.getSugar());
        productDto.setFiber(product.getFiber());
        productDto.setAlcohol(product.getAlcohol());
        productDto.setDescription(product.getDescription());
        productDto.setCalories(product.getCalories());
        productDto.setMeasurementUnit(com.margosha.kse.calories.presentation.enums.MeasurementUnit.valueOf(product.getMeasurementUnit().name()));
        return productDto;
    }
}

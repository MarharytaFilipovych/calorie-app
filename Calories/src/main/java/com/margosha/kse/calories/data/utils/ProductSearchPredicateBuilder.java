package com.margosha.kse.calories.data.utils;

import com.margosha.kse.calories.data.entity.QProduct;
import com.querydsl.core.BooleanBuilder;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class ProductSearchPredicateBuilder {
    private final QProduct product = QProduct.product;
    private final BooleanBuilder predicate = new BooleanBuilder();

    public static ProductSearchPredicateBuilder create(){
        ProductSearchPredicateBuilder builder = new ProductSearchPredicateBuilder();
        builder.predicate.and(builder.product.archived.isFalse());
        return builder;
    }

    public ProductSearchPredicateBuilder withName(String name){
        Optional.ofNullable(name)
                .filter(StringUtils::hasText)
                .ifPresent(n -> predicate.and(product.name.containsIgnoreCase(n)));
        return this;
    }

    public ProductSearchPredicateBuilder withBrand(String brand){
        Optional.ofNullable(brand)
                .filter(StringUtils::hasText)
                .ifPresent(b -> predicate.and(product.brand.name.containsIgnoreCase(b)));
        return this;
    }

    public ProductSearchPredicateBuilder withMinCalories(Integer minCalories){
        Optional.ofNullable(minCalories)
                .ifPresent(mc -> predicate.and(product.calories.goe(mc)));
        return this;
    }

    public ProductSearchPredicateBuilder withMaxCalories(Integer maxCalories){
        Optional.ofNullable(maxCalories)
                .ifPresent(mc -> predicate.and(product.calories.loe(mc)));
        return this;
    }

    public ProductSearchPredicateBuilder withMeasurementUnit(com.margosha.kse.calories.presentation.enums.MeasurementUnit measurementUnit){
        Optional.ofNullable(measurementUnit)
                .ifPresent(m -> {
                    com.margosha.kse.calories.data.enums.MeasurementUnit dataEnum =
                            com.margosha.kse.calories.data.enums.MeasurementUnit.valueOf(m.name());
                    predicate.and(product.measurementUnit.eq(dataEnum));
                });
        return this;
    }

    public BooleanBuilder build(){
        return predicate;
    }
}

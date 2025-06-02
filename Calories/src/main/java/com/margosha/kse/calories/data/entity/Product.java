package com.margosha.kse.calories.data.entity;

import com.margosha.kse.calories.data.enums.MeasurementUnit;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true)
    private String barcode;

    @Column(nullable = false)
    private Double proteins;

    @Column(nullable = false)
    private Double fats;

    @Column(nullable = false)
    private Double carbohydrates;

    @Column(nullable = false)
    private Double water;

    @Column(nullable = false)
    private Double salt;

    @Column(nullable = false)
    private Double sugar;

    @Column(nullable = false)
    private Double fiber;

    @Column(nullable = false)
    private Double alcohol;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer calories;

    @Column(name = "measurement_unit", nullable = false)
    @Enumerated(EnumType.STRING)
    private MeasurementUnit measurementUnit;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Set<ProductRecord> productRecords = new HashSet<>();
}

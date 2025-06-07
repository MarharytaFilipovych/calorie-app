package com.margosha.kse.calories.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.margosha.kse.calories.data.enums.MeasurementUnit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"productRecords"})
@ToString(exclude = {"productRecords"})
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
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean archived = false;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    @JsonIgnore
    private Set<ProductRecord> productRecords = new HashSet<>();
}

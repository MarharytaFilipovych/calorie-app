package com.margosha.kse.calories.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "product_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"product", "record"})
@ToString(exclude = {"product", "record"})
public class ProductRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id", nullable = false)
    @JsonIgnore
    private Record record;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Double quantity;
}

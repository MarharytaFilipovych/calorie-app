package com.margosha.kse.calories.data.entity;

import com.margosha.kse.calories.data.enums.ActivityLevel;
import com.margosha.kse.calories.data.enums.Gender;
import com.margosha.kse.calories.data.enums.Goal;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"records"})
@ToString(exclude = {"records"})
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String telephone;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Double height;

    @Column(name="activity_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Goal goal;

    @Column(name = "target_weight")
    private Double targetWeight;

    @CreatedDate
    @Column(name = "registered_at", insertable = false)
    private LocalDateTime registeredAt;

    @LastModifiedDate
    @Column(name = "updated_at", updatable = false)
    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            targetEntity = Record.class,
            orphanRemoval = true)
    private Set<Record> records = new HashSet<>();
}

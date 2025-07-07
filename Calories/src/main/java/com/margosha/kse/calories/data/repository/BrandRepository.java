package com.margosha.kse.calories.data.repository;

import com.margosha.kse.calories.data.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    Optional<Brand> getBrandByName(String name);

    boolean existsByName(String name);
}

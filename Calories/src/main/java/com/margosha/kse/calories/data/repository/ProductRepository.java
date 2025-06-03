package com.margosha.kse.calories.data.repository;

import com.margosha.kse.calories.data.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findProductByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean findByBarcode(String barcode);
}

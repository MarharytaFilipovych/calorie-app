package com.margosha.kse.calories.data.repository;

import com.margosha.kse.calories.data.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.*;

public interface ProductRepository extends
        JpaRepository<Product, UUID>
        //QuerydslPredicateExecutor<Product>
        {

    Page<Product> findProductByNameContainingIgnoreCaseAndArchivedIsFalse(String name, Pageable pageable);

    boolean existsByBarcodeAndArchivedIsFalse(String barcode);

    boolean existsByIdAndArchivedIsFalse(UUID id);

    Optional<Product> findByIdAndArchivedIsFalse(UUID id);

    Page<Product> findByArchivedFalse(Pageable pageable);

    List<Product> findAllByIdInAndArchivedIsFalse(Set<UUID> ids);
}

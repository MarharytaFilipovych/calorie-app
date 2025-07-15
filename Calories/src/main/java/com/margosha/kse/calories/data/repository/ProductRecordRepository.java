package com.margosha.kse.calories.data.repository;

import com.margosha.kse.calories.data.entity.ProductRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProductRecordRepository extends JpaRepository<ProductRecord, UUID> {
    boolean existsByProduct_Id(UUID productId);

    List<ProductRecord> findByRecordIdIn(Collection<UUID> recordIds);
}

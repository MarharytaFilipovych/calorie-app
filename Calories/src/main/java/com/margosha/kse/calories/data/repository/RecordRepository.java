package com.margosha.kse.calories.data.repository;

import com.margosha.kse.calories.data.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
public interface RecordRepository extends JpaRepository<UUID, Record> {

    @Query("SELECT r FROM Record r "+
            "JOIN FETCH r.recordProducts rp "+
            "JOIN FETCH rp.product "+
            "WHERE r.user.id = :userId")
    List<Record> findByUserIdWithProducts(UUID userId);
}

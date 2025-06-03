package com.margosha.kse.calories.data.repository;

import com.margosha.kse.calories.data.entity.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface RecordRepository extends JpaRepository<Record, UUID> {

    Optional<Record> findByIdAndUser_Id(UUID id, UUID userId);

    void deleteByIdAndUser_Id(UUID id, UUID userId);

    @Query("""
        SELECT r.id FROM Record r
        WHERE r.user.id = :userId AND r.consumedAt BETWEEN :start AND :end
    """)
    Page<UUID> findIdsByUserIdAndDateTime(UUID userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    @Query("""
        SELECT r.id FROM Record r
        WHERE r.user.id = :userId
    """)
    Page<UUID> findIdsByUserId(UUID userId, Pageable pageable);

    @Query("""
        SELECT DISTINCT r FROM Record r
        JOIN FETCH r.productRecords rp
        JOIN FETCH rp.product
        WHERE r.id IN :ids
    """)
    List<Record> findAllByIdsWithProducts(List<UUID> ids);

    boolean existsByIdAndUser_Id(UUID id, UUID userId);

    @Query("""
        SELECT r FROM Record r
        JOIN FETCH r.productRecords rp
        JOIN FETCH rp.product
    """)
    Record findByIdWithProducts(UUID id);
}

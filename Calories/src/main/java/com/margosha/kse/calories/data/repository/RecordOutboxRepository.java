package com.margosha.kse.calories.data.repository;

import com.margosha.kse.calories.data.entity.RecordOutbox;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RecordOutboxRepository extends CrudRepository<RecordOutbox, UUID> {

    @Query(value = "SELECT DISTINCT record_id FROM record_outbox LIMIT :limit", nativeQuery = true)
    List<UUID> findDistinctRecordsIds(int limit);

    void deleteByRecordId(UUID recordId);
}

package com.margosha.kse.calories.data.repository;

import com.margosha.kse.calories.data.entity.RecordOutbox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RecordOutboxRepository extends CrudRepository<RecordOutbox, UUID> {

    @Query("SELECT DISTINCT r.recordId FROM RecordOutbox r")
    List<UUID> findDistinctRecordsIds(Pageable pageable);

    void deleteAllByRecordId(UUID recordId);
}

package com.margosha.kse.calories.data.repository;

import com.margosha.kse.calories.data.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecordRepository extends JpaRepository<UUID, Record> {
}

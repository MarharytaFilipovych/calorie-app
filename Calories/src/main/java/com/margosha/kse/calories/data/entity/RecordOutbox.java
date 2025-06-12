package com.margosha.kse.calories.data.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "record_outbox")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecordOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "record_id")
    private UUID recordId;

    public RecordOutbox(UUID recordId){
        this.recordId = recordId;
    }
}

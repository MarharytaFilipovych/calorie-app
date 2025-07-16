package com.margosha.kse.calorie_client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateRecordResult {
    private String recordId;
    private String message;
}

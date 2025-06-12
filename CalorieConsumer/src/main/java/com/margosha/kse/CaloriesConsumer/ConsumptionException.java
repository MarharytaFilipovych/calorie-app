package com.margosha.kse.CaloriesConsumer;

import com.margosha.kse.CaloriesConsumer.dto.RecordEventDto;

public class ConsumptionException extends RuntimeException {
    public ConsumptionException(RecordEventDto dto) {
        super("You won't get to it today! The record " + dto.toString() + " was not received successfully!");
    }
}

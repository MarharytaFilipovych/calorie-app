package com.margosha.kse.calorie_client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CreateUserResult {
    private String userId;
    private String message;
}

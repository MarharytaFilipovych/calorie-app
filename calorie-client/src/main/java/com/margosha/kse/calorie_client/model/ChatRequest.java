package com.margosha.kse.calorie_client.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public  class ChatRequest {
    @NotBlank(message = "Message cannot be blank")
    @Size(max = 2000, message = "Message too long (max 2000 characters)")
    private String message;
}
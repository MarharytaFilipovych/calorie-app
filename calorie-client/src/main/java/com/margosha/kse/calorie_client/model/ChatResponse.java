package com.margosha.kse.calorie_client.model;

import java.util.List;

public record ChatResponse(String message, boolean success, String error, long tokensUsed, List<String> functionsUsed) {
}
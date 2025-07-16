package com.margosha.kse.calorie_client.model;

public record UsageCheck(boolean allowed, String reason, long remainingTokens, long remainingRequests) {
}
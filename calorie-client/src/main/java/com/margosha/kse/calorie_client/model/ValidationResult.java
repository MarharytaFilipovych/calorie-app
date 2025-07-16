package com.margosha.kse.calorie_client.model;

public record ValidationResult(boolean safe, String reason, double riskScore) {
}
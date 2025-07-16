package com.margosha.kse.calorie_client.model;

import java.time.LocalDateTime;

public record UsageStatus(long tokensUsed, long tokenLimit, long requestsUsed, long requestLimit, LocalDateTime resetTime) {
}
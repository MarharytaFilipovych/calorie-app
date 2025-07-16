package com.margosha.kse.calorie_client.service;

import com.margosha.kse.calorie_client.config.LLMSettings;
import com.margosha.kse.calorie_client.model.UsageCheck;
import com.margosha.kse.calorie_client.model.UsageStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class TokenUsageTrackerService {
    private final LLMSettings llmSettings;
    private final AtomicLong tokensUsedThisMinute = new AtomicLong(0);
    private final AtomicLong requestsThisMinute  = new AtomicLong(0);
    private volatile LocalDateTime lastReset = LocalDateTime.now();

    public TokenUsageTrackerService(LLMSettings llmSettings) {
        this.llmSettings = llmSettings;
    }

    public synchronized UsageCheck canMakeRequest(int estimatedTokens){
        resetIfNeeded();

        long currentTokens = tokensUsedThisMinute.get();
        long currentRequests = requestsThisMinute.get();
        if (currentRequests >= llmSettings.getMax().getRequestsPerMinute()) {
            return new UsageCheck(false, "Request limit exceeded",
                    llmSettings.getMax().getTokensPerMinute() - currentTokens,
                    llmSettings.getMax().getRequestsPerMinute() - currentRequests);
        }
        if (currentTokens + estimatedTokens > llmSettings.getMax().getTokensPerMinute()) {
            return new UsageCheck(false, "Token limit would be exceeded",
                    llmSettings.getMax().getTokensPerMinute() - currentTokens,
                    llmSettings.getMax().getRequestsPerMinute() - currentRequests);
        }

        return new UsageCheck(true, "Request allowed",
                llmSettings.getMax().getTokensPerMinute() - currentTokens - estimatedTokens,
                llmSettings.getMax().getRequestsPerMinute() - currentRequests - 1);
    }

    private void resetIfNeeded(){
        if(lastReset.isBefore(LocalDateTime.now().minusMinutes(1))){
            resetCounters();
        }
    }

    @Scheduled(fixedRate = 60000)
    public synchronized void resetCounters(){
        tokensUsedThisMinute.set(0);
        requestsThisMinute.set(0);
        lastReset = LocalDateTime.now();
        log.debug("Token usage counters reset");
    }

    public UsageStatus getUsageStatus() {
        resetIfNeeded();
        return new UsageStatus(
                tokensUsedThisMinute.get(),
                llmSettings.getMax().getTokensPerMinute(),
                requestsThisMinute.get(),
                llmSettings.getMax().getRequestsPerMinute(),
                lastReset
        );
    }

    public synchronized void recordUsage(long tokensUsed) {
        resetIfNeeded();
        tokensUsedThisMinute.addAndGet(tokensUsed);
        requestsThisMinute.incrementAndGet();

        log.info("Token usage recorded: {} tokens. Total this minute: {}/{}, Requests: {}/{}",
                tokensUsed, tokensUsedThisMinute.get(), llmSettings.getMax().getTokensPerMinute(),
                requestsThisMinute.get(), llmSettings.getMax().getRequestsPerMinute());
    }

}

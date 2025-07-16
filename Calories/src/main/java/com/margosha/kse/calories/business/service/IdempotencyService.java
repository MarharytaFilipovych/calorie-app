package com.margosha.kse.calories.business.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class IdempotencyService {

    private final ConcurrentMap<String, ProcessedRequest> processedRequests = new ConcurrentHashMap<>();
    
    @Getter
    public static class ProcessedRequest {
        private final Object result;
        private final long timestamp;
        private final boolean success;
        
        public ProcessedRequest(Object result, boolean success) {
            this.result = result;
            this.success = success;
            this.timestamp = System.currentTimeMillis();
        }

    }

    public boolean isProcessed(String requestId) {
        return processedRequests.containsKey(requestId);
    }

    public ProcessedRequest getProcessedResult(String requestId) {
        return processedRequests.get(requestId);
    }
    

    public void storeResult(String requestId, Object result, boolean success) {
        ProcessedRequest processed = new ProcessedRequest(result, success);
        processedRequests.put(requestId, processed);
        
        log.debug("💾 Stored idempotent result for request: {}, success: {}", requestId, success);
        cleanupOldRequests();
    }

    private void cleanupOldRequests() {
        long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000);
        
        processedRequests.entrySet().removeIf(entry -> 
            entry.getValue().getTimestamp() < oneHourAgo
        );
    }


    public void clear() {
        processedRequests.clear();
        log.debug("🗑️ Cleared all idempotency records");
    }


    public int size() {
        return processedRequests.size();
    }
}
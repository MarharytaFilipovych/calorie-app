package com.margosha.kse.calorie_client.controller;

import com.margosha.kse.calorie_client.model.ChatRequest;
import com.margosha.kse.calorie_client.model.ChatResponse;
import com.margosha.kse.calorie_client.model.UsageStatus;
import com.margosha.kse.calorie_client.service.CalorieLLMService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {
    
    private final CalorieLLMService llmService;

    @PostMapping("/message")
    public Mono<ResponseEntity<ChatResponse>> sendMessage(@Valid @RequestBody ChatRequest request) {
        log.info("Received chat message: {}", request.getMessage());
        
        return llmService.processUserMessage(request.getMessage())
            .map(response -> {
                if (response.success()) {
                    log.info("Chat response generated successfully. Tokens used: {}, Functions: {}", 
                        response.tokensUsed(), response.functionsUsed());
                    return ResponseEntity.ok(response);
                } else {
                    log.warn("Chat response failed: {}", response.error());
                    return ResponseEntity.badRequest().body(response);
                }
            })
            .onErrorReturn(ResponseEntity.internalServerError().body(
                new ChatResponse(
                    "Internal server error occurred",
                    false,
                    "Server error",
                    0,
                    java.util.Collections.emptyList()
                )
            ));
    }
    
    @GetMapping("/usage")
    public ResponseEntity<UsageStatus> getUsageStatus() {
        return ResponseEntity.ok(llmService.getUsageStatus());
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat service is running");
    }
}
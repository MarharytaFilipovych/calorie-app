package com.margosha.kse.calorie_client.service;

import com.margosha.kse.calorie_client.config.LLMSettings;
import com.margosha.kse.calorie_client.model.*;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;
import com.openai.models.completions.CompletionUsage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalorieLLMService {

    private final OpenAIClient openAIClient;
    private final LLMSettings llmSettings;
    private final TokenUsageTrackerService tokenTracker;
    private final PromptInjectionGuardService injectionGuard;
    private final CalorieFunctionToolService functionTools;
    private final Supplier<ChatCompletionCreateParams.Builder> chatCompletionBuilderSupplier;

    public Mono<ChatResponse> processUserMessage(String userMessage) {
        return Mono.fromCallable(() -> {
            ValidationResult validation = injectionGuard.validateInput(userMessage);
            if (!validation.safe()) {
                log.warn("Blocked potentially unsafe input: {}", validation.reason());
                return new ChatResponse(
                        "I can't process that request. Please ask questions related to nutrition and calorie tracking.",
                        false,
                        "Input validation failed: " + validation.reason(),
                        0,
                        Collections.emptyList()
                );
            }

            int estimatedTokens = estimateTokens(userMessage);
            UsageCheck usageCheck = tokenTracker.canMakeRequest(estimatedTokens);
            if (!usageCheck.allowed()) {
                log.warn("Request blocked due to usage limits: {}", usageCheck.reason());
                return new ChatResponse(
                        "Service temporarily unavailable due to usage limits. Please try again later.",
                        false,
                        usageCheck.reason(),
                        0,
                        Collections.emptyList()
                );
            }

            String sanitizedMessage = injectionGuard.sanitizeInput(userMessage);

            try {
                return processWithOpenAI(sanitizedMessage);
            } catch (Exception e) {
                log.error("Error processing message with OpenAI", e);
                return new ChatResponse(
                        "Sorry, I encountered an error processing your request. Please try again.",
                        false,
                        e.getMessage(),
                        0,
                        Collections.emptyList()
                );
            }
        }).onErrorReturn(new ChatResponse(
                "An unexpected error occurred. Please try again.",
                false,
                "Internal error",
                0,
                Collections.emptyList()
        ));
    }

    private ChatResponse processWithOpenAI(String userMessage) {
        List<String> functionsUsed = new ArrayList<>();
        long totalTokensUsed = 0;

        try {
            log.info("Processing message: {}", userMessage);

            ChatCompletionCreateParams.Builder builder = chatCompletionBuilderSupplier.get();
            builder.addMessage(ChatCompletionUserMessageParam.builder().content(userMessage).build());

            int maxRounds = llmSettings.getMax().getRounds();
            int currentRound = 0;

            while (currentRound < maxRounds) {
                log.info("🔄 Starting round {} of function calling", currentRound + 1);

                ChatCompletion response = openAIClient.chat().completions()
                        .create(builder.build());

                totalTokensUsed += trackTokenUsage(response);

                ChatCompletionMessage assistantMessage = response.choices().get(0).message();
                log.info("Assistant message has {} tool calls",
                        assistantMessage.toolCalls().map(List::size).orElse(0));

                if (assistantMessage.toolCalls().isPresent() && !assistantMessage.toolCalls().get().isEmpty()) {

                    builder.addMessage(ChatCompletionAssistantMessageParam.builder()
                            .content(assistantMessage.content().orElse(""))
                            .toolCalls(assistantMessage.toolCalls().get())
                            .build());

                    executeToolCalls(builder, assistantMessage.toolCalls().get(), functionsUsed, currentRound);

                    currentRound++;
                    log.info("📝 Round {} completed. Functions executed: {}. Continuing to next round...",
                            currentRound, functionsUsed);
                } else {
                    return handleFinalResponse(assistantMessage, functionsUsed, totalTokensUsed, currentRound);
                }
            }

            log.warn("⚠️ Reached maximum function calling rounds ({}). Stopping execution.", maxRounds);
            return new ChatResponse(
                    "I processed your request but reached the maximum number of operations. The available results have been processed.",
                    true,
                    "Max rounds reached",
                    totalTokensUsed,
                    functionsUsed
            );

        } catch (Exception e) {
            log.error("Error in OpenAI processing: {}", e.getMessage(), e);
            return new ChatResponse(
                    "I encountered an error processing your request. Error: " + e.getMessage(),
                    false,
                    e.getMessage(),
                    totalTokensUsed,
                    functionsUsed
            );
        }
    }

    private Object callFunction(ChatCompletionMessageToolCall.Function function) {
        return switch (function.name()) {
            case "SearchProducts" -> functionTools.executeFunction("SearchProducts", function.arguments(SearchProducts.class));
            case "CreateUser" -> functionTools.executeFunction("CreateUser", function.arguments(CreateUser.class));
            case "GetUser" -> functionTools.executeFunction("GetUser", function.arguments(GetUser.class));
            case "CreateFoodRecord" -> functionTools.executeFunction("CreateFoodRecord", function.arguments(CreateFoodRecord.class));
            case "GetUserRecords" -> functionTools.executeFunction("GetUserRecords", function.arguments(GetUserRecords.class));
            case "GetSingleRecord" -> functionTools.executeFunction("GetSingleRecord", function.arguments(GetSingleRecord.class));
            case "UpdateFoodRecord" -> functionTools.executeFunction("UpdateFoodRecord", function.arguments(UpdateFoodRecord.class));
            case "DeleteFoodRecord" -> functionTools.executeFunction("DeleteFoodRecord", function.arguments(DeleteFoodRecord.class));
            default -> throw new IllegalArgumentException("Unknown function: " + function.name());
        };
    }

    public UsageStatus getUsageStatus() {
        return tokenTracker.getUsageStatus();
    }

    private int estimateTokens(String text) {
        return (text.length() / 4) + 500;
    }

    private long trackTokenUsage(ChatCompletion response) {
        long tokens = response.usage().map(CompletionUsage::totalTokens).orElse(0L);
        tokenTracker.recordUsage(tokens);
        return tokens;
    }

    private void executeToolCalls(ChatCompletionCreateParams.Builder builder,
                                  List<ChatCompletionMessageToolCall> toolCalls,
                                  List<String> functionsUsed,
                                  int round) {
        for (ChatCompletionMessageToolCall toolCall : toolCalls) {
            if (!toolCall.function().isValid()) continue;

            String functionName = toolCall.function().name();
            functionsUsed.add(functionName);
            log.info("🔗 Executing function {} in round {}: {}", functionName, round + 1, toolCall.function().arguments());

            try {
                Object result = callFunction(toolCall.function());
                builder.addMessage(ChatCompletionToolMessageParam.builder()
                        .toolCallId(toolCall.id())
                        .contentAsJson(result)
                        .build());
            } catch (Exception e) {
                log.error("❌ Error executing function {}: {}", functionName, e.getMessage(), e);
                builder.addMessage(ChatCompletionToolMessageParam.builder()
                        .toolCallId(toolCall.id())
                        .content("Function execution failed: " + e.getMessage())
                        .build());
            }
        }
    }

    private ChatResponse handleFinalResponse(ChatCompletionMessage assistantMessage,
                                             List<String> functionsUsed,
                                             long totalTokensUsed,
                                             int round) {
        String content = assistantMessage.content().orElse("I processed your request successfully.");
        log.info("🎯 Function chaining completed after {} rounds. Total functions used: {}", round, functionsUsed);
        return new ChatResponse(content, true, null, totalTokensUsed, functionsUsed);
    }
}

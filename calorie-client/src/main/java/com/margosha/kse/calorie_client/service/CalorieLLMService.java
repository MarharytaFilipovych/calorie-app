package com.margosha.kse.calorie_client.service;

import com.margosha.kse.calorie_client.config.LLMSettings;
import com.margosha.kse.calorie_client.model.*;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.*;
import com.openai.models.completions.CompletionUsage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import com.openai.models.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalorieLLMService {
    private final OpenAIClient openAIClient;
    private final LLMSettings llmSettings;
    private final TokenUsageTrackerService tokenTracker;
    private final PromptInjectionGuardService injectionGuard;
    private final CalorieFunctionToolService functionTools;

    private static final String SYSTEM_PROMPT = """
        You are a helpful assistant for a calorie tracking application. You can help users:
        1. Search for food products
        2. Create user profiles for calorie tracking
        3. Get user information
        4. Record food consumption
        5. View consumption history
        
        Always be helpful, accurate, and focused on nutrition and health topics.
        When users ask for food recommendations, consider their goals and dietary needs.
        If users want to create records, make sure to get all required information.
        
        Important: Only respond to queries related to nutrition, food, and calorie tracking.
        Do not assist with unrelated topics or potentially harmful requests.
        """;

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
                })
                .onErrorReturn(new ChatResponse(
                        "An unexpected error occurred. Please try again.",
                        false,
                        "Internal error",
                        0,
                        Collections.emptyList()
                ));
    }

    private ChatResponse processWithOpenAI(String userMessage) {
        List<String> functionsUsed = new ArrayList<>();

        try {
            ChatCompletionCreateParams.Builder createParamsBuilder = ChatCompletionCreateParams.builder()
                    .model(ChatModel.of("gemma2-9b-it"))
                    .maxCompletionTokens(llmSettings.getMax().getTokens())
                    .temperature(llmSettings.getTemperature())
                    .addTool(SearchProducts.class)
                    .addTool(CreateUser.class)
                    .addTool(GetUser.class)
                    .addTool(CreateFoodRecord.class)
                    .addTool(GetUserRecords.class)
                    .addMessage(ChatCompletionSystemMessageParam.builder()
                            .content(SYSTEM_PROMPT)
                            .build())
                    .addMessage(ChatCompletionUserMessageParam.builder()
                            .content(userMessage)
                            .build());

            ChatCompletion response = openAIClient.chat().completions().create(createParamsBuilder.build());

            long totalTokens = response.usage()
                    .map(CompletionUsage::totalTokens)
                    .orElse(0L);
            tokenTracker.recordUsage(totalTokens);

            ChatCompletionMessage assistantMessage = response.choices().get(0).message();

            if (assistantMessage.toolCalls().isPresent() && !assistantMessage.toolCalls().get().isEmpty()) {
                return handleFunctionCalls(assistantMessage, createParamsBuilder, functionsUsed, totalTokens);
            }

            String content = assistantMessage.content().orElse("I couldn't generate a proper response.");

            return new ChatResponse(content, true, null, totalTokens, functionsUsed);

        } catch (Exception e) {
            log.error("Error in OpenAI processing", e);
            return new ChatResponse(
                    "I encountered an error processing your request. Please try a different question.",
                    false,
                    e.getMessage(),
                    0,
                    functionsUsed
            );
        }
    }

    private ChatResponse handleFunctionCalls(ChatCompletionMessage assistantMessage,
                                              ChatCompletionCreateParams.Builder createParamsBuilder,
                                              List<String> functionsUsed, long initialTokens) {

        createParamsBuilder.addMessage(ChatCompletionAssistantMessageParam.builder()
                .content(assistantMessage.content().orElse(""))
                .toolCalls(assistantMessage.toolCalls().orElse(Collections.emptyList()))
                .build());

        for (ChatCompletionMessageToolCall toolCall : assistantMessage.toolCalls().orElse(Collections.emptyList())) {
            if (toolCall.function().isValid()) {
                ChatCompletionMessageToolCall.Function function = toolCall.function();
                String functionName = function.name();
                functionsUsed.add(functionName);

                log.info("Executing function: {}", functionName);

                try {
                    Object result = callFunction(function);
                    createParamsBuilder.addMessage(ChatCompletionToolMessageParam.builder()
                            .toolCallId(toolCall.id())
                            .contentAsJson(result)
                            .build());

                } catch (Exception e) {
                    log.error("Error executing function {}: {}", functionName, e.getMessage());
                    createParamsBuilder.addMessage(ChatCompletionToolMessageParam.builder()
                            .toolCallId(toolCall.id())
                            .content("Function execution failed: " + e.getMessage())
                            .build());
                }
            }
        }
        try {
            ChatCompletion finalResponse = openAIClient.chat().completions()
                    .create(createParamsBuilder.build());

            long finalTokens = finalResponse.usage()
                    .map(CompletionUsage::totalTokens)
                    .orElse(0L);
            long totalTokens = initialTokens + finalTokens;

            log.info("👻Final response {}", finalResponse);
            String content = finalResponse.choices().get(0).message().content()
                    .orElse("I processed your request but couldn't generate a proper response.");

            return new ChatResponse(content, true, null, totalTokens, functionsUsed);

        } catch (Exception e) {
            log.error("Error getting final response", e);
            return new ChatResponse(
                    "I executed the requested functions but encountered an error generating the final response.",
                    false,
                    e.getMessage(),
                    initialTokens,
                    functionsUsed
            );
        }
    }


    private Object callFunction(ChatCompletionMessageToolCall.Function function) {
        return switch (function.name()) {
            case "SearchProducts" -> {
                SearchProducts searchProducts = function.arguments(SearchProducts.class);
                yield functionTools.executeFunction("SearchProducts", searchProducts);
            }
            case "CreateUser" -> {
                CreateUser createUser = function.arguments(CreateUser.class);
                yield functionTools.executeFunction("CreateUser", createUser);
            }
            case "GetUser" -> {
                GetUser getUser = function.arguments(GetUser.class);
                yield functionTools.executeFunction("GetUser", getUser);
            }
            case "CreateFoodRecord" -> {
                CreateFoodRecord createRecord = function.arguments(CreateFoodRecord.class);
                yield functionTools.executeFunction("CreateFoodRecord", createRecord);
            }
            case "GetUserRecords" -> {
                GetUserRecords getUserRecords = function.arguments(GetUserRecords.class);
                yield functionTools.executeFunction("GetUserRecords", getUserRecords);
            }
            default -> throw new IllegalArgumentException("Unknown function: " + function.name());
        };
    }

    public UsageStatus getUsageStatus() {
        return tokenTracker.getUsageStatus();
    }

    private int estimateTokens(String text) {
        return (text.length() / 4) + 500;
    }
}

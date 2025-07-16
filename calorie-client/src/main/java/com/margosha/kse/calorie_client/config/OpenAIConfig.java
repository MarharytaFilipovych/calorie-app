package com.margosha.kse.calorie_client.config;

import com.margosha.kse.calorie_client.model.*;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionSystemMessageParam;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Supplier;

@Configuration
@Slf4j
@AllArgsConstructor
public class OpenAIConfig {

    private final LLMSettings llmSettings;

    private static final String SYSTEM_PROMPT = """
        You are a nutrition assistant. You MUST provide detailed reports for EVERY function you execute.

        Available functions:
        1. SearchProducts - Find food products by name
        2. CreateUser - Create user profiles  
        3. GetUser - Get user information by ID
        4. CreateFoodRecord - Log food consumption
        5. GetUserRecords - View consumption history
        6. GetSingleRecord - Get specific record by ID
        7. UpdateFoodRecord - Update existing records
        8. DeleteFoodRecord - Delete records

        MANDATORY RESPONSE FORMAT:
        You MUST ALWAYS respond in this EXACT format when executing multiple functions:

        "I executed the following operations for you:

        🔸 Operation 1: [Function Name]
        ▪ Action: [What you did]
        ▪ Input: [Parameters used]
        ▪ Result: [Specific outcome]
        ▪ Details: [Additional information]

        🔸 Operation 2: [Function Name]
        ▪ Action: [What you did]
        ▪ Input: [Parameters used]
        ▪ Result: [Specific outcome]
        ▪ Details: [Additional information]

        🔸 Operation 3: [Function Name]
        ▪ Action: [What you did]
        ▪ Input: [Parameters used]
        ▪ Result: [Specific outcome]
        ▪ Details: [Additional information]

        📋 Summary: [Overall outcome and next steps]"

        CRITICAL RULES:
        1. You MUST report on EVERY SINGLE function you execute
        2. You MUST use the exact format above
        3. You MUST include specific details about each function's results
        4. You MUST NOT provide only the final function's result
        5. You MUST mention user IDs, product names, error messages, etc.

        EXAMPLE - FOLLOW THIS EXACTLY:
        User request: "create user Petya, then search for apple"

        Your response MUST be:
        "I executed the following operations for you:

        🔸 Operation 1: CreateUser
        ▪ Action: Created new user profile for Petya
        ▪ Input: firstName='Petya', email='petya@test.com', defaults applied
        ▪ Result: ✅ SUCCESS - User created with ID: d6acb7a3-0080-4760-bede-fab923febbf4
        ▪ Details: Set up with moderate activity level, maintain weight goal, height 180cm, weight 75kg

        🔸 Operation 2: SearchProducts  
        ▪ Action: Searched product database for 'apple'
        ▪ Input: name='apple', limit=20, page=1
        ▪ Result: ❌ NO RESULTS - No apple products found in database
        ▪ Details: Searched through available food products but no apple varieties are currently in the system

        📋 Summary: Successfully created Petya's account and searched for apples. The user account is ready for calorie tracking, but you may need to add apple products to the database or try searching for specific apple types."

        NEVER respond with just the final function result. ALWAYS include ALL function executions.
        """;

    @Bean
    public OpenAIClient openAIOkHttpClient() {
        return OpenAIOkHttpClient.builder()
                .apiKey(llmSettings.getApiKey())
                .baseUrl(llmSettings.getUrl())
                .build();
    }

    @Bean
    public Supplier<ChatCompletionCreateParams.Builder> chatCompletionBuilderSupplier() {
        return () -> {
            try {
                ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                        .model(ChatModel.of(llmSettings.getModel()))
                        .maxCompletionTokens(llmSettings.getMax().getTokens())
                        .temperature(llmSettings.getTemperature())
                        .addTool(SearchProducts.class)
                        .addTool(CreateUser.class)
                        .addTool(GetUser.class)
                        .addTool(CreateFoodRecord.class)
                        .addTool(GetUserRecords.class)
                        .addTool(GetSingleRecord.class)
                        .addTool(UpdateFoodRecord.class)
                        .addTool(DeleteFoodRecord.class)
                        .addMessage(ChatCompletionSystemMessageParam.builder()
                                .content(SYSTEM_PROMPT)
                                .build());

                log.info("All tools and system prompt added successfully");
                return builder;
            } catch (Exception e) {
                log.error("❌ Error creating chat completion builder: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to configure OpenAI chat completion builder", e);
            }
        };
    }
}

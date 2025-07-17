package com.margosha.kse.calories.config;

import com.margosha.kse.calories.mcp.BrandMcpTools;
import com.margosha.kse.calories.mcp.ProductMcpTools;
import com.margosha.kse.calories.mcp.RecordMcpTools;
import com.margosha.kse.calories.mcp.UserMcpTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {
    @Bean
    public ToolCallbackProvider calorieTools(
            ProductMcpTools productMcpTools,
            UserMcpTools userMcpTools,
            RecordMcpTools recordMcpTools,
            BrandMcpTools brandMcpTools){
        return MethodToolCallbackProvider.builder()
                .toolObjects(productMcpTools, userMcpTools,
                        recordMcpTools, brandMcpTools)
                .build();
    }
}

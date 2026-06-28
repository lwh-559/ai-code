package com.dorr.aicode.config;


import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: lwh
 * @date: 2026-06-28
 * @description: 深度推理模型配置
 */

@Data
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.streaming-chat-model")
public class ReasoningStreamingChatModelConfig {

    private String baseUrl;

    private String apiKey;

    private Integer maxTokens;

    /**
     * 推理流式模型（用于 vue 项目生成，带工具调用）
     * @return StreamingChatModel
     */
    @Bean
    public StreamingChatModel reasoningStreamingChatModel() {

        // 测试环境
        String modelName = "glm-4.5-air";
        // 生产环境
//        String modelName = "glm-4.7";
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}

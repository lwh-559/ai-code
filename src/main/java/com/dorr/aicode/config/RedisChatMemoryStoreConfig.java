package com.dorr.aicode.config;


import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: lwh
 * @date: 2026-06-27
 * @description: redis 会话记忆配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisChatMemoryStoreConfig {

    private String host;

    private int port;

    private String username;

    private String password;

    private long ttl;

    @Bean
    public RedisChatMemoryStore redisChatMemoryStore() {
        return RedisChatMemoryStore.builder()
                .host(host)
                .port(port)
                .user(username)
                .password(password)
                .prefix("ai-code-")
                .ttl(ttl)
                .build();
    }
}

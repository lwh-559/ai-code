package com.dorr.aicode.config;


import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.*;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: lwh
 * @date: 2026-06-29
 * @description: 阿里云对象存储配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliOSSConfig {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String domain;
    private String region;


    private OSS ossClient;

    @Bean
    public OSS ossClient() {
        // 使用 OSSClientBuilder 代替已弃用的 OSSClient 构造函数
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
        }
    }
}
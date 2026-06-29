package com.dorr.aicode.manager;


import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.dorr.aicode.config.AliOSSConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author: lwh
 * @date: 2026-06-29
 * @description: 阿里云 OSS 对象存储管理器
 */
@Slf4j
@Component
public class AliOSSManager {

    @Resource
    private AliOSSConfig aliOSSConfig;

    @Resource
    private OSS ossClient;

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(aliOSSConfig.getBucketName(), key, file);
        return ossClient.putObject(putObjectRequest);
    }

    /**
     * 上传文件到 OSS 并返回访问 URL
     *
     * @param key  OSS对象键（完整路径）
     * @param file 要上传的文件
     * @return 文件的访问URL，失败返回null
     */
    public String uploadFile(String key, File file) {
        // 上传文件
        PutObjectResult result = putObject(key, file);
        if (result != null) {
            // 构建访问URL
            String url = String.format("%s/%s", aliOSSConfig.getDomain(), key);
            log.info("文件上传COS成功: {} -> {}", file.getName(), url);
            return url;
        } else {
            log.error("文件上传COS失败，返回结果为空");
            return null;
        }
    }
}

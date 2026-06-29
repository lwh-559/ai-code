package com.dorr.aicode.service;


import jakarta.servlet.http.HttpServletResponse;

/**
 * @author: lwh
 * @date: 2026-06-29
 * @description: 项目代码下载 服务层。
 */

public interface ProjectDownloadService {

    /**
     * 下载项目代码为ZIP文件
     * @param projectPath 项目路径
     * @param downloadFileName 下载文件名
     * @param response HTTP响应
     */
    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}

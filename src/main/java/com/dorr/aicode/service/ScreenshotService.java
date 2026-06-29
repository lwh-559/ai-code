package com.dorr.aicode.service;


/**
 * @author: lwh
 * @date: 2026-06-29
 * @description: 截图 服务层。
 */

public interface ScreenshotService {

    /**
     * 生成并上传网页截图
     * @param webUrl 网页URL
     * @return 截图在对象存储中的URL
     */
    String generateAndUploadScreenshot(String webUrl);
}

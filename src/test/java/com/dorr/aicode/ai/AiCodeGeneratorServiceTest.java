package com.dorr.aicode.ai;

import com.dorr.aicode.ai.model.HtmlCodeResult;
import com.dorr.aicode.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult code = aiCodeGeneratorService.generateHtmlCode("做一个登录网页，不要超过100行代码");
        Assertions.assertNotNull(code);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult code = aiCodeGeneratorService.generateMultiFileCode("开发一个个人主页，不要超过100行代码");
        Assertions.assertNotNull(code);
    }
}
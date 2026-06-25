# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

AI 代码生成器 —— 用户输入自然语言描述，系统调用 LLM 生成可运行的前端页面代码（HTML/CSS/JS），并保存到本地。

## 常用命令

```bash
# 构建
mvn clean package

# 运行（端口 8123，context path /api）
mvn spring-boot:run

# 测试（需要可用的 LLM API 连接，测试会调用真实 AI 服务）
mvn test

# 运行单个测试类
mvn test -Dtest=AiCodeGeneratorServiceTest

# 运行单个测试方法
mvn test -Dtest=AiCodeGeneratorServiceTest#testMethod
```

## 技术栈

- Java 21 + Spring Boot 3.5.14
- LangChain4j 1.11.8（LLM 集成，OpenAI 兼容 API）
- MyBatis-Flex 1.11.7（ORM）
- MySQL + HikariCP
- Knife4j 4.4.0（API 文档，访问 /api/doc.html）
- Lombok、Hutool

## 核心架构

### AI 代码生成管线

这是应用的核心流程：**生成 → 解析 → 保存**

1. **`AiCodeGeneratorService`**（`ai/`）— LangChain4j 接口，通过 `@SystemMessage` 绑定 `resources/prompt/` 下的系统提示词。支持同步和流式两种模式，返回结构化结果（`HtmlCodeResult` / `MultiFileCodeResult`）。

2. **`AiCodeGeneratorServiceFactory`**（`ai/`）— Spring `@Configuration`，用 `AiServices` 构建代理 Bean。

3. **`AiCodeGeneratorFacade`**（`core/`）— 门面模式，编排完整管线：
   - 非流式：调用 AI → 解析结果 → 保存文件 → 返回目录路径
   - 流式：边推流边收集 → 流结束后解析并保存

4. **解析器**（`core/parser/`）— 策略模式。`CodeParserExecutor` 按 `CodeGenTypeEnum` 分派到 `HtmlCodeParser` 或 `MultiFileCodeParser`，用正则从 markdown 代码块中提取代码。

5. **保存器**（`core/saver/`）— 模板方法模式。`CodeFileSaverTemplate` 定义骨架（校验 → 建目录 → 写文件），具体实现保存到 `tmp/code_output/<type>_<snowflakeId>/`。

### 认证系统

- 基于 Session 的认证（`HttpSession`）
- `@AuthCheck` 注解 + `AuthInterceptor`（AOP）实现角色鉴权（user/admin）
- 密码使用 MD5 + 盐值 "dorr" 哈希

### 代码生成类型

通过 `CodeGenTypeEnum` 区分两种模式：
- `HTML` — 单文件，生成 `index.html`
- `MULTI_FILE` — 多文件，生成 `index.html` + `style.css` + `script.js`

## 配置说明

- `application.yaml` — 默认配置：MySQL `ai_code` 库、DeepSeek API、端口 8123
- `application-local.yaml` — 本地开发覆盖配置（不同 DB 和 LLM 端点），已加入 `.gitignore`
- 测试代码使用 `@SpringBootTest`，会连接真实 LLM 服务

## 目录约定

- 生成的代码文件输出到 `tmp/code_output/`（已 gitignore）
- 数据库建表脚本在 `sql/create_table.sql`
- 系统提示词模板在 `src/main/resources/prompt/`

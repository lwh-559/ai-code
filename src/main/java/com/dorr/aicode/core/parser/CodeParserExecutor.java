package com.dorr.aicode.core.parser;


import com.dorr.aicode.ai.model.enums.CodeGenTypeEnum;
import com.dorr.aicode.exception.BusinessException;
import com.dorr.aicode.exception.ErrorCode;

/**
 * @author: lwh
 * @date: 2026-06-24
 * @description: 代码解析执行器
 */
public class CodeParserExecutor {

    public static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();
    public static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    /**
     * 执行代码解析
     * @param codeContent 代码内容
     * @param codeGenType 代码生成类型
     * @return 解析结果（HtmlCodeResult 或 MultiFileCodeResult）
     */
    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML -> htmlCodeParser.parseCode(codeContent);
            case MULTI_FILE -> multiFileCodeParser.parseCode(codeContent);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型:"+codeGenType);
        };
    }
}

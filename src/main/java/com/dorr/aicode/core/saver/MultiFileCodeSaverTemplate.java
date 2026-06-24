package com.dorr.aicode.core.saver;


import cn.hutool.core.util.StrUtil;
import com.dorr.aicode.ai.model.MultiFileCodeResult;
import com.dorr.aicode.ai.model.enums.CodeGenTypeEnum;
import com.dorr.aicode.exception.BusinessException;
import com.dorr.aicode.exception.ErrorCode;

/**
 * @author: lwh
 * @date: 2026-06-24
 * @description: 多文件代码保存模板
 */

public class MultiFileCodeSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult>{

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult codeResult, String basePath) {
        // 保存HTML文件
        writeToFile(basePath, "index.html", codeResult.getHtmlCode());
        // 保存CSS文件
        writeToFile(basePath, "style.css", codeResult.getCssCode());
        // 保存JS文件
        writeToFile(basePath, "script.js", codeResult.getJsCode());
    }

    @Override
    protected void validateInput(MultiFileCodeResult codeResult) {
        super.validateInput(codeResult);
        // 至少要有 HTML 代码，CSS 和 JS 可以为空
        if (StrUtil.isBlank(codeResult.getHtmlCode())){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
        }
    }
}

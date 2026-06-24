package com.dorr.aicode.core.saver;


import cn.hutool.core.util.StrUtil;
import com.dorr.aicode.ai.model.HtmlCodeResult;
import com.dorr.aicode.ai.model.enums.CodeGenTypeEnum;
import com.dorr.aicode.exception.BusinessException;
import com.dorr.aicode.exception.ErrorCode;


/**
 * @author: lwh
 * @date: 2026-06-24
 * @description: HTML代码文件保存模板
 */

public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult>{

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult codeResult, String basePath) {
        // 保存HTML代码文件
        writeToFile(basePath, "index.html", codeResult.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult codeResult) {
        super.validateInput(codeResult);
        // HTML 代码不能为空
        if (StrUtil.isBlank(codeResult.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
        }
    }
}

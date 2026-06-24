package com.dorr.aicode.core.saver;


import com.dorr.aicode.ai.model.HtmlCodeResult;
import com.dorr.aicode.ai.model.MultiFileCodeResult;
import com.dorr.aicode.ai.model.enums.CodeGenTypeEnum;
import com.dorr.aicode.exception.BusinessException;
import com.dorr.aicode.exception.ErrorCode;

import java.io.File;

/**
 * @author: lwh
 * @date: 2026-06-24
 * @description: 代码文件保存执行器
 */
public class CodeFileSaverExecutor {

    public static final HtmlCodeFileSaverTemplate htmlCodeFileSaver = new HtmlCodeFileSaverTemplate();
    public static final MultiFileCodeSaverTemplate multiFileCodeSaver = new MultiFileCodeSaverTemplate();

    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeType) {
        return switch (codeType) {
            case HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) codeResult);
            case MULTI_FILE -> multiFileCodeSaver.saveCode((MultiFileCodeResult) codeResult);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: "+codeType);
        };
    }
}

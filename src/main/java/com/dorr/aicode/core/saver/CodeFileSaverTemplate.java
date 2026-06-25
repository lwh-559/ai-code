package com.dorr.aicode.core.saver;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.dorr.aicode.ai.model.enums.CodeGenTypeEnum;
import com.dorr.aicode.constant.AppConstant;
import com.dorr.aicode.exception.BusinessException;
import com.dorr.aicode.exception.ErrorCode;
import com.dorr.aicode.exception.ThrowUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * @author: lwh
 * @date: 2026-06-24
 * @description: 文件保存器 - 模板方法模式
 */

public abstract class CodeFileSaverTemplate<T> {

    // 应用生成根目录（用于浏览）
    private static final String FILE_SAVE_ROOT_DIR = AppConstant.CODE_OUTPUT_ROOT_DIR;


    public final File saveCode(T codeResult, Long appId) {
        // 1、验证输入
        validateInput(codeResult);
        // 2、构建基于 appId 的目录
        String basePath = buildUniqueDir(appId);
        // 3、保存文件（具体实现由子类提供）
        saveFiles(codeResult, basePath);
        // 4、返回目录文件对象
        return new File(basePath);
    }

    /**
     * 验证输入参数（可由子类覆盖）
     * @param codeResult 代码结果对象
     */
    protected void validateInput(T codeResult) {
        if (codeResult == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码结果不能为空");
        }
    }

    /**
     * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
     * @param appId 应用 id
     * @return 目录路径
     */
    protected final String buildUniqueDir(Long appId) {
        ThrowUtils.throwIf(appId == null, ErrorCode.PARAMS_ERROR, "应用 id 不能为空");
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     * @param dirPath 目录路径
     * @param filename 文件名
     * @param content 文件内容
     */
    protected static void writeToFile(String dirPath, String filename, String content) {
        if (StrUtil.isNotBlank(content)) {
            String filePath = dirPath + File.separator + filename;
            FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
        }
    }

    /**
     * 获取代码类型（由子类实现）
     * @return 代码生成类型
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存文件（由子类实现）
     * @param codeResult 代码结果对象
     * @param basePath 基础目录路径
     */
    protected abstract void saveFiles(T codeResult, String basePath);
}

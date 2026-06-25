package com.dorr.aicode.model.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: lwh
 * @date: 2026-06-25
 * @description: 用户更新应用请求
 */
@Data
@Schema(description = "用户更新应用请求")
public class AppUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用 id
     */
    @Schema(description = "应用 id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称", example = "贪吃蛇游戏")
    private String appName;

}

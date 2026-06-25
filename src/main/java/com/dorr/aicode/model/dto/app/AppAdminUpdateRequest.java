package com.dorr.aicode.model.dto.app;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: lwh
 * @date: 2026-06-25
 * @description: 管理员更新应用请求
 */
@Data
@Schema(description = "管理员更新应用请求")
public class AppAdminUpdateRequest implements Serializable {

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

    /**
     * 应用封面
     */
    @Schema(description = "应用封面 URL", example = "https://example.com/cover.png")
    private String cover;

    /**
     * 优先级
     */
    @Schema(description = "优先级，数值越大优先级越高，用于精选应用排序", example = "10")
    private Integer priority;

}

package com.dorr.aicode.model.dto.app;

import com.dorr.aicode.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: lwh
 * @date: 2026-06-25
 * @description: 应用查询请求
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "应用查询请求")
public class AppQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用 id
     */
    @Schema(description = "应用 id", example = "1")
    private Long id;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称，支持模糊查询", example = "贪吃蛇")
    private String appName;

    /**
     * 代码生成类型
     */
    @Schema(description = "代码生成类型", example = "html", allowableValues = {"html", "multi_file"})
    private String codeGenType;

    /**
     * 部署标识
     */
    @Schema(description = "部署标识", example = "abc123")
    private String deployKey;

    /**
     * 优先级
     */
    @Schema(description = "优先级", example = "10")
    private Integer priority;

    /**
     * 创建用户 id
     */
    @Schema(description = "创建用户 id", example = "1")
    private Long userId;

}

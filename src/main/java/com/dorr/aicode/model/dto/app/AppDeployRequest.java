package com.dorr.aicode.model.dto.app;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: lwh
 * @date: 2026-06-26
 * @description: 应用部署请求
 */
@Data
@Schema(description = "应用部署请求")
public class AppDeployRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 应用 id
     */
    @Schema(description = "应用 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long appId;
}

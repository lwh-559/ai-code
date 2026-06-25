package com.dorr.aicode.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: lwh
 * @date: 2026-06-22
 * @description: 分页请求包装类
 */
@Data
@Schema(description = "分页请求基类")
public class PageRequest {

    /**
     * 当前页号
     */
    @Schema(description = "当前页号，从 1 开始", example = "1")
    private int pageNum = 1;

    /**
     * 页面大小
     */
    @Schema(description = "每页数量，最大 20", example = "10")
    private int pageSize = 10;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "create_time")
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    @Schema(description = "排序顺序", example = "descend", allowableValues = {"ascend", "descend"})
    private String sortOrder = "descend";
}

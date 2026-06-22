package com.dorr.aicode.common;


import lombok.Data;

/**
 * @author: lwh
 * @date: 2026-06-22
 * @description: 分页请求包装类
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int pageNum = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}


package com.dorr.aicode.common;


import com.dorr.aicode.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: lwh
 * @date: 2026-06-22
 * @description: 通用响应结果类
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}


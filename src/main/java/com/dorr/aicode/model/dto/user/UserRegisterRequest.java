package com.dorr.aicode.model.dto.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: lwh
 * @date: 2026-06-23
 * @description: 用户注册请求DTO
 */
@Data
@Schema(description = "用户注册请求")
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 账号
     */
    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhangsan")
    private String userAccount;

    /**
     * 密码
     */
    @Schema(description = "用户密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String userPassword;

    /**
     * 确认密码
     */
    @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String checkPassword;
}

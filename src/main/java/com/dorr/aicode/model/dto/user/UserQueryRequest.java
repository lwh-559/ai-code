package com.dorr.aicode.model.dto.user;


import com.dorr.aicode.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: lwh
 * @date: 2026-06-23
 * @description: 分页查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户分页查询请求")
public class UserQueryRequest extends PageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Schema(description = "用户 ID", example = "1")
    private Long id;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称，支持模糊查询", example = "张三")
    private String userName;

    /**
     * 账号
     */
    @Schema(description = "用户账号", example = "zhangsan")
    private String userAccount;

    /**
     * 简介
     */
    @Schema(description = "用户简介", example = "这个人很懒")
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    @Schema(description = "用户角色", example = "user", allowableValues = {"user", "admin", "ban"})
    private String userRole;

}

package com.dorr.aicode.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: lwh
 * @date: 2026-06-23
 * @description: 用户更新请求DTO
 */
@Data
@Schema(description = "用户更新请求")
public class UserUpdateRequest implements Serializable {

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
    @Schema(description = "用户昵称", example = "张三")
    private String userName;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像 URL", example = "https://example.com/avatar.png")
    private String userAvatar;

    /**
     * 简介
     */
    @Schema(description = "用户简介", example = "这个人很懒，什么也没留下")
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    @Schema(description = "用户角色", example = "user", allowableValues = {"user", "admin"})
    private String userRole;

}

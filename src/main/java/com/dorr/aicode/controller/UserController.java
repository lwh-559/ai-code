package com.dorr.aicode.controller;

import com.dorr.aicode.annotation.AuthCheck;
import com.dorr.aicode.common.BaseResponse;
import com.dorr.aicode.common.DeleteRequest;
import com.dorr.aicode.common.ResultUtils;
import com.dorr.aicode.constant.UserConstant;
import com.dorr.aicode.exception.BusinessException;
import com.dorr.aicode.exception.ErrorCode;
import com.dorr.aicode.exception.ThrowUtils;
import com.dorr.aicode.model.dto.user.*;
import com.dorr.aicode.model.entity.user.User;
import com.dorr.aicode.model.vo.user.LoginUserVO;
import com.dorr.aicode.model.vo.user.UserVO;
import com.dorr.aicode.service.UserService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

/**
 * 用户 控制层。
 *
 * @author lwh
 */
@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户的注册、登录、CRUD 接口")
public class UserController {

    @Resource
    private UserService userService;


    /**
     * 用户注销
     *
     * @param request request
     * @return boolean
     */
    @Operation(summary = "用户注销", description = "注销当前登录用户，需要登录")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }


    /**
     * 获取登录的用户信息
     *
     * @author lwh
     * @param request  request
     * @return BaseResponse<LoginUserVO>
     */
    @Operation(summary = "获取当前登录用户", description = "获取当前登录用户的信息，需要登录")
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest  用户信息
     * @param request request
     * @return 脱敏后的用户信息
     */
    @Operation(summary = "用户登录", description = "用户账号密码登录")
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }


    /**
     * 用户注册
     *
     * @param userRegisterRequest   用户注册信息
     * @return 新用户 id
     */
    @Operation(summary = "用户注册", description = "新用户注册账号")
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 1、参数校验
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 创建用户（仅管理员）
     *
     * @param userAddRequest 用户创建请求
     * @return 新用户 id
     */
    @Operation(summary = "管理员创建用户", description = "管理员创建新用户，需要管理员权限")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        long result = userService.addUser(userAddRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     */
    @Operation(summary = "管理员获取用户", description = "管理员根据 ID 获取用户详情，需要管理员权限")
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(
            @Parameter(description = "用户 ID", required = true, example = "1")
            long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     */
    @Operation(summary = "获取用户脱敏信息", description = "根据 ID 获取用户脱敏信息，无需登录")
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(
            @Parameter(description = "用户 ID", required = true, example = "1")
            long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户
     */
    @Operation(summary = "管理员删除用户", description = "管理员删除用户，需要管理员权限")
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest 用户更新请求
     * @param request           request
     * @return 是否更新成功
     */
    @Operation(summary = "更新用户信息", description = "用户更新自己的信息，需要登录")
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
                                            HttpServletRequest request) {
        boolean result = userService.updateUser(userUpdateRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 分页获取用户封装列表（仅管理员）
     *
     * @param userQueryRequest 查询请求参数
     * @return 分页脱敏用户列表
     */
    @Operation(summary = "管理员分页查询用户", description = "管理员分页查询用户列表，需要管理员权限")
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        Page<UserVO> userVOPage = userService.listUserVOByPage(userQueryRequest);
        return ResultUtils.success(userVOPage);
    }


}

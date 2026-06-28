package com.dorr.aicode.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.dorr.aicode.ai.model.enums.CodeGenTypeEnum;
import com.dorr.aicode.annotation.AuthCheck;
import com.dorr.aicode.common.BaseResponse;
import com.dorr.aicode.common.DeleteRequest;
import com.dorr.aicode.common.ResultUtils;
import com.dorr.aicode.constant.UserConstant;
import com.dorr.aicode.exception.ErrorCode;
import com.dorr.aicode.exception.ThrowUtils;
import com.dorr.aicode.model.dto.app.*;
import com.dorr.aicode.model.entity.User;
import com.dorr.aicode.model.vo.app.AppVO;
import com.dorr.aicode.service.AppService;
import com.dorr.aicode.service.UserService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 应用 控制层。
 *
 * @author lwh
 */
@RestController
@RequestMapping("/app")
@Tag(name = "应用管理", description = "应用的增删改查接口")
public class AppController {

    @Resource
    private AppService appService;

    @Resource
    private UserService userService;

    /**
     * 应用聊天生成代码（流式 SSE）
     *
     * @param appId   应用 ID
     * @param message 用户提示词
     * @param request 请求对象
     * @return 生成结果流
     */
    @Operation(summary = "AI生成代码", description = "根据用户提示词生成代码，返回 SSE 流式响应，需要登录")
    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenCode(
            @Parameter(description = "应用 ID", required = true, example = "1")
            @RequestParam Long appId,
            @Parameter(description = "用户提示词", required = true, example = "生成一个贪吃蛇游戏")
            @RequestParam String message,
            HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID无效");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户提示词不能为空");
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 调用服务生成代码（流式）
        Flux<String> contentFlux = appService.chatToGenCode(appId, message, loginUser);
        // 转换为 ServerSentEvent 格式
        return contentFlux
                .map(chunk -> {
                    // 将内容包装成JSON对象
                    Map<String, String> wrapper = Map.of("d", chunk);
                    String jsonData = JSONUtil.toJsonStr(wrapper);

                    return ServerSentEvent.<String>builder()
                            .data(jsonData)
                            .build();
                })
                .concatWith(Mono.just(
                        // 发送结束事件
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build()
                ));
    }

    /**
     * 应用部署
     *
     * @param appDeployRequest 部署请求
     * @param request          请求
     * @return 部署 URL
     */
    @Operation(summary = "部署应用", description = "部署应用并返回访问 URL，需要登录")
    @PostMapping("/deploy")
    public BaseResponse<String> deployApp(@RequestBody AppDeployRequest appDeployRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(appDeployRequest == null, ErrorCode.PARAMS_ERROR);
        Long appId = appDeployRequest.getAppId();
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 调用服务部署应用
        String deployUrl = appService.deployApp(appId, loginUser);
        return ResultUtils.success(deployUrl);
    }


    /**
     * 创建应用
     *
     * @param appAddRequest 创建应用请求
     * @param request       request
     * @return 新应用 id
     */
    @PostMapping("/add")
    @Operation(summary = "创建应用", description = "用户创建新的应用，需要登录")
    public BaseResponse<String> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {
        appAddRequest.setCodeGenType(CodeGenTypeEnum.VUE_PROJECT.getValue());
        long appId = appService.addApp(appAddRequest, request);
        return ResultUtils.success(String.valueOf(appId));
    }

    /**
     * 用户更新自己的应用
     *
     * @param appUpdateRequest 更新应用请求
     * @param request          request
     * @return 是否更新成功
     */
    @PostMapping("/update")
    @Operation(summary = "更新应用", description = "用户更新自己创建的应用，需要登录")
    public BaseResponse<Boolean> updateApp(@RequestBody AppUpdateRequest appUpdateRequest, HttpServletRequest request) {
        boolean result = appService.updateApp(appUpdateRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 用户删除自己的应用
     *
     * @param deleteRequest 删除应用请求
     * @param request request
     * @return 是否删除成功
     */
    @PostMapping("/delete")
    @Operation(summary = "删除应用", description = "用户删除自己创建的应用，需要登录")
    public BaseResponse<Boolean> deleteApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR, "应用 id 不合法");
        boolean result = appService.deleteApp(deleteRequest.getId(), request);
        return ResultUtils.success(result);
    }

    /**
     * 获取应用详情
     *
     * @param id 应用 id
     * @return 应用详情
     */
    @GetMapping("/get/vo")
    @Operation(summary = "获取应用详情", description = "根据 id 获取应用详情，返回脱敏后的应用信息")
    public BaseResponse<AppVO> getAppVOById(
            @Parameter(description = "应用 id", required = true, example = "1")
            long id) {
        AppVO appVO = appService.getAppVO(id);
        return ResultUtils.success(appVO);
    }

    /**
     * 分页查询用户自己的应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 分页应用列表
     */
    @PostMapping("/my/list")
    @Operation(summary = "分页查询我的应用", description = "分页查询当前登录用户创建的应用列表，需要登录")
    public BaseResponse<Page<AppVO>> listMyAppByPage(@RequestBody AppQueryRequest appQueryRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 只查询用户自己的应用
        appQueryRequest.setUserId(loginUser.getId());
        Page<AppVO> page = appService.listMyAppByPage(appQueryRequest);
        return ResultUtils.success(page);
    }

    /**
     * 分页查询精选应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 分页应用列表
     */
    @PostMapping("/featured/list")
    @Operation(summary = "分页查询精选应用", description = "分页查询精选应用列表，无需登录")
    public BaseResponse<Page<AppVO>> listFeaturedAppByPage(@RequestBody AppQueryRequest appQueryRequest) {
        Page<AppVO> page = appService.listFeaturedAppByPage(appQueryRequest);
        return ResultUtils.success(page);
    }

    /**
     * 管理员删除任意应用
     *
     * @param deleteRequest 删除应用请求
     * @return 是否删除成功
     */
    @PostMapping("/admin/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "管理员删除应用", description = "管理员删除任意应用，需要管理员权限")
    public BaseResponse<Boolean> adminDeleteApp(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR, "应用 id 不合法");
        long id = deleteRequest.getId();
        boolean result = appService.adminDeleteApp(id);
        return ResultUtils.success(result);
    }

    /**
     * 管理员更新应用
     *
     * @param appAdminUpdateRequest 管理员更新请求
     * @return 是否更新成功
     */
    @PostMapping("/admin/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "管理员更新应用", description = "管理员更新应用信息，需要管理员权限")
    public BaseResponse<Boolean> adminUpdateApp(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
        boolean result = appService.adminUpdateApp(appAdminUpdateRequest);
        return ResultUtils.success(result);
    }

    /**
     * 管理员分页查询应用列表
     *
     * @param appQueryRequest 查询请求
     * @return 分页应用列表
     */
    @PostMapping("/admin/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "管理员查询应用列表", description = "管理员分页查询所有应用列表，需要管理员权限")
    public BaseResponse<Page<AppVO>> adminListAppByPage(@RequestBody AppQueryRequest appQueryRequest) {
        Page<AppVO> page = appService.adminListAppByPage(appQueryRequest);
        return ResultUtils.success(page);
    }

    /**
     * 管理员获取应用详情
     *
     * @param id 应用 id
     * @return 应用详情
     */
    @GetMapping("/admin/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @Operation(summary = "管理员获取应用详情", description = "管理员获取任意应用详情，需要管理员权限")
    public BaseResponse<AppVO> adminGetAppVO(
            @Parameter(description = "应用 id", required = true, example = "1")
            long id) {
        AppVO appVO = appService.adminGetAppVO(id);
        return ResultUtils.success(appVO);
    }

}

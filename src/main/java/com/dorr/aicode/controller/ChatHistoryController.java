package com.dorr.aicode.controller;

import com.dorr.aicode.annotation.AuthCheck;
import com.dorr.aicode.common.BaseResponse;
import com.dorr.aicode.common.ResultUtils;
import com.dorr.aicode.constant.UserConstant;
import com.dorr.aicode.model.dto.chathistory.ChatHistoryQueryRequest;
import com.dorr.aicode.model.entity.ChatHistory;
import com.dorr.aicode.model.entity.User;
import com.dorr.aicode.service.ChatHistoryService;
import com.dorr.aicode.service.UserService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 对话历史 控制层。
 *
 * @author lwh
 */
@RestController
@RequestMapping("/chatHistory")
@Tag(name = "应用对话历史", description = "应用对话历史的增删改查接口")
public class ChatHistoryController {

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private UserService userService;


    /**
     * 分页查询某个应用的对话历史（游标查询）
     *
     * @param appId          应用ID
     * @param pageSize       页面大小
     * @param lastCreateTime 最后一条记录的创建时间
     * @param request        请求
     * @return 对话历史分页
     */
    @Operation(summary = "分页查询应用对话历史", description = "游标分页查询指定应用的对话历史记录，需要登录")
    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> listAppChatHistory(
            @Parameter(description = "应用ID") @PathVariable Long appId,
            @Parameter(description = "页面大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "最后一条记录的创建时间，用于游标分页") @RequestParam(required = false) LocalDateTime lastCreateTime,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Page<ChatHistory> result = chatHistoryService.listAppChatHistoryByPage(appId, pageSize, lastCreateTime, loginUser);
        return ResultUtils.success(result);
    }


    /**
     * 管理员分页查询所有对话历史
     *
     * @param queryRequest 查询请求
     * @return 对话历史分页
     */
    @Operation(summary = "管理员分页查询对话历史", description = "管理员分页查询所有应用的对话历史，需要管理员权限")
    @PostMapping("/admin/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ChatHistory>> adminListAppChatHistoryByPage(@RequestBody ChatHistoryQueryRequest queryRequest) {
        Page<ChatHistory> result = chatHistoryService.adminListAppChatHistoryByPage(queryRequest);
        return ResultUtils.success(result);
    }

}

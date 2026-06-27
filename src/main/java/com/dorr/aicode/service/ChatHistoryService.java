package com.dorr.aicode.service;

import com.dorr.aicode.model.dto.chathistory.ChatHistoryQueryRequest;
import com.dorr.aicode.model.entity.ChatHistory;
import com.dorr.aicode.model.entity.User;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层。
 *
 * @author lwh
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加聊天消息
     *
     * @param appId        应用ID
     * @param message      消息内容
     * @param messageType  消息类型（user/ai）
     * @param userId       用户ID
     * @return 是否添加成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 根据应用ID删除聊天记录
     *
     * @param appId 应用ID
     * @return 是否删除成功
     */
    boolean deleteByAppId(Long appId);

    /**
     * 分页获取应用聊天历史
     *
     * @param appId         应用ID
     * @param pageSize      页面大小
     * @param lastCreateTime 最后创建时间
     * @param loginUser     登录用户
     * @return 聊天历史分页结果
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);

    /**
     * 管理员分页查询应用聊天历史
     *
     * @param queryRequest 查询请求
     * @return 聊天历史分页结果
     */
    Page<ChatHistory> adminListAppChatHistoryByPage(ChatHistoryQueryRequest queryRequest);

    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);
}

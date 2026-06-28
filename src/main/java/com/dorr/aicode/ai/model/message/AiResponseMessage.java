package com.dorr.aicode.ai.model.message;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author: lwh
 * @date: 2026-06-28
 * @description: AI 响应消息
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AiResponseMessage extends StreamMessage {

    private String data;

    public AiResponseMessage(String data) {
        super(StreamMessageTypeEnum.AI_RESPONSE.getValue());
        this.data = data;
    }
}


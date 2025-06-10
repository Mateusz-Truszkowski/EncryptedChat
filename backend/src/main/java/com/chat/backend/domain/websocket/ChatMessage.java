package com.chat.backend.domain.websocket;

import com.chat.backend.domain.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatMessage {
    private UserDto sender;
    private String content;
    private Integer groupId;
}

package com.chat.backend.controllers;

import com.chat.backend.domain.dto.MessageDto;
import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.domain.entities.GroupEntity;
import com.chat.backend.domain.websocket.ChatMessage;
import com.chat.backend.services.MessageService;
import com.chat.backend.services.UserService;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final MessageService service;

    public ChatController(SimpMessagingTemplate messagingTemplate, UserService userService, MessageService service) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.service = service;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessage incomingMessage,
                            Principal principal,
                            SimpMessageHeaderAccessor headerAccessor) {

        String username = principal != null ? principal.getName()
                : (String) headerAccessor.getSessionAttributes().get("username");

        Optional<UserDto> sender = userService.getUserByUsername(username);

        if (sender.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        ChatMessage message = new ChatMessage();
        message.setSender(sender.get());
        message.setContent(incomingMessage.getContent());
        message.setGroupId(incomingMessage.getGroupId());

        MessageDto messageDto = new MessageDto()
                .builder()
                .content(message.getContent())
                .group(new GroupEntity().builder().id(message.getGroupId()).build())
                .status("Sent")
                .build();

        service.sendMessage(messageDto, username);

        messagingTemplate.convertAndSend(
                "/topic/group." + message.getGroupId(), message);
    }
}

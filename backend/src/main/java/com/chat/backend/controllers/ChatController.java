package com.chat.backend.controllers;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat.send")
    public void sendMessage(SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().get("username");
    }
}

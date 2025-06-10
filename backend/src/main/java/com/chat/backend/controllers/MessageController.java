package com.chat.backend.controllers;

import com.chat.backend.domain.dto.MessageDto;
import com.chat.backend.security.JwtUtil;
import com.chat.backend.services.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
public class MessageController {

    private final MessageService service;
    private final JwtUtil jwtUtil;

    public MessageController(MessageService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/messages")
    public Page<MessageDto> getMessages(Pageable pageable) {
        return service.getMessages(pageable);
    }

    @PostMapping(path = "/messages")
    public MessageDto sendMessage(@RequestBody MessageDto messageDto, @RequestHeader (name = "Authorization") String token) {
        token = token.substring(7);
        return service.sendMessage(messageDto, jwtUtil.extractUsername(token));
    }
}

package com.chat.backend.controllers;

import com.chat.backend.domain.dto.MessageDto;
import com.chat.backend.services.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    /*@GetMapping(path = "/messages")
    public Page<MessageDto> getMessages(Pageable pageable) {
        return service.getMessages(pageable);
    }*/
}

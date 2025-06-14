package com.chat.backend.controllers;

import com.chat.backend.domain.dto.MessageDto;
import com.chat.backend.security.JwtUtil;
import com.chat.backend.services.MessageService;
import com.chat.backend.services.impl.MyFirebaseMessagingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class MessageController {

    private final MessageService service;
    private final SimpMessagingTemplate messagingTemplate;
    private final MyFirebaseMessagingService fcmService;
    private final JwtUtil jwtUtil;

    public MessageController(MessageService service, SimpMessagingTemplate messagingTemplate,
                             JwtUtil jwtUtil, MyFirebaseMessagingService fcmService) {
        this.service = service;
        this.messagingTemplate = messagingTemplate;
        this.jwtUtil = jwtUtil;
        this.fcmService = fcmService;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping(path = "/messages")
    public Page<MessageDto> getMessages(Pageable pageable) {
        return service.getMessages(pageable);
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
    @GetMapping(path = "/messages/{group_id}")
    public Page<MessageDto> getMessagesByGroupId(@PathVariable Integer group_id, Pageable pageable) {
        return service.getMessagesByGroup(pageable, group_id);
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
    @PostMapping(path = "/messages")
    public MessageDto sendMessage(@RequestBody MessageDto messageDto, @RequestHeader (name = "Authorization") String token) {
        token = token.substring(7);
        String username = jwtUtil.extractUsername(token);
        MessageDto sentMessage = service.sendMessage(messageDto, username);
        messagingTemplate.convertAndSend("/topic/group." + messageDto.getGroup().getId(), sentMessage);
        fcmService.sendNotification(sentMessage.getGroup().getId(), username, sentMessage.getSender().getUsername(), sentMessage.getContent());
        return sentMessage;
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
    @DeleteMapping(path = "/messages/{id}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long id, @RequestHeader (name = "Authorization") String token) {
        token = token.substring(7);
        String username = jwtUtil.extractUsername(token);
        try {
            service.deleteMessageById(id, username);
        }
        catch (Exception e) {
            return new ResponseEntity<>("No permissions", HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>("Message deleted", HttpStatus.OK);
    }
}

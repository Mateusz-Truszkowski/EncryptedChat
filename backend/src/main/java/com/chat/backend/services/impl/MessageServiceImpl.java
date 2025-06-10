package com.chat.backend.services.impl;

import com.chat.backend.domain.dto.MessageDto;
import com.chat.backend.domain.entities.MessageEntity;
import com.chat.backend.domain.entities.UserEntity;
import com.chat.backend.mappers.Mapper;
import com.chat.backend.repositories.MessageRepository;
import com.chat.backend.services.MessageService;
import com.chat.backend.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository repository;
    private final UserService userService;
    private final Mapper<MessageEntity, MessageDto> mapper;

    public MessageServiceImpl(MessageRepository repository, UserService userService, Mapper<MessageEntity, MessageDto> mapper) {
        this.repository = repository;
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    public MessageDto sendMessage(MessageDto messageDto, String senderUsername) {
        Optional<UserEntity> sender = userService.getUserEntityByUsername(senderUsername);

        if (sender.isEmpty()) {
            throw new RuntimeException("Sender does not exist");
        }

        messageDto.setSender(sender.get());
        messageDto.setSent_at(LocalDateTime.now());
        return mapper.mapTo(repository.save(mapper.mapFrom(messageDto)));
    }

    @Override
    public Page<MessageDto> getMessages(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::mapTo);
    }

    @Override
    public Page<MessageDto> getMessagesByGroup(Pageable pageable, Integer groupId) {
        return repository.findByGroup_Id(groupId, pageable).map(mapper::mapTo);
    }
}

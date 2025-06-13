package com.chat.backend.services.impl;

import com.chat.backend.domain.dto.MessageDto;
import com.chat.backend.domain.entities.MessageEntity;
import com.chat.backend.domain.entities.UserEntity;
import com.chat.backend.mappers.Mapper;
import com.chat.backend.repositories.MessageRepository;
import com.chat.backend.services.MessageService;
import com.chat.backend.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("sentAt").ascending());
        return repository.findByGroup_Id(groupId, pageable).map(mapper::mapTo);
    }

    @Override
    public void deleteByGroupId(Integer groupId) {
        repository.deleteByGroupId(groupId);
    }

    @Override
    public void changeMessagesUserToDeleted(Integer id) {
        UserEntity deletedUser = userService.getUserEntityByUsername("Deleted User [*]")
                .orElseThrow(() -> new RuntimeException("Deleted user not found"));
        List<MessageEntity> messages = repository.findBySenderId(id);
        for (MessageEntity msg : messages) {
            msg.setSender(deletedUser);
        }
        repository.saveAll(messages);
    }

    @Override
    public void deleteMessageById(Long id, String username) {
        Optional<UserEntity> sender = userService.getUserEntityByUsername(username);
        Optional<MessageEntity> message = repository.findById(id);

        if (sender.isEmpty())
            throw new RuntimeException("Sender does not exist");

        if (message.isEmpty())
            throw new RuntimeException("Message not found");

        if (sender.get().getRole().equals("admin") || sender.get().getUsername().equals(message.get().getSender().getUsername()))
            repository.deleteById(id);
        else
            throw new RuntimeException("Sender does not have required permissions");
    }

    @Override
    public Optional<MessageDto> getMessageByAttachment(String filename) {
        return repository.findByAttachment(filename).map(mapper::mapTo);
    }
}

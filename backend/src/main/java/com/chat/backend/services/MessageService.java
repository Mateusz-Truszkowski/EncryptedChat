package com.chat.backend.services;

import com.chat.backend.domain.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MessageService {
    MessageDto sendMessage(MessageDto messageDto, String senderUsername);

    Page<MessageDto> getMessages(Pageable pageable);

    Page<MessageDto> getMessagesByGroup(Pageable pageable, Integer groupId);

    void deleteByGroupId(Integer groupId);

    void changeMessagesUserToDeleted(Integer id);

    void deleteMessageById(Long id, String username);

    Optional<MessageDto> getMessageByAttachment(String filename);
}

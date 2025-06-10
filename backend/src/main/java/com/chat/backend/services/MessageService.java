package com.chat.backend.services;

import com.chat.backend.domain.dto.MessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {
    MessageDto sendMessage(MessageDto messageDto, String senderUsername);

    Page<MessageDto> getMessages(Pageable pageable);
}

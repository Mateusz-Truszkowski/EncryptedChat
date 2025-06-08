package com.chat.backend.repositories;

import com.chat.backend.domain.entities.MessageEntity;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<MessageEntity, Long> {
}

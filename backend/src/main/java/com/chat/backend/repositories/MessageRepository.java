package com.chat.backend.repositories;

import com.chat.backend.domain.entities.MessageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MessageRepository extends CrudRepository<MessageEntity, Long>, PagingAndSortingRepository<MessageEntity, Long> {
}

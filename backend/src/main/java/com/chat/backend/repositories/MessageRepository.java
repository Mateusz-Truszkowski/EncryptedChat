package com.chat.backend.repositories;

import com.chat.backend.domain.dto.MessageDto;
import com.chat.backend.domain.entities.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MessageRepository extends CrudRepository<MessageEntity, Long>, PagingAndSortingRepository<MessageEntity, Long> {
    Page<MessageEntity> findByGroup_Id(Integer groupId, Pageable pageable);
}

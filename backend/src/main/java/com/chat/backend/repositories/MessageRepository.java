package com.chat.backend.repositories;

import com.chat.backend.domain.entities.MessageEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends CrudRepository<MessageEntity, Long>, PagingAndSortingRepository<MessageEntity, Long> {
    Page<MessageEntity> findByGroup_Id(Integer groupId, Pageable pageable);

    void deleteByGroupId(Integer groupId);

    List<MessageEntity> findBySenderId(Integer senderId);

    Optional<MessageEntity> findByAttachment(String filename);
}

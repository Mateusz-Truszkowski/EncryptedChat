package com.chat.backend.domain.dto;

import com.chat.backend.domain.entities.GroupEntity;
import com.chat.backend.domain.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MessageDto {
    private Long id;
    private GroupEntity group;
    private UserEntity sender;
    private String content;
    private LocalDateTime sent_at;
    private String attachment;
    private String status;
}

package com.chat.backend.domain.dto;

import com.chat.backend.domain.entities.GroupEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDto {
    private Integer id;
    private String username;
    private String password;
    private LocalDateTime last_activity;
    private Set<GroupEntity> groups;
}

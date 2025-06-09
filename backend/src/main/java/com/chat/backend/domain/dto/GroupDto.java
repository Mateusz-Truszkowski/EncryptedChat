package com.chat.backend.domain.dto;

import com.chat.backend.domain.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class GroupDto {
    private Integer id;
    private String name;
    private Set<UserEntity> users;
}

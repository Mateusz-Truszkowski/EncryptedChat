package com.chat.backend.services;

import com.chat.backend.domain.dto.GroupDto;
import com.chat.backend.domain.dto.UserDto;

import java.util.List;

public interface GroupService {
    List<GroupDto> getAllGroups(String username);

    GroupDto createGroup(GroupDto dto, String creatorUsername);

    void addUserToGroup(UserDto dto, Integer groupId, String sender);

    boolean deleteGroup(Integer groupId, String sender);
}

package com.chat.backend.services;

import com.chat.backend.domain.dto.GroupDto;

import java.util.List;

public interface GroupService {
    List<GroupDto> getAllGroups();

    GroupDto createGroup(GroupDto dto, String creatorUsername);
}

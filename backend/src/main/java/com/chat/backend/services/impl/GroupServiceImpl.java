package com.chat.backend.services.impl;

import com.chat.backend.domain.dto.GroupDto;
import com.chat.backend.domain.entities.GroupEntity;
import com.chat.backend.domain.entities.GroupUserEntity;
import com.chat.backend.domain.entities.UserEntity;
import com.chat.backend.mappers.Mapper;
import com.chat.backend.repositories.GroupRepository;
import com.chat.backend.repositories.GroupUserRepository;
import com.chat.backend.services.GroupService;
import com.chat.backend.services.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository repository;
    private final GroupUserRepository groupUserRepository;
    private final Mapper<GroupEntity, GroupDto> mapper;
    private final UserService userService;

    public GroupServiceImpl(GroupRepository groupRepository, Mapper<GroupEntity, GroupDto> groupMapper, GroupUserRepository groupUserRepository,UserService userService) {
        this.repository = groupRepository;
        this.groupUserRepository = groupUserRepository;
        this.mapper = groupMapper;
        this.userService = userService;
    }

    @Override
    public List<GroupDto> getAllGroups() {
        List<GroupEntity> groups = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        return groups.stream().map(mapper::mapTo).toList();
    }

    @Override
    public GroupDto createGroup(GroupDto dto, String creatorUsername) {
        Optional<UserEntity> user = userService.getUserEntityByUsername(creatorUsername);
        GroupDto savedGroup = null;
        if (user.isEmpty()) {
            throw new RuntimeException("Group created by non existing user!");
        }
        else {
            GroupUserEntity entity = new GroupUserEntity().builder()
                    .user(user.get())
                    .group(mapper.mapFrom(dto))
                    .build();
            savedGroup = mapper.mapTo(groupUserRepository.save(entity).getGroup());
        }
        return savedGroup;
    }
}

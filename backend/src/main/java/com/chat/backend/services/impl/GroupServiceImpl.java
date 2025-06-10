package com.chat.backend.services.impl;

import com.chat.backend.domain.dto.GroupDto;
import com.chat.backend.domain.dto.UserDto;
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
    private final Mapper<UserEntity, UserDto> userMapper;
    private final UserService userService;

    public GroupServiceImpl(GroupRepository groupRepository, Mapper<GroupEntity, GroupDto> groupMapper,
                            GroupUserRepository groupUserRepository,UserService userService,
                            Mapper<UserEntity, UserDto> userMapper) {
        this.repository = groupRepository;
        this.groupUserRepository = groupUserRepository;
        this.mapper = groupMapper;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @Override
    public List<GroupDto> getAllGroups(String username) {
        Optional<UserEntity> user = userService.getUserEntityByUsername(username);

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        List<GroupEntity> groups = StreamSupport.stream(repository.findByUser(user.get()).spliterator(), false).toList();
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
                    .role("ADMIN")
                    .build();
            savedGroup = mapper.mapTo(groupUserRepository.save(entity).getGroup());
        }
        return savedGroup;
    }

    @Override
    public void addUserToGroup(UserDto dto, Integer groupId, String sender) {
        Optional<UserEntity> user = userService.getUserEntityByUsername(sender);
        Optional<UserEntity> userToAdd = userService.getUserEntityByUsername(dto.getUsername());

        if (user.isEmpty() || userToAdd.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        Optional<GroupEntity> groupOptional = repository.findById(groupId);

        if (groupOptional.isEmpty()) {
            throw new RuntimeException("Group not found");
        }
        GroupEntity group = groupOptional.get();

        GroupUserEntity groupUser = new GroupUserEntity()
                .builder()
                .user(userToAdd.get())
                .group(group)
                .role("USER")
                .build();
        groupUserRepository.save(groupUser);
    }
}

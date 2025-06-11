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
import com.chat.backend.services.MessageService;
import com.chat.backend.services.UserService;
import jakarta.transaction.Transactional;
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
    private final MessageService messageService;

    public GroupServiceImpl(GroupRepository groupRepository, Mapper<GroupEntity, GroupDto> groupMapper,
                            GroupUserRepository groupUserRepository,UserService userService,
                            MessageService messageService) {
        this.repository = groupRepository;
        this.groupUserRepository = groupUserRepository;
        this.mapper = groupMapper;
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    public List<GroupDto> getAllGroups(String username) {
        Optional<UserEntity> user = userService.getUserEntityByUsername(username);
        List<GroupEntity> groups;

        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        if (user.get().getRole().equals("admin"))
            groups = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        else
            groups = repository.findByUser(user.get()).stream().toList();

        return groups.stream().map(mapper::mapTo).toList();
    }

    @Override
    public GroupDto createGroup(GroupDto dto, String creatorUsername) {
        Optional<UserEntity> user = userService.getUserEntityByUsername(creatorUsername);
        GroupDto savedGroup;
        if (user.isEmpty()) {
            throw new RuntimeException("Group created by non existing user!");
        }
        else {
            GroupUserEntity entity = new GroupUserEntity();
            entity.setUser(user.get());
            entity.setGroup(mapper.mapFrom(dto));
            entity.setRole("ADMIN");
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

        GroupUserEntity groupUser = new GroupUserEntity();
        groupUser.setUser(userToAdd.get());
        groupUser.setGroup(group);
        groupUser.setRole("USER");
        groupUserRepository.save(groupUser);
    }

    @Transactional
    @Override
    public boolean deleteGroup(Integer groupId, String sender) {
        Optional<UserEntity> user = userService.getUserEntityByUsername(sender);
        Optional<GroupEntity> groupOptional = repository.findById(groupId);

        if (user.isEmpty() || groupOptional.isEmpty())
            return false;

        Optional<GroupUserEntity> groupUser = groupUserRepository.findAllByUserAndGroupId(user.get().getId(), groupId);

        if (groupUser.isEmpty() || !groupUser.get().getRole().equals("ADMIN")) {
            return false;
        }

        messageService.deleteByGroupId(groupId);
        groupUserRepository.deleteByGroupId(groupId);
        repository.deleteById(groupId);
        return true;
    }
}

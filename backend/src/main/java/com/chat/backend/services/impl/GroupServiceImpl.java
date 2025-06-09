package com.chat.backend.services.impl;

import com.chat.backend.domain.dto.GroupDto;
import com.chat.backend.domain.entities.GroupEntity;
import com.chat.backend.mappers.Mapper;
import com.chat.backend.repositories.GroupRepository;
import com.chat.backend.services.GroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository repository;
    private final Mapper<GroupEntity, GroupDto> mapper;

    public GroupServiceImpl(GroupRepository groupRepository, Mapper<GroupEntity, GroupDto> groupMapper) {
        this.repository = groupRepository;
        this.mapper = groupMapper;
    }

    @Override
    public List<GroupDto> getAllGroups() {
        List<GroupEntity> groups = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        return groups.stream().map(mapper::mapTo).toList();
    }

    @Override
    public GroupDto createGroup(GroupDto dto) {
        return mapper.mapTo(repository.save(mapper.mapFrom(dto)));
    }
}

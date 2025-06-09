package com.chat.backend.mappers.impl;

import com.chat.backend.domain.dto.GroupDto;
import com.chat.backend.domain.entities.GroupEntity;
import com.chat.backend.mappers.Mapper;
import org.modelmapper.ModelMapper;

public class GroupMapperImpl implements Mapper<GroupEntity, GroupDto> {

    private final ModelMapper modelMapper;

    public GroupMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public GroupDto mapTo(GroupEntity entity) {
        return modelMapper.map(entity, GroupDto.class);
    }

    @Override
    public GroupEntity mapFrom(GroupDto GroupDto) {
        return modelMapper.map(GroupDto, GroupEntity.class);
    }
}

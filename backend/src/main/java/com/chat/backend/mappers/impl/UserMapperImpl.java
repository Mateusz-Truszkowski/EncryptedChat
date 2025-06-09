package com.chat.backend.mappers.impl;

import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.domain.entities.UserEntity;
import com.chat.backend.mappers.Mapper;
import org.modelmapper.ModelMapper;

public class UserMapperImpl implements Mapper<UserEntity, UserDto> {

    private final ModelMapper modelMapper;

    public UserMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }
}

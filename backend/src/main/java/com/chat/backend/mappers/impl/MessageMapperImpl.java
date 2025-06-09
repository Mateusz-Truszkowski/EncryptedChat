package com.chat.backend.mappers.impl;

import com.chat.backend.domain.dto.MessageDto;
import com.chat.backend.domain.entities.MessageEntity;
import com.chat.backend.mappers.Mapper;
import org.modelmapper.ModelMapper;

public class MessageMapperImpl implements Mapper<MessageEntity, MessageDto> {

    private final ModelMapper modelMapper;
    
    public MessageMapperImpl(ModelMapper mapper) {
        this.modelMapper = mapper;
    }

    @Override
    public MessageDto mapTo(MessageEntity entity) {
        return modelMapper.map(entity, MessageDto.class);
    }

    @Override
    public MessageEntity mapFrom(MessageDto MessageDto) {
        return modelMapper.map(MessageDto, MessageEntity.class);
    }
}

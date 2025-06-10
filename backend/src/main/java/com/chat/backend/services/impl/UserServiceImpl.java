package com.chat.backend.services.impl;

import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.domain.entities.UserEntity;
import com.chat.backend.mappers.Mapper;
import com.chat.backend.repositories.UserRepository;
import com.chat.backend.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    Mapper<UserEntity, UserDto> mapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, Mapper<UserEntity, UserDto> userMapper, PasswordEncoder passwordEncoder) {
        this.repository = userRepository;
        this.mapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto user) {
        UserEntity userEntity = mapper.mapFrom(user);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
        return mapper.mapTo(repository.save(userEntity));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserEntity> users = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        return users.stream().map(mapper::mapTo).toList();
    }
}

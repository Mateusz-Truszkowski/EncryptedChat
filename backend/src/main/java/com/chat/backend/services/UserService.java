package com.chat.backend.services;

import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.domain.entities.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto createUser(UserDto user, String sender);

    List<UserDto> getAllUsers();

    Optional<UserDto> getUserByUsername(String username);

    Optional<UserEntity> getUserEntityByUsername(String username);

    void deleteById(Integer id);
}

package com.chat.backend.services;

import com.chat.backend.domain.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto user);

    List<UserDto> getAllUsers();
}

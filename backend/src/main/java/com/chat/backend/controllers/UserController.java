package com.chat.backend.controllers;

import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.services.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final UserService service;

    public UserController(UserService userService) {
        this.service = userService;
    }

    @GetMapping(path = "/users")
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping(path = "/users")
    public UserDto createUser(@RequestBody UserDto user) {
        return service.createUser(user);
    }
}

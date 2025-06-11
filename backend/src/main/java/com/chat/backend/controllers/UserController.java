package com.chat.backend.controllers;

import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.security.JwtUtil;
import com.chat.backend.services.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService service;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.service = userService;
        this.jwtUtil = jwtUtil;
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
    @GetMapping(path = "/users")
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @PostMapping(path = "/users")
    public UserDto createUser(@RequestBody UserDto user, @RequestHeader(name = "Authorization", required = false) String token) {
        String username = null;

        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        return service.createUser(user, username);
    }
}

package com.chat.backend.controllers;

import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.security.JwtUtil;
import com.chat.backend.services.UserDeletionService;
import com.chat.backend.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService service;
    private final UserDeletionService userDeletionService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, UserDeletionService userDeletionService, JwtUtil jwtUtil) {
        this.service = userService;
        this.jwtUtil = jwtUtil;
        this.userDeletionService = userDeletionService;
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

    @GetMapping(path = "/users/{username}")
    public ResponseEntity<UserDto> getUser(
            @PathVariable("username") String username,
            @RequestHeader(name = "Authorization", required = false) String token) {

        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
        }

        Optional<UserDto> foundUser = service.getUserByUsername(username);

        return foundUser.map(user -> new ResponseEntity<>(user, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyRole('admin', 'user')")
    @DeleteMapping(path = "/users/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable("username") String username, @RequestHeader(name = "Authorization", required = false) String token) {
        String sender;

        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            sender = jwtUtil.extractUsername(jwt);
        }
        else
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);

        if (userDeletionService.deleteUserByUsername(username, sender))
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        else
            return new ResponseEntity<>("Cannot delete user", HttpStatus.FORBIDDEN);
    }
}

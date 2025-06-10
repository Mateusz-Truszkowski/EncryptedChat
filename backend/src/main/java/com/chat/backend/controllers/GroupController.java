package com.chat.backend.controllers;

import com.chat.backend.domain.dto.GroupDto;
import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.security.JwtUtil;
import com.chat.backend.services.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupController {

    private final GroupService service;
    private final JwtUtil jwtUtil;

    public GroupController(GroupService groupService, JwtUtil jwtUtil) {
        this.service = groupService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/groups")
    public List<GroupDto> getAllGroups(@RequestHeader (name = "Authorization") String token) {
        token = token.substring(7);
        return service.getAllGroups(jwtUtil.extractUsername(token));
    }

    @PostMapping(path = "/groups")
    public GroupDto createGroup(@RequestBody GroupDto dto, @RequestHeader (name = "Authorization") String token) {
        token = token.substring(7);
        return service.createGroup(dto, jwtUtil.extractUsername(token));
    }

    @PostMapping(path = "/groups/{groupId}/add_user")
    public ResponseEntity<String> addUserToGroup(@PathVariable Integer groupId, @RequestBody UserDto dto, @RequestHeader (name = "Authorization") String token) {
        token = token.substring(7);
        String sender = jwtUtil.extractUsername(token);
        service.addUserToGroup(dto, groupId, sender);
        return new ResponseEntity<String>("Successfully added new user", HttpStatus.OK);
    }
}

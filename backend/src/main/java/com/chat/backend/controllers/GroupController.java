package com.chat.backend.controllers;

import com.chat.backend.domain.dto.GroupDto;
import com.chat.backend.security.JwtUtil;
import com.chat.backend.services.GroupService;
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
    public List<GroupDto> getAllGroups() {
        return service.getAllGroups();
    }

    @PostMapping(path = "/groups")
    public GroupDto createGroup(@RequestBody GroupDto dto, @RequestHeader (name = "Authorization") String token) {
        token = token.substring(7);
        return service.createGroup(dto, jwtUtil.extractUsername(token));
    }
}

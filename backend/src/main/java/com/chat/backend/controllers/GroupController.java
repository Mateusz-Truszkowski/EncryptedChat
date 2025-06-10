package com.chat.backend.controllers;

import com.chat.backend.domain.dto.GroupDto;
import com.chat.backend.services.GroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GroupController {

    private final GroupService service;

    public GroupController(GroupService groupService) {
        this.service = groupService;
    }

    @GetMapping(path = "/groups")
    public List<GroupDto> getAllGroups() {
        return service.getAllGroups();
    }

    @PostMapping(path = "/groups")
    public GroupDto createGroup(@RequestBody GroupDto dto, @RequestHeader (name = "Authorization") String token) {
        return service.createGroup(dto);
    }
}

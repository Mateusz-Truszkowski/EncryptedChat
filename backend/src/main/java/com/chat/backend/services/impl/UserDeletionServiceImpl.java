package com.chat.backend.services.impl;

import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.repositories.GroupUserRepository;
import com.chat.backend.services.MessageService;
import com.chat.backend.services.UserDeletionService;
import com.chat.backend.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDeletionServiceImpl implements UserDeletionService {

    private final UserService userService;
    private final GroupUserRepository groupUserRepository;
    private final MessageService messageService;

    public UserDeletionServiceImpl(UserService userService, GroupUserRepository groupUserRepository, MessageService messageService) {
        this.userService = userService;
        this.groupUserRepository = groupUserRepository;
        this.messageService = messageService;
    }

    @Transactional
    @Override
    public boolean deleteUserByUsername(String username, String sender) {
        Optional<UserDto> user = userService.getUserByUsername(username);
        Optional<UserDto> senderDto = userService.getUserByUsername(sender);

        if (user.isEmpty() || senderDto.isEmpty()) {
            return false;
        }

        if (senderDto.get().getRole().equals("admin") || senderDto.get().getUsername().equals(username)) {
            messageService.changeMessagesUserToDeleted(user.get().getId());
            groupUserRepository.deleteByUserId(user.get().getId());
            userService.deleteById(user.get().getId());
            return true;
        }
        else
            return false;
    }
}

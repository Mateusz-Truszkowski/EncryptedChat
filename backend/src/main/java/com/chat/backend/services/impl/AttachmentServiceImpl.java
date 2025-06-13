package com.chat.backend.services.impl;

import com.chat.backend.domain.dto.MessageDto;
import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.services.AttachmentService;
import com.chat.backend.services.GroupService;
import com.chat.backend.services.MessageService;
import com.chat.backend.services.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final MessageService messageService;
    private final UserService userService;
    private final GroupService groupService;

    public AttachmentServiceImpl(MessageService messageService, UserService userService,
                                 GroupService groupService) {
        this.messageService = messageService;
        this.userService = userService;
        this.groupService = groupService;
    }

    @Override
    public String saveAttachment(MultipartFile file, String sender) throws IOException {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadDir = Paths.get(System.getProperty("user.home"), "chatapp_uploads");

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir); // <- UTWÓRZ KATALOG JEŚLI GO NIE MA
        }

        Path path = uploadDir.resolve(filename);
        file.transferTo(path.toFile());
        return filename;
    }

    @Override
    public Resource getAttachment(String filename, String sender) throws IOException {
        Optional<MessageDto> msg = messageService.getMessageByAttachment(filename);
        Optional<UserDto> user = userService.getUserByUsername(sender);

        if (msg.isEmpty() || user.isEmpty())
            return null;

        if (!groupService.isUserInGroup(user.get(), msg.get().getGroup()))
            return null;

        Path filePath = Paths.get(System.getProperty("user.home"), "chatapp_uploads").resolve(filename);
        return new UrlResource(filePath.toUri());
    }
}

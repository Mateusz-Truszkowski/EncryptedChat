package com.chat.backend.controllers;

import com.chat.backend.security.JwtUtil;
import com.chat.backend.services.AttachmentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@PreAuthorize("hasAnyRole('admin', 'user')")
@RestController
public class AttachmentController {

    private final JwtUtil jwtUtil;
    private final AttachmentService attachmentService;

    public AttachmentController(JwtUtil jwtUtil, AttachmentService attachmentService) {
        this.jwtUtil = jwtUtil;
        this.attachmentService = attachmentService;
    }

    @PostMapping("/attachments")
    public ResponseEntity<Map<String, String>> uploadAttachment(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String token
    ) throws IOException {
        token = token.substring(7);
        String username = jwtUtil.extractUsername(token);
        String filename = attachmentService.saveAttachment(file, username);

        return ResponseEntity.ok(Map.of("attachment", filename));
    }

    @GetMapping("/attachments/{filename}")
    public ResponseEntity<Resource> getAttachment(@PathVariable String filename, @RequestHeader("Authorization") String token) throws MalformedURLException {
        token = token.substring(7);
        String username = jwtUtil.extractUsername(token);
        Resource resource = null;

        try {
            resource = attachmentService.getAttachment(filename, username);
        }
        catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

package com.chat.backend.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AttachmentService {
    String saveAttachment(MultipartFile file, String sender) throws IOException;
    Resource getAttachment(String filename, String sender) throws IOException;
}

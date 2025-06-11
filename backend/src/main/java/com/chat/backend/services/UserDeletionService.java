package com.chat.backend.services;

public interface UserDeletionService {
    boolean deleteUserByUsername(String username, String sender);
}

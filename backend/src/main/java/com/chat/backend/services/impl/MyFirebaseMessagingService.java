package com.chat.backend.services.impl;

import com.chat.backend.domain.entities.UserEntity;
import com.chat.backend.repositories.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyFirebaseMessagingService {

    @Autowired
    private UserRepository userRepository;

    public void setToken(String username, String token) {
        Optional<UserEntity> user = userRepository.findUserByUsername(username);

        if (user.isEmpty())
            throw new RuntimeException("User not found");

        user.get().setFcmToken(token);
        userRepository.save(user.get());
    }

    public void sendNotification(Integer groupId, String sender, String title, String body) {
        List<UserEntity> users = userRepository.findAllUsersByGroupId(groupId);

        if (users.isEmpty())
            throw new RuntimeException("Cannot send notification: No user found");

        for (UserEntity user : users) {
            if (!user.getUsername().equals(sender)) {
                Message message = Message.builder()
                        .setToken(user.getFcmToken())
                        .putData("title", title)
                        .putData("body", body)
                        .build();
                try {
                    FirebaseMessaging.getInstance().send(message);
                } catch (FirebaseMessagingException e) {
                    System.err.println("Error sending FCM: " + e.getMessage());
                }
            }
        }
    }
}

package com.chat.backend.services.impl;

import com.chat.backend.domain.dto.UserDto;
import com.chat.backend.domain.entities.UserEntity;
import com.chat.backend.mappers.Mapper;
import com.chat.backend.repositories.UserRepository;
import com.chat.backend.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    Mapper<UserEntity, UserDto> mapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, Mapper<UserEntity, UserDto> userMapper, PasswordEncoder passwordEncoder) {
        this.repository = userRepository;
        this.mapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto user, String sender) {
        Optional<UserDto> senderDto = getUserByUsername(sender);

        UserEntity userEntity = mapper.mapFrom(user);
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));

        if (senderDto.isEmpty()) {
            userEntity.setRole("user");
        }
        else if (senderDto.get().getRole().equals("user")) {
            userEntity.setRole("user");
        }

        return mapper.mapTo(repository.save(userEntity));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserEntity> users = StreamSupport.stream(repository.findAll().spliterator(), false).toList();
        return users.stream().map(mapper::mapTo).toList();
    }

    @Override
    public Optional<UserDto> getUserByUsername(String username) {
        return repository.findUserByUsername(username).map(mapper::mapTo);
    }

    @Override
    public Optional<UserEntity> getUserEntityByUsername(String username) {
        return repository.findUserByUsername(username);
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}

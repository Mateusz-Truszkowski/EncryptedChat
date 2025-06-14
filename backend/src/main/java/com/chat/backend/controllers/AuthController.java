package com.chat.backend.controllers;

import com.chat.backend.security.JwtUtil;
import com.chat.backend.services.UserService;
import com.chat.backend.services.impl.MyFirebaseMessagingService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final MyFirebaseMessagingService fcmService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
                          JwtUtil jwtUtil, MyFirebaseMessagingService fcmService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.fcmService = fcmService;
    }

    @PostMapping(path = "/auth")
    public ResponseEntity<Map<String, String>> authorize(@RequestBody Credentials credentials) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>(Map.of("error", "Bad login credentials"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.getUsername());
        fcmService.setToken(credentials.getUsername(), credentials.getFcmToken());
        return new ResponseEntity<>(Map.of("token", jwtUtil.generateToken(userDetails)), HttpStatus.OK);
    }

    @Data
    public static class Credentials {
        String username;
        String password;
        String fcmToken;
    }
}

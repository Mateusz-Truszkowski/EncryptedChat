package com.chat.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtHandshakeInterceptor(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {

        String query = request.getURI().getQuery();
        String token = null;

        if (query != null && query.startsWith("token=")) {
            token = query.substring("token=".length());
        }

        if (token != null && jwtUtil.validateToken(token, userDetailsService.loadUserByUsername(jwtUtil.extractUsername(token)))) {
            String username = jwtUtil.extractUsername(token);
            attributes.put("username", username);
            return true;
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {}
}
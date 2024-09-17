package com.serch.server.core.socket;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

public interface SocketService {
    /**
     * Authenticate the web socket request
     *
     * @param accessor The Header details for the web socket request
     */
    void authenticate(SimpMessageHeaderAccessor accessor);
}
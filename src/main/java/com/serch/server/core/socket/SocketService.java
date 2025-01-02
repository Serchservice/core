package com.serch.server.core.socket;

import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

/**
 * Service interface for managing WebSocket operations.
 * <p>
 * This interface provides methods for handling WebSocket connections and
 * communications. Implementations of this interface should manage
 * the authentication and other related functionalities for WebSocket
 * requests.
 * </p>
 */
public interface SocketService {

    /**
     * Authenticates a WebSocket request using the provided header information.
     * <p>
     * This method is responsible for validating the authenticity of a WebSocket
     * connection request. It checks the provided headers to determine if the
     * request should be allowed to establish a connection. Implementations should
     * perform necessary security checks, such as verifying tokens or user sessions.
     * </p>
     *
     * @param accessor The {@link SimpMessageHeaderAccessor} that contains the
     *                 header details for the WebSocket request. This includes
     *                 information such as session ID, user information, and other
     *                 metadata associated with the WebSocket connection.
     */
    void check(SimpMessageHeaderAccessor accessor);
}
package com.serch.server.services.conversation.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.conversation.responses.ChatGroupMessageResponse;
import com.serch.server.services.conversation.responses.ChatRoomResponse;
import com.serch.server.services.conversation.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final ChatService service;

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> rooms(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<ChatRoomResponse>> response = service.rooms(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/room/{room}")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> room(@PathVariable String room) {
        ApiResponse<ChatRoomResponse> response = service.room(room);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/room")
    @PreAuthorize(value = "hasRole('USER')")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> room(@RequestParam UUID roommate) {
        ApiResponse<ChatRoomResponse> response = service.getOrCreate(roommate);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/room/{room}/messages")
    public ResponseEntity<ApiResponse<List<ChatGroupMessageResponse>>> messages(
            @PathVariable String room,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<ChatGroupMessageResponse>> response = service.messages(room, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
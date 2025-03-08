package com.serch.server.domains.conversation.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.conversation.requests.SendMessageRequest;
import com.serch.server.domains.conversation.requests.UpdateMessageRequest;
import com.serch.server.domains.conversation.responses.ChatGroupMessageResponse;
import com.serch.server.domains.conversation.responses.ChatRoomResponse;
import com.serch.server.domains.conversation.services.ChatService;
import com.serch.server.domains.conversation.services.ChattingService;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService service;
    private final ChattingService chatting;

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

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> send(@RequestBody SendMessageRequest request) {
        chatting.send(request, AuthUtil.getAuth());
        return ResponseEntity.ok(new ApiResponse<>("Successfully sent", HttpStatus.OK));
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<String>> update(@RequestBody UpdateMessageRequest request) {
        chatting.update(request, AuthUtil.getAuth());
        return ResponseEntity.ok(new ApiResponse<>("Successfully updated", HttpStatus.OK));
    }
}
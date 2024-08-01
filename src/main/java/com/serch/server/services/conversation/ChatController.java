package com.serch.server.services.conversation;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.conversation.requests.RoomRequest;
import com.serch.server.services.conversation.requests.SendMessageRequest;
import com.serch.server.services.conversation.requests.UpdateMessageRequest;
import com.serch.server.services.conversation.responses.ChatRoomResponse;
import com.serch.server.services.conversation.services.ChatService;
import com.serch.server.services.conversation.services.ChattingService;
import com.serch.server.core.socket.SocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@MessageMapping("/chat")
@RequestMapping("/chat")
public class ChatController {
    private final ChatService service;
    private final SocketService socket;
    private final ChattingService chatting;

    @MessageMapping("/send")
    public void sendMessage(@Payload SendMessageRequest message, SimpMessageHeaderAccessor header) {
        socket.authenticate(header);
        chatting.send(message);
    }

    @MessageMapping("/refresh")
    public void refresh(@Payload RoomRequest room, SimpMessageHeaderAccessor header) {
        socket.authenticate(header);
        chatting.refresh(room.getRoom());
    }

    @MessageMapping("/update")
    public void updateStatus(@Payload UpdateMessageRequest message, SimpMessageHeaderAccessor header) {
        socket.authenticate(header);
        chatting.update(message);
    }

    @MessageMapping("/update/all")
    public void updateAllStatus(@Payload UpdateMessageRequest message, SimpMessageHeaderAccessor header) {
        socket.authenticate(header);
        chatting.updateAll(message);
    }

    @MessageMapping("/connect")
    public void sendMessage(@Payload RoomRequest room, SimpMessageHeaderAccessor header) {
        socket.authenticate(header);
        chatting.announce(room.getRoom());
    }

    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse<List<ChatRoomResponse>>> rooms() {
        ApiResponse<List<ChatRoomResponse>> response = service.rooms();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/room/{room}")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> messages(@PathVariable String room) {
        ApiResponse<ChatRoomResponse> response = service.messages(room);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/room")
    @PreAuthorize(value = "hasRole('USER')")
    public ResponseEntity<ApiResponse<ChatRoomResponse>> room(@RequestParam UUID roommate) {
        ApiResponse<ChatRoomResponse> response = service.room(roommate);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
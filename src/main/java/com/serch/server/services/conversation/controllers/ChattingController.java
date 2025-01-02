package com.serch.server.services.conversation.controllers;

import com.serch.server.services.conversation.requests.MessageTypingRequest;
import com.serch.server.services.conversation.requests.SendMessageRequest;
import com.serch.server.services.conversation.requests.UpdateMessageRequest;
import com.serch.server.services.conversation.services.ChattingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@MessageMapping("/chat")
public class ChattingController {
    private final ChattingService chatting;

    @MessageMapping("/send")
    public void sendMessage(@Payload SendMessageRequest message, SimpMessageHeaderAccessor header) {
        Object emailAddress = Objects.requireNonNull(header.getSessionAttributes()).get("username");

        chatting.send(message, (String) emailAddress);
    }

    @MessageMapping("/refresh/{room}")
    public void refresh(@DestinationVariable String room, SimpMessageHeaderAccessor header) {
        Object emailAddress = Objects.requireNonNull(header.getSessionAttributes()).get("username");

        chatting.refresh(room, (String) emailAddress);
    }

    @MessageMapping("/update")
    public void updateStatus(@Payload UpdateMessageRequest message, SimpMessageHeaderAccessor header) {
        Object emailAddress = Objects.requireNonNull(header.getSessionAttributes()).get("username");

        chatting.update(message, (String) emailAddress);
    }

    @MessageMapping("/update/all")
    public void updateAllStatus(@Payload UpdateMessageRequest message, SimpMessageHeaderAccessor header) {
        Object emailAddress = Objects.requireNonNull(header.getSessionAttributes()).get("username");

        chatting.updateAll(message, (String) emailAddress);
    }

    @MessageMapping("/{room}/mark/read")
    public void updateAllStatus(@DestinationVariable String room, SimpMessageHeaderAccessor header) {
        Object emailAddress = Objects.requireNonNull(header.getSessionAttributes()).get("username");

        chatting.markAllAsRead(room, (String) emailAddress);
    }

    @MessageMapping("/typing/notify")
    public void notifyTyping(@Payload MessageTypingRequest request, SimpMessageHeaderAccessor header) {
        Object emailAddress = Objects.requireNonNull(header.getSessionAttributes()).get("username");

        chatting.notifyTyping(request, (String) emailAddress);
    }

    @MessageMapping("/announce/{room}")
    public void sendMessage(@DestinationVariable String room, SimpMessageHeaderAccessor header) {
        Object emailAddress = Objects.requireNonNull(header.getSessionAttributes()).get("username");

        chatting.announce(room, (String) emailAddress);
    }
}
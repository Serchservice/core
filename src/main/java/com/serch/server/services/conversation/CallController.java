package com.serch.server.services.conversation;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.socket.SocketService;
import com.serch.server.services.conversation.requests.UpdateCallRequest;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.ActiveCallResponse;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.services.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@MessageMapping("/call")
@RequestMapping("/call")
public class CallController {
    private final CallService service;
    private final SocketService socket;

    @MessageMapping("/session")
    public void checkSession(@Payload UpdateCallRequest request, SimpMessageHeaderAccessor header) {
        socket.authenticate(header);
        service.checkSession(request.getDuration(), request.getChannel());
    }

    @MessageMapping("/answer")
    public void answer(@Payload UpdateCallRequest request, SimpMessageHeaderAccessor header) {
        socket.authenticate(header);
        service.answer(request.getChannel());
    }

    @MessageMapping("/end")
    public void end(@Payload UpdateCallRequest request, SimpMessageHeaderAccessor header) {
        socket.authenticate(header);
        service.end(request.getChannel());
    }

    @MessageMapping("/decline")
    public void decline(@Payload UpdateCallRequest request, SimpMessageHeaderAccessor header) {
        socket.authenticate(header);
        service.decline(request.getChannel());
    }

    @MessageMapping("/update")
    public void update(@Payload UpdateCallRequest request, SimpMessageHeaderAccessor header) {
        socket.authenticate(header);
        service.update(request.getChannel(), request.getStatus());
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<List<CallResponse>>> logs() {
        ApiResponse<List<CallResponse>> response = service.logs();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/auth")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<String>> auth(@RequestParam String channel) {
        ApiResponse<String> response = service.auth(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<ActiveCallResponse>> start(@RequestBody StartCallRequest request) {
        ApiResponse<ActiveCallResponse> response = service.start(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
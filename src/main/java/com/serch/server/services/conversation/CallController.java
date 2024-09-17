package com.serch.server.services.conversation;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.conversation.requests.UpdateCallRequest;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.ActiveCallResponse;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.services.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/call")
@PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
public class CallController {
    private final CallService service;

    @GetMapping
    public ResponseEntity<ApiResponse<ActiveCallResponse>> fetch(@RequestParam String channel) {
        ApiResponse<ActiveCallResponse> response = service.fetch(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<CallResponse>>> logs() {
        ApiResponse<List<CallResponse>> response = service.logs();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/auth")
    public ResponseEntity<ApiResponse<String>> auth(@RequestParam String channel) {
        ApiResponse<String> response = service.auth(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/authentication")
    public ResponseEntity<ApiResponse<String>> auth() {
        ApiResponse<String> response = service.auth();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<ActiveCallResponse>> start(@RequestBody StartCallRequest request) {
        ApiResponse<ActiveCallResponse> response = service.start(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<ActiveCallResponse>> update(@RequestBody UpdateCallRequest request) {
        ApiResponse<ActiveCallResponse> response = service.update(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/session")
    public ResponseEntity<ApiResponse<ActiveCallResponse>> checkSession(@RequestBody UpdateCallRequest request) {
        ApiResponse<ActiveCallResponse> response = service.checkSession(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
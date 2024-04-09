package com.serch.server.services.conversation.controllers;

import com.serch.backend.bases.ApiResponse;
import com.serch.backend.enums.call.CallStatus;
import com.serch.backend.enums.call.CallType;
import com.serch.backend.platform.call.responses.CallHistoryResponse;
import com.serch.backend.platform.call.responses.CallResponse;
import com.serch.backend.platform.call.responses.StartCallResponse;
import com.serch.backend.platform.call.services.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/call")
public class CallController {
    private final CallService callService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<StartCallResponse>> start(
            @RequestParam UUID invited, @RequestParam CallType type
    ) {
        var response = callService.start(invited, type);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/join")
    public ResponseEntity<ApiResponse<StartCallResponse>> join(@RequestParam String channel) {
        var response = callService.join(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/end")
    public ResponseEntity<ApiResponse<String>> end(@RequestParam String channel) {
        var response = callService.end(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/decline")
    public ResponseEntity<ApiResponse<CallStatus>> decline(@RequestParam String channel) {
        var response = callService.decline(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/leave")
    public ResponseEntity<ApiResponse<CallStatus>> leave(@RequestParam String channel) {
        var response = callService.leave(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CallHistoryResponse>>> viewAll() {
        var response = callService.calls();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<CallResponse>>> view(@PathVariable UUID userId) {
        var response = callService.view(userId);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

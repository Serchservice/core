package com.serch.server.services.conversation.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.responses.StartCallResponse;
import com.serch.server.services.conversation.services.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/call")
public class CallController {
    private final CallService service;

    @GetMapping
    public ResponseEntity<ApiResponse<String>> checkSession(@RequestParam Integer duration, @RequestParam String channel) {
        var response = service.checkSession(duration, channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/logs")
    public ResponseEntity<ApiResponse<List<CallResponse>>> logs() {
        var response = service.logs();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<StartCallResponse>> start(@RequestBody StartCallRequest request) {
        var response = service.start(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/answer")
    public ResponseEntity<ApiResponse<StartCallResponse>> answer(@RequestParam String channel) {
        var response = service.answer(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/end")
    public ResponseEntity<ApiResponse<StartCallResponse>> end(@RequestParam String channel) {
        var response = service.end(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/decline")
    public ResponseEntity<ApiResponse<StartCallResponse>> decline(@RequestParam String channel) {
        var response = service.decline(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/cancel")
    public ResponseEntity<ApiResponse<StartCallResponse>> cancel(@RequestParam String channel) {
        var response = service.cancel(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

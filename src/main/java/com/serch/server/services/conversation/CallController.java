package com.serch.server.services.conversation;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.responses.StartCallResponse;
import com.serch.server.services.conversation.services.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/call")
public class CallController {
    private final CallService service;

    @GetMapping
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<String>> checkSession(@RequestParam Integer duration, @RequestParam String channel) {
        ApiResponse<String> response = service.checkSession(duration, channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/logs")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<List<CallResponse>>> logs() {
        ApiResponse<List<CallResponse>> response = service.logs();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/verify")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<StartCallResponse>> verify(@RequestParam String channel) {
        ApiResponse<StartCallResponse> response = service.verify(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/rtc/{channelName}/publisher/uid/{uid}")
    public ResponseEntity<Map<String, String>> authenticate(
            @PathVariable(name = "channelName") String channel,
            @PathVariable(name = "uid") Integer uid
    ) {
        Map<String, String> response = service.authenticate(channel, uid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/start")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<StartCallResponse>> start(@RequestBody StartCallRequest request) {
        ApiResponse<StartCallResponse> response = service.start(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/answer")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<StartCallResponse>> answer(@RequestParam String channel) {
        ApiResponse<StartCallResponse> response = service.answer(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/end")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<StartCallResponse>> end(@RequestParam String channel) {
        ApiResponse<StartCallResponse> response = service.end(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/decline")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<StartCallResponse>> decline(@RequestParam String channel) {
        ApiResponse<StartCallResponse> response = service.decline(channel);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/update")
    @PreAuthorize("hasRole('PROVIDER') || hasRole('USER') || hasRole('ASSOCIATE_PROVIDER')")
    public ResponseEntity<ApiResponse<StartCallResponse>> update(@RequestParam String channel, @RequestParam CallStatus status) {
        ApiResponse<StartCallResponse> response = service.update(channel, status);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

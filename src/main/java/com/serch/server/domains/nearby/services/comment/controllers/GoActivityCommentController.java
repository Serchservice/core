package com.serch.server.domains.nearby.services.comment.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.services.comment.requests.GoActivityCommentRequest;
import com.serch.server.domains.nearby.services.comment.responses.GoActivityCommentResponse;
import com.serch.server.domains.nearby.services.comment.services.GoActivityCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/go/comment")
public class GoActivityCommentController {
    private final GoActivityCommentService service;

    @GetMapping("/{activity}")
    public ResponseEntity<ApiResponse<List<GoActivityCommentResponse>>> getComments(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @PathVariable String activity
    ) {
        ApiResponse<List<GoActivityCommentResponse>> response = service.getComments(page, size, activity);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoActivityCommentResponse>> comment(@RequestBody GoActivityCommentRequest request) {
        ApiResponse<GoActivityCommentResponse> response = service.comment(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
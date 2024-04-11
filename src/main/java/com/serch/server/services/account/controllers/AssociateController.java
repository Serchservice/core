package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AddAssociateRequest;
import com.serch.server.services.account.services.AssociateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/associate")
public class AssociateController {
    private final AssociateService service;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<String>> add(@RequestBody AddAssociateRequest request) {
        ApiResponse<String> response = service.add(request);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<String>> delete(@RequestParam UUID id) {
        ApiResponse<String> response = service.delete(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/activate")
    public ResponseEntity<ApiResponse<String>> activate(@RequestParam UUID id) {
        ApiResponse<String> response = service.activate(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/deactivate()")
    public ResponseEntity<ApiResponse<String>> deactivate(@RequestParam UUID id) {
        ApiResponse<String> response = service.deactivate(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

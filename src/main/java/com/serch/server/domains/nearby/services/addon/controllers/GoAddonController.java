package com.serch.server.domains.nearby.services.addon.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.domains.nearby.services.addon.responses.GoAddonResponse;
import com.serch.server.domains.nearby.services.addon.responses.GoAddonVerificationResponse;
import com.serch.server.domains.nearby.services.addon.responses.GoUserAddonResponse;
import com.serch.server.domains.nearby.services.addon.services.GoAddonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/go/addon")
public class GoAddonController {
    private final GoAddonService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoAddonResponse>>> get() {
        ApiResponse<List<GoAddonResponse>> response = service.get();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<GoUserAddonResponse>>> getAll() {
        ApiResponse<List<GoUserAddonResponse>> response = service.getAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/create")
    public ResponseEntity<ApiResponse<InitializePaymentData>> init(@RequestParam String id) {
        ApiResponse<InitializePaymentData> response = service.init(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/see")
    public ResponseEntity<ApiResponse<GoAddonVerificationResponse>> see(@RequestParam String reference) {
        ApiResponse<GoAddonVerificationResponse> response = service.see(reference);
        return new  ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/add")
    public ResponseEntity<ApiResponse<List<GoUserAddonResponse>>> add(@RequestParam String reference) {
        ApiResponse<List<GoUserAddonResponse>> response = service.add(reference);
        return new  ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/switch")
    public ResponseEntity<ApiResponse<Object>> change(@RequestParam String id, @RequestParam Boolean useExistingAuthorization) {
        ApiResponse<Object> response = service.change(id, useExistingAuthorization);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/cancel")
    public ResponseEntity<ApiResponse<List<GoUserAddonResponse>>> cancel(@RequestParam Long id) {
        ApiResponse<List<GoUserAddonResponse>> response = service.cancel(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/switch/cancel")
    public ResponseEntity<ApiResponse<List<GoUserAddonResponse>>> cancelSwitch(@RequestParam Long id) {
        ApiResponse<List<GoUserAddonResponse>> response = service.cancelSwitch(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/activate")
    public ResponseEntity<ApiResponse<List<GoUserAddonResponse>>> activate(@RequestParam Long id) {
        ApiResponse<List<GoUserAddonResponse>> response = service.activate(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/renew")
    public ResponseEntity<ApiResponse<Object>> renew(@RequestParam Long id, @RequestParam Boolean useExistingAuthorization) {
        ApiResponse<Object> response = service.renew(id, useExistingAuthorization);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
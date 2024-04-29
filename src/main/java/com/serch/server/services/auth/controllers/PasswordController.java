package com.serch.server.services.auth.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.auth.requests.RequestPasswordChange;
import com.serch.server.services.auth.requests.RequestResetPassword;
import com.serch.server.services.auth.requests.RequestResetPasswordVerify;
import com.serch.server.services.auth.responses.AuthResponse;
import com.serch.server.services.auth.services.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/password")
public class PasswordController {
    private final PasswordService resetService;

    @GetMapping("/reset/check")
    public ResponseEntity<ApiResponse<String>> checkEmail(@RequestParam String emailAddress) {
        var response = resetService.checkEmail(emailAddress);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/reset/verify")
    public ResponseEntity<ApiResponse<String>> verifyOTP(@RequestBody RequestResetPasswordVerify verify) {
        var response = resetService.verifyToken(verify);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody RequestResetPassword password) {
        var response = resetService.resetPassword(password);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/change")
    public ResponseEntity<ApiResponse<AuthResponse>> changePassword(@RequestBody RequestPasswordChange request) {
        var response = resetService.changePassword(request);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

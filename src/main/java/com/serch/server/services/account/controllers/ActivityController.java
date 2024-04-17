package com.serch.server.services.account.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.responses.ActivityResponse;
import com.serch.server.services.account.services.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activity")
public class ActivityController {
    private final ActivityService service;

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<ActivityResponse>> today() {
        ApiResponse<ActivityResponse> response = service.today();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
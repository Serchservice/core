package com.serch.server.services.subscription.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.subscription.responses.PlanParentResponse;
import com.serch.server.services.subscription.services.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanService planService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PlanParentResponse>>> plans() {
        ApiResponse<List<PlanParentResponse>> response = planService.plans();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
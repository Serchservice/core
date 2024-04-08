package com.serch.server.services.subscription.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.services.subscription.responses.PlanParentResponse;
import com.serch.server.services.subscription.services.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanController {
    private final PlanService planService;

    @GetMapping
    public ResponseEntity<ApiResponse<PlanParentResponse>> getPlan(@RequestParam PlanType type) {
        var response = planService.getPlan(type);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<PlanParentResponse>>> getPlans() {
        var response = planService.getPlans();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
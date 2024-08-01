package com.serch.server.admin.services.scopes.marketing;

import com.serch.server.admin.services.scopes.marketing.responses.MarketingResponse;
import com.serch.server.admin.services.scopes.marketing.responses.NewsletterResponse;
import com.serch.server.bases.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/marketing")
@PreAuthorize("hasRole('SUPER_ADMIN') || hasRole('ADMIN') || hasRole('MANAGER') || hasRole('TEAM')")
public class MarketingScopeController {
    private final MarketingScopeService service;

    @GetMapping
    public ResponseEntity<ApiResponse<MarketingResponse>> overview() {
        ApiResponse<MarketingResponse> response = service.overview();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/newsletters")
    public ResponseEntity<ApiResponse<List<NewsletterResponse>>> newsletters() {
        ApiResponse<List<NewsletterResponse>> response = service.newsletters();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PatchMapping("/newsletter")
    public ResponseEntity<ApiResponse<List<NewsletterResponse>>> update(@RequestParam Long id) {
        ApiResponse<List<NewsletterResponse>> response = service.update(id);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

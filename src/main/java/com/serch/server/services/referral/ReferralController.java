package com.serch.server.services.referral;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.referral.responses.ReferralProgramResponse;
import com.serch.server.services.referral.responses.ReferralResponse;
import com.serch.server.services.referral.services.ReferralProgramService;
import com.serch.server.services.referral.services.ReferralService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/referral")
public class ReferralController {
    private final ReferralService service;
    private final ReferralProgramService programService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ReferralResponse>>> viewReferrals() {
        var response = service.viewReferrals();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/program")
    public ResponseEntity<ApiResponse<ReferralProgramResponse>> program() {
        ApiResponse<ReferralProgramResponse> response = programService.program();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/program/verify/link")
    public ResponseEntity<ApiResponse<ReferralProgramResponse>> verifyLink(@RequestParam String link) {
        ApiResponse<ReferralProgramResponse> response = programService.verifyLink(link);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/program/verify/code")
    public ResponseEntity<ApiResponse<ReferralProgramResponse>> verifyCode(@RequestParam String code) {
        ApiResponse<ReferralProgramResponse> response = programService.verifyCode(code);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

package com.serch.server.services.email;

import com.resend.services.emails.model.SendEmailResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.models.email.SendEmail;
import com.serch.server.services.email.services.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {
    private final EmailAuthService service;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<SendEmailResponse>> send(@RequestBody SendEmail email) {
        ApiResponse<SendEmailResponse> response = service.send(email);
        return new ResponseEntity<>(response, response.getStatus());
    }
}

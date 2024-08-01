package com.serch.server.services.transaction.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.responses.Bank;
import com.serch.server.core.payment.responses.BankAccount;
import com.serch.server.services.transaction.services.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/banking")
public class BankController {
    private final BankService service;

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<BankAccount>> verify(@RequestParam String number, @RequestParam String code) {
        ApiResponse<BankAccount> response = service.verify(number, code);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/banks")
    public ResponseEntity<ApiResponse<List<Bank>>> banks() {
        ApiResponse<List<Bank>> response = service.banks();
        return ResponseEntity.ok(response);
    }
}
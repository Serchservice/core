package com.serch.server.services.transaction.controllers;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.transaction.responses.TransactionResponse;
import com.serch.server.services.transaction.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService service;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> transactions() {
        var response = service.transactions();
        return new ResponseEntity<>(response, response.getStatus());
    }
}

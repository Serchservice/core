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

/**
 * The TransactionController class handles HTTP requests related to transaction operations.
 * It provides endpoints for retrieving all transactions.
 *
 * @see TransactionService
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService service;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> transactions() {
        ApiResponse<List<TransactionResponse>> response = service.transactions();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/subscription")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> subscriptions() {
        ApiResponse<List<TransactionResponse>> response = service.subscriptions();
        return new ResponseEntity<>(response, response.getStatus());
    }
}
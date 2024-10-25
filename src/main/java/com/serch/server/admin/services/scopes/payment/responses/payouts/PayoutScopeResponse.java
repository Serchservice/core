package com.serch.server.admin.services.scopes.payment.responses.payouts;

import lombok.Data;

import java.util.List;

@Data
public class PayoutScopeResponse {
    private String label;
    private List<PayoutResponse> payouts;
}
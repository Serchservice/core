package com.serch.server.core.payment.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BankAccountResponse extends BasePaymentApiResponse {
    private BankAccount data;
}
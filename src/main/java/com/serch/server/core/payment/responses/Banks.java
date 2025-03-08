package com.serch.server.core.payment.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class Banks extends BasePaymentApiResponse {
    private List<Bank> data;
}

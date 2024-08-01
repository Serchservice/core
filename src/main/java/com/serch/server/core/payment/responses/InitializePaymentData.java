package com.serch.server.core.payment.responses;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InitializePaymentData {
    private String authorization_url;
    private String access_code;
    private String reference;
}

package com.serch.server.domains.auth.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MFADataResponse{
        private String secret;

        @JsonProperty("qr_code")
        private String qrCode;
}

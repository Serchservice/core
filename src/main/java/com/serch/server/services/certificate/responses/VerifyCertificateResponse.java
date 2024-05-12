package com.serch.server.services.certificate.responses;

import com.serch.server.services.rating.responses.RatingResponse;
import lombok.Data;

import java.util.List;

@Data
public class VerifyCertificateResponse {
    private String document;
    private String picture;
    private CertificateData data;
    private List<RatingResponse> ratings;
}
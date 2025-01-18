package com.serch.server.domains.certificate.responses;

import lombok.Data;

@Data
public class CertificateData {
    private String document;
    private String id;
    private String content;
    private String header;
    private String qrCode;
    private String signature;
    private String issueDate;
    private String name;
    private String category;
    private String image;
}
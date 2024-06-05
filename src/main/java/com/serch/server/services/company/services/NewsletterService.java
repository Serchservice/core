package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;

public interface NewsletterService {
    ApiResponse<String> subscribe(String emailAddress);
    ApiResponse<String> unsubscribe(String emailAddress);
}
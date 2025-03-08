package com.serch.server.core.file.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public abstract class FileBuilderService {
    @Value("${application.supabase.api-key}")
    private String API_KEY;

    @Value("${application.supabase.base-url}")
    private String BASE_URL;

    public String supabase(String url) {
        return String.format("%s%s", BASE_URL, url);
    }

    public static FileBuilderService instance = new FileBuilderService() {
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    };
}
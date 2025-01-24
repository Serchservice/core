package com.serch.server.utils;

import com.serch.server.enums.auth.Role;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class LinkUtils {
    public static final LinkUtils instance = new LinkUtils();

    private final String STARTER = "http";
    private final String USER_BASE_URL = "https://user.serchservice.com";
    private final String PROVIDER_BASE_URL = "https://provider.serchservice.com";
    private final String BUSINESS_BASE_URL = "https://business.serchservice.com";

    // Generates a shared link with a role category and secret
    public String shared(String category, String secret) {
        return "%s/invite/shared?category=%s&shared_by=%s".formatted(USER_BASE_URL, category, secret);
    }

    // Extracts the "shared_by" value from the query or returns the secret if not present
    public String shared(String content) {
        if (content.startsWith(USER_BASE_URL) || content.startsWith(STARTER)) {
            Map<String, String> queryParams = extractQueryParams(content);
            return queryParams.getOrDefault("shared_by", content);
        } else {
            return content;
        }
    }

    // Generates a referral link based on the category and secret
    public String referral(Role category, String secret) {
        if (category == Role.USER) {
            return "%s/invite?ref=%s".formatted(USER_BASE_URL, secret);
        } else if (category == Role.BUSINESS) {
            return "%s/invite?ref=%s".formatted(BUSINESS_BASE_URL, secret);
        } else {
            return "%s/invite?ref=%s".formatted(PROVIDER_BASE_URL, secret);
        }
    }

    // Extracts the "ref" value from the query or returns the secret if not present
    public String referral(String content) {
        if (content.startsWith(PROVIDER_BASE_URL) || content.startsWith(BUSINESS_BASE_URL) || content.startsWith(USER_BASE_URL) || content.startsWith(STARTER)) {
            Map<String, String> queryParams = extractQueryParams(content);
            return queryParams.getOrDefault("ref", content);
        } else {
            return content;
        }
    }

    // Generates an invitation link with the role and secret
    public String invite(Role role, String secret) {
        return String.format("%s/invite/associate?invite=%s&role=%s", PROVIDER_BASE_URL, secret, role.getType());
    }

    // Extracts the "invite" value from the query or returns the secret if not present
    public String invite(String content) {
        if (content.startsWith(PROVIDER_BASE_URL) || content.startsWith(STARTER)) {
            Map<String, String> queryParams = extractQueryParams(content);
            return queryParams.getOrDefault("invite", content);
        } else {
            return content;
        }
    }

    // Helper method to extract query parameters from a URI
    private Map<String, String> extractQueryParams(String link) {
        Map<String, String> params = new HashMap<>();
        URI uri = URI.create(link);
        String query = uri.getQuery();

        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }

        return params;
    }
}
package com.serch.server.domains.nearby.services.auth.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoAuthTokenRequest(@JsonProperty("email_address") String emailAddress, String token) { }
package com.serch.server.services.shared.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedPricing;
import com.serch.server.services.shared.requests.CreateGuestRequest;
import com.serch.server.services.shared.requests.VerifyEmailRequest;
import com.serch.server.services.shared.responses.GuestAuthResponse;
import com.serch.server.services.shared.responses.GuestResponse;
import com.serch.server.services.shared.responses.SharedPricingData;

public interface GuestAuthService {
    ApiResponse<GuestAuthResponse> verifyLink(String link);
    GuestResponse response(SharedLink link, Guest guest);
    ApiResponse<String> askToVerifyEmail(VerifyEmailRequest request);
    ApiResponse<String> verifyEmailWithToken(VerifyEmailRequest request);
    ApiResponse<String> askToConfirmExistingEmailIdentity(VerifyEmailRequest request);
    ApiResponse<String> confirmExistingEmailIdentityWithToken(VerifyEmailRequest request);
    ApiResponse<GuestResponse> create(CreateGuestRequest request);
    ApiResponse<GuestResponse> login(VerifyEmailRequest request);
    ApiResponse<GuestResponse> createFromExistingUser(CreateGuestRequest request);
    ApiResponse<GuestResponse> addGuestAccountToLink(CreateGuestRequest request);
    SharedPricingData getSharedPricingData(SharedLink link, SharedPricing pricing);
}

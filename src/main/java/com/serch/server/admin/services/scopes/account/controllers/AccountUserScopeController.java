package com.serch.server.admin.services.scopes.account.controllers;

import com.serch.server.admin.services.responses.AnalysisResponse;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.admin.services.responses.auth.AccountMFAChallengeResponse;
import com.serch.server.admin.services.scopes.account.responses.user.*;
import com.serch.server.admin.services.scopes.account.services.AccountUserScopeService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.rating.responses.RatingChartResponse;
import com.serch.server.services.schedule.responses.ScheduleTimeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scope/account/user")
public class AccountUserScopeController {
    private final AccountUserScopeService service;

    @GetMapping
    public ResponseEntity<ApiResponse<AccountUserScopeProfileResponse>> profile(@RequestParam String id) {
        ApiResponse<AccountUserScopeProfileResponse> response = service.profile(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/auth")
    public ResponseEntity<ApiResponse<AccountUserScopeAuthResponse>> auth(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<AccountUserScopeAuthResponse> response = service.auth(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/rating")
    public ResponseEntity<ApiResponse<List<AccountUserScopeRatingResponse>>> rating(
            @RequestParam String id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeRatingResponse>> response = service.rating(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/rating/chart")
    public ResponseEntity<ApiResponse<List<RatingChartResponse>>> rating(@RequestParam String id) {
        ApiResponse<List<RatingChartResponse>> response = service.rating(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<AccountUserScopeWalletResponse>> wallet(@RequestParam UUID id) {
        ApiResponse<AccountUserScopeWalletResponse> response = service.wallet(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/certificate")
    public ResponseEntity<ApiResponse<AccountUserScopeCertificateResponse>> certificate(@RequestParam UUID id) {
        ApiResponse<AccountUserScopeCertificateResponse> response = service.certificate(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/analysis")
    public ResponseEntity<ApiResponse<AnalysisResponse>> analysis(@RequestParam UUID id) {
        ApiResponse<AnalysisResponse> response = service.analysis(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<List<AccountUserScopeTransactionResponse>>> transactions(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeTransactionResponse>> response = service.transactions(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/challenges")
    public ResponseEntity<ApiResponse<List<AccountMFAChallengeResponse>>> challenges(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountMFAChallengeResponse>> response = service.challenges(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<List<AccountUserScopeReportResponse>>> reports(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeReportResponse>> response = service.reports(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/associates")
    public ResponseEntity<ApiResponse<List<CommonProfileResponse>>> associates(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<CommonProfileResponse>> response = service.associates(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/shops")
    public ResponseEntity<ApiResponse<List<AccountUserScopeShopResponse>>> shops(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeShopResponse>> response = service.shops(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/schedules")
    public ResponseEntity<ApiResponse<List<AccountUserScopeScheduleResponse>>> schedules(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeScheduleResponse>> response = service.schedules(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/schedule/times")
    public ResponseEntity<ApiResponse<List<ScheduleTimeResponse>>> times(@RequestParam UUID id) {
        ApiResponse<List<ScheduleTimeResponse>> response = service.times(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<AccountUserScopeTicketResponse>>> tickets(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeTicketResponse>> response = service.tickets(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/referral")
    public ResponseEntity<ApiResponse<AccountUserScopeReferralResponse>> referral(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<AccountUserScopeReferralResponse> response = service.referral(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/referrals")
    public ResponseEntity<ApiResponse<List<AccountUserScopeReferralResponse.Referral>>> referrals(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeReferralResponse.Referral>> response = service.referrals(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<ApiResponse<List<AccountUserScopeBookmarkResponse>>> bookmarks(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeBookmarkResponse>> response = service.bookmarks(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/calls")
    public ResponseEntity<ApiResponse<List<AccountUserScopeCallResponse>>> calls(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeCallResponse>> response = service.calls(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/trips")
    public ResponseEntity<ApiResponse<List<AccountUserScopeTripResponse>>> trips(
            @RequestParam String id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeTripResponse>> response = service.trips(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chat/rooms")
    public ResponseEntity<ApiResponse<List<AccountUserScopeChatRoomResponse>>> chatRooms(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeChatRoomResponse>> response = service.chatRooms(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/shared/links")
    public ResponseEntity<ApiResponse<List<AccountUserScopeSharedLinkResponse>>> sharedLinks(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeSharedLinkResponse>> response = service.sharedLinks(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/shared/accounts")
    public ResponseEntity<ApiResponse<List<AccountUserScopeSharedAccountResponse>>> sharedAccounts(
            @RequestParam String id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        ApiResponse<List<AccountUserScopeSharedAccountResponse>> response = service.sharedAccounts(id, page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/auth")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> fetchAuthChart(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer year
    ) {
        ApiResponse<List<ChartMetric>> response = service.fetchAuthChart(id, year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/chart/account/status")
    public ResponseEntity<ApiResponse<List<ChartMetric>>> fetchAccountStatusChart(
            @RequestParam UUID id,
            @RequestParam(required = false) Integer year
    ) {
        ApiResponse<List<ChartMetric>> response = service.fetchAccountStatusChart(id, year);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<List<AccountUserScopeActivityResponse>>> fetchActivity(
            @RequestParam String id,
            @RequestParam(required = false) Integer year
    ) {
        ApiResponse<List<AccountUserScopeActivityResponse>> response = service.fetchActivity(id, year);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
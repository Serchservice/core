package com.serch.server.services.account.responses;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.services.rating.responses.RatingChartResponse;
import com.serch.server.services.rating.responses.RatingResponse;
import lombok.Data;

import java.util.List;

/**
 * Carries the account response for business associates.
 * <p>
 * Categories of status response:
 * <ol>
 *     <li>{@link AccountStatus#HAS_REPORTED_ISSUES} -> Subscription is pending for next charge</li>
 *     <li>{@link AccountStatus#ACTIVE} -> Subscription is active</li>
 *     <li>{@link AccountStatus#SUSPENDED} - Subscription is suspended by business</li>
 *     <li>{@link AccountStatus#BUSINESS_DEACTIVATED} - Not added yet</li>
 * </ol>
 */
@Data
public class BusinessAssociateResponse {
    private ProfileResponse profile;
    private List<RatingChartResponse> chart;
    private List<RatingResponse> good;
    private List<RatingResponse> bad;
    private Boolean verified;
    private AccountStatus status = AccountStatus.BUSINESS_DEACTIVATED;
}
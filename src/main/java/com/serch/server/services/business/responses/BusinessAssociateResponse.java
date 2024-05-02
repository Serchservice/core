package com.serch.server.services.business.responses;

import com.serch.server.enums.account.AccountStatus;
import lombok.Data;

import java.util.UUID;

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
    private String avatar;
    private String name;
    private String category;
    private String image;
    private UUID id;
    private AccountStatus status = AccountStatus.BUSINESS_DEACTIVATED;
}
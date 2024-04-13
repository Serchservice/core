package com.serch.server.services.account.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.account.requests.AccountReportRequest;

/**
 * This handles everything that has to do with reporting of a user, provider or shop.
 * <p></p>
 * The implementation of this interface can be found in {@link com.serch.server.services.account.services.implementations.AccountReportImplementation}
 * @see com.serch.server.models.shop.Shop
 * @see com.serch.server.models.account.Profile
 * @see com.serch.server.models.auth.User
 */
public interface AccountReportService {
    /**
     * @param request
     *
     * @return
     */
    ApiResponse<String> report(AccountReportRequest request);
}

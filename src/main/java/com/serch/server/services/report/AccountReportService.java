package com.serch.server.services.report;

import com.serch.server.bases.ApiResponse;

/**
 * This handles everything that has to do with reporting of a user, provider or shop.
 * <p></p>
 * The implementation of this interface can be found in {@link AccountReportImplementation}
 * @see com.serch.server.models.shop.Shop
 * @see com.serch.server.models.account.Profile
 * @see com.serch.server.models.auth.User
 */
public interface AccountReportService {
    /**
     * Reports a user account or a shop based on the provided request.
     *
     * @param request The AccountReportRequest containing the report details.
     * @return ApiResponse indicating the status of the report.
     */
    ApiResponse<String> report(AccountReportRequest request);
}

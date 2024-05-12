package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.ComplaintRequest;

/**
 * This is the wrapper class for making complaints in the Serch platform
 */
public interface ComplaintService {
    /**
     * Make a complaint to the Serch administrators
     *
     * @param request The complaint request
     *
     * @return ApiResponse {@link ApiResponse}
     *
     * @see ComplaintRequest
     */
    ApiResponse<String> complain(ComplaintRequest request);

    void removeOldContents();
}

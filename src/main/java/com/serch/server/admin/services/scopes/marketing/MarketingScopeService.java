package com.serch.server.admin.services.scopes.marketing;

import com.serch.server.admin.services.scopes.marketing.responses.MarketingResponse;
import com.serch.server.admin.services.scopes.marketing.responses.NewsletterResponse;
import com.serch.server.bases.ApiResponse;

import java.util.List;

public interface MarketingScopeService {
    /**
     * Get an overview of what's happening in the marketing department
     *
     * @return {@link ApiResponse} of {@link MarketingResponse}
     */
    ApiResponse<MarketingResponse> overview();

    /**
     * Get the list of newsletter subscriptions in Serch
     *
     * @return {@link ApiResponse} list of {@link NewsletterResponse}
     */
    ApiResponse<List<NewsletterResponse>> newsletters();

    /**
     * Update the status of a newsletter subscription to either
     * {@link com.serch.server.enums.company.NewsletterStatus#COLLECTED} or
     * {@link com.serch.server.enums.company.NewsletterStatus#UNCOLLECTED}
     *
     * @param id The id of the {@link NewsletterResponse} to update its status
     *
     * @return {@link ApiResponse} list of {@link NewsletterResponse}
     */
    ApiResponse<List<NewsletterResponse>> update(Long id);
}

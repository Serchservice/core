package com.serch.server.admin.services.scopes.support.services;

import com.serch.server.admin.services.scopes.support.responses.SpeakWithSerchOverviewResponse;
import com.serch.server.admin.services.scopes.support.responses.SpeakWithSerchScopeResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.IssueRequest;

import java.util.UUID;

public interface SpeakWithSerchScopeService {
    /**
     * Get an overview of all tickets in Serch for both assigned and others
     *
     * @return {@link ApiResponse} of {@link SpeakWithSerchOverviewResponse}
     */
    ApiResponse<SpeakWithSerchOverviewResponse> overview();

    /**
     * Reply a ticket opened by a user in the Serch platform. This will send back the updated data
     *
     * @param request The {@link IssueRequest} data for replying a ticket
     *
     * @return {@link ApiResponse} of {@link SpeakWithSerchScopeResponse}
     */
    ApiResponse<SpeakWithSerchScopeResponse> reply(IssueRequest request);

    /**
     * Find a ticket. This will send back the ticket data
     *
     * @param ticket The Ticket ID
     *
     * @return {@link ApiResponse} of {@link SpeakWithSerchScopeResponse}
     */
    ApiResponse<SpeakWithSerchScopeResponse> find(String ticket);

    /**
     * Resolve an open or pending ticket. This will send back the updated data
     *
     * @param ticket The Ticket ID
     *
     * @return {@link ApiResponse} of {@link SpeakWithSerchScopeResponse}
     */
    ApiResponse<SpeakWithSerchScopeResponse> resolve(String ticket);

    /**
     * Assign a ticket to an admin. This will send back the updated data
     *
     * @param ticket The Ticket ID
     * @param id The Admin id
     *
     * @return {@link ApiResponse} of {@link SpeakWithSerchScopeResponse}
     */
    ApiResponse<SpeakWithSerchScopeResponse> assign(String ticket, UUID id);

    /**
     * Close an open ticket. This will send back the updated data
     *
     * @param ticket The Ticket ID
     *
     * @return {@link ApiResponse} of {@link SpeakWithSerchScopeResponse}
     */
    ApiResponse<SpeakWithSerchScopeResponse> close(String ticket);
}
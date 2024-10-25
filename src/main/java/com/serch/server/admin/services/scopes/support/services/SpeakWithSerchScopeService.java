package com.serch.server.admin.services.scopes.support.services;

import com.serch.server.admin.services.scopes.support.responses.SpeakWithSerchOverviewResponse;
import com.serch.server.admin.services.scopes.support.responses.SpeakWithSerchScopeResponse;
import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.IssueRequest;

import java.util.UUID;

/**
 * Service interface for handling ticket-related operations in the Serch platform support system.
 * Provides methods for ticket management, including retrieval, replies, assignment, resolution, and closure.
 */
public interface SpeakWithSerchScopeService {

    /**
     * Retrieves an overview of all tickets in the Serch platform, including assigned and unassigned tickets.
     * This provides a high-level summary of the ticketing system status, helping administrators
     * to understand the current workload and ticket distribution.
     *
     * @return an {@link ApiResponse} containing a {@link SpeakWithSerchOverviewResponse}, which provides details
     *         about the number of tickets in various states (e.g., open, resolved, assigned).
     */
    ApiResponse<SpeakWithSerchOverviewResponse> overview();

    /**
     * Sends a reply to an existing ticket on the Serch platform, based on the details provided in the request.
     * This will update the ticket with the reply content, and the system may notify the user of the new response.
     *
     * @param request the {@link IssueRequest} containing the reply details, such as the message content and ticket ID.
     * @return an {@link ApiResponse} containing a {@link SpeakWithSerchScopeResponse}, representing the updated state
     *         of the ticket after the reply has been processed.
     */
    ApiResponse<SpeakWithSerchScopeResponse> reply(IssueRequest request);

    /**
     * Finds a specific ticket using its unique identifier.
     * This allows administrators to view the details of a ticket, including its status, history, and content.
     *
     * @param ticket the unique ID of the ticket to be retrieved.
     * @return an {@link ApiResponse} containing a {@link SpeakWithSerchScopeResponse}, which provides detailed information
     *         about the requested ticket, such as the issue description, current status, and any related updates.
     */
    ApiResponse<SpeakWithSerchScopeResponse> find(String ticket);

    /**
     * Resolves a ticket on the Serch platform, changing its status from open or pending to resolved.
     * The updated ticket information is returned, reflecting the resolution action.
     *
     * @param ticket the unique ID of the ticket to be resolved.
     * @return an {@link ApiResponse} containing a {@link SpeakWithSerchScopeResponse}, representing the updated state
     *         of the ticket after it has been marked as resolved.
     */
    ApiResponse<SpeakWithSerchScopeResponse> resolve(String ticket);

    /**
     * Assigns a ticket to a specific admin on the Serch platform, allowing the admin to manage the ticket.
     * This will update the ticket's assignment information and may notify the assigned admin of the new ticket.
     *
     * @param ticket the unique ID of the ticket to be assigned.
     * @param id     the unique ID of the admin to whom the ticket will be assigned.
     * @return an {@link ApiResponse} containing a {@link SpeakWithSerchScopeResponse}, which provides the updated
     *         assignment details for the ticket.
     */
    ApiResponse<SpeakWithSerchScopeResponse> assign(String ticket, UUID id);

    /**
     * Closes an open ticket on the Serch platform, indicating that the ticket has been resolved or no further action is required.
     * This will update the ticket's status to closed and finalize any remaining actions associated with the ticket.
     *
     * @param ticket the unique ID of the ticket to be closed.
     * @return an {@link ApiResponse} containing a {@link SpeakWithSerchScopeResponse}, representing the final state
     *         of the ticket after it has been closed.
     */
    ApiResponse<SpeakWithSerchScopeResponse> close(String ticket);
}
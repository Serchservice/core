package com.serch.server.services.company.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.company.requests.IssueRequest;
import com.serch.server.services.company.responses.IssueResponse;
import com.serch.server.services.company.responses.SpeakWithSerchResponse;

import java.util.List;

public interface SpeakWithSerchService {
    /**
     * Lodges an issue for a specific product.
     *
     * @param request The IssueRequest object containing issue details.
     * @return ApiResponse containing a message with the issue submission status.
     *
     * @see ApiResponse
     * @see IssueRequest
     * @see SpeakWithSerchResponse
     */
    ApiResponse<SpeakWithSerchResponse> lodgeIssue(IssueRequest request);

    /**
     * Fetches all Speak With Serch messages
     *
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return ApiResponse containing all messages.
     *
     * @see ApiResponse
     * @see SpeakWithSerchResponse
     */
    ApiResponse<List<SpeakWithSerchResponse>> messages(Integer page, Integer size);

    /**
     * Fetches all issues belonging to a ticket.
     *
     * @param ticket The ticket to fetch its issues.
     * @param page The page number to retrieve (zero-based index).
     * @param size The number of items per page.
     * @return ApiResponse containing all messages.
     *
     * @see ApiResponse
     * @see IssueResponse
     */
    ApiResponse<List<IssueResponse>> issues(String ticket, Integer page, Integer size);

    /**
     * Marks all the message in a SpeakWithSerch record read
     *
     * @param ticket The SpeakWithSerch ticket
     *
     * @return ApiResponse of {@link SpeakWithSerchResponse} list
     */
    ApiResponse<List<SpeakWithSerchResponse>> markRead(String ticket);

    void removeOldContents();
}
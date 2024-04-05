package com.serch.server.services.help.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.services.help.requests.HelpAskRequest;
import com.serch.server.services.help.response.*;

import java.util.List;
import java.util.UUID;

public interface HelpService {
    ApiResponse<List<HelpCategoryResponse>> fetchCategories();
    ApiResponse<HelpCategoryResponse> fetchCategory(String category);
    ApiResponse<List<HelpSectionResponse>> fetchSections(String category);
    ApiResponse<HelpSectionResponse> fetchSection(String category);
    ApiResponse<List<HelpGroupResponse>> fetchGroups(String category, String section);
    ApiResponse<HelpGroupResponse> fetchGroup(String key);
    ApiResponse<HelpResponse> fetchHelp(UUID id);
    ApiResponse<List<HelpSearchResponse>> search(String query);
    ApiResponse<String> ask(HelpAskRequest request);
}

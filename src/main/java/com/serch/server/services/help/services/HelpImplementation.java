package com.serch.server.services.help.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.exceptions.HelpException;
import com.serch.server.services.help.HelpMapper;
import com.serch.server.services.help.models.*;
import com.serch.server.services.help.requests.HelpAskRequest;
import com.serch.server.services.help.response.*;
import com.serch.server.services.help.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HelpImplementation implements HelpService {
    private final HelpCategoryRepository categoryRepository;
    private final HelpSectionRepository sectionRepository;
    private final HelpRepository helpRepository;
    private final HelpGroupRepository groupRepository;
    private final HelpAskRepository askRepository;

    @Override
    public ApiResponse<List<HelpCategoryResponse>> fetchCategories() {
        return new ApiResponse<>(
                "Categories fetched",
                categoryRepository.findAll()
                        .stream()
                        .map(HelpMapper.INSTANCE::response)
                        .toList(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<HelpCategoryResponse> fetchCategory(String key) {
        HelpCategory category = categoryRepository.findById(key)
                .orElseThrow(() -> new HelpException("Category not found"));
        return new ApiResponse<>(
                "Category fetched",
                HelpMapper.INSTANCE.response(category),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<List<HelpSectionResponse>> fetchSections(String key) {
        HelpCategory category = categoryRepository.findById(key)
                .orElseThrow(() -> new HelpException("Category not found"));

        return new ApiResponse<>(
                "Sections fetched",
                category.getSections()
                        .stream()
                        .map(HelpMapper.INSTANCE::response)
                        .toList(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<HelpSectionResponse> fetchSection(String key) {
        HelpSection section = sectionRepository.findById(key)
                .orElseThrow(() -> new HelpException("Section not found"));
        return new ApiResponse<>(
                "Category fetched",
                HelpMapper.INSTANCE.response(section),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<List<HelpGroupResponse>> fetchGroups(String category, String section) {
        HelpSection foundSection = sectionRepository.findByCategory_KeyAndKey(category, section)
                .orElseThrow(() -> new HelpException("Section not found"));

        return new ApiResponse<>(
                "Groups fetched",
                foundSection.getGroups()
                        .stream()
                        .map(group -> {
                            HelpGroupResponse groupResponse = HelpMapper.INSTANCE.response(group);
                            groupResponse.setHelpResponses(group.getHelps()
                                    .stream()
                                    .map(HelpMapper.INSTANCE::response)
                                    .toList());
                            return groupResponse;
                        })
                        .toList(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<HelpGroupResponse> fetchGroup(String key) {
        HelpGroup group = groupRepository.findById(key)
                .orElseThrow(() -> new HelpException("Group not found"));
        return new ApiResponse<>(
                "Category fetched",
                HelpMapper.INSTANCE.response(group),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<HelpResponse> fetchHelp(UUID id) {
        Help help = helpRepository.findById(id)
                .orElseThrow(() -> new HelpException("Help not found"));

        return new ApiResponse<>(
                "Help fetched",
                HelpMapper.INSTANCE.response(help),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<List<HelpSearchResponse>> search(String query) {
        return new ApiResponse<>(
                "Query found",
                helpRepository.search(query)
                        .stream()
                        .map(objects -> {
                            HelpSearchResponse response = new HelpSearchResponse();
                            response.setCategory((String) objects[1]);
                            response.setLink((String) objects[4]);
                            response.setImage((String) objects[3]);
                            response.setSection((String) objects[2]);
                            response.setQuestion((String) objects[0]);
                            return response;
                        })
                        .toList(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<String> ask(HelpAskRequest request) {
        HelpAsk ask = HelpMapper.INSTANCE.toAsk(request);
        askRepository.save(ask);
        return new ApiResponse<>(
                "Query submitted",
                null,
                HttpStatus.OK
        );
    }
}

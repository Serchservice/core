package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.company.SpecialtyKeyword;
import com.serch.server.repositories.company.SpecialtyKeywordRepository;
import com.serch.server.services.company.responses.SpecialtyKeywordResponse;
import com.serch.server.services.company.services.SpecialtyKeywordService;
import com.serch.server.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing keywords related to specialties.
 * It implements the {@link SpecialtyKeywordService} interface.
 *
 * @see SpecialtyKeywordRepository
 */
@Service
@RequiredArgsConstructor
public class SpecialtyKeywordImplementation implements SpecialtyKeywordService {
    private final SpecialtyKeywordRepository repository;

    @Override
    public ApiResponse<List<SpecialtyKeywordResponse>> getAllSpecialties(SerchCategory type) {
        List<SpecialtyKeywordResponse> specialties = repository
                .findByCategory(type)
                .stream()
                .map(this::getSpecialtyResponse)
                .collect(Collectors.toList());
        return new ApiResponse<>(
                "Request successfully processed",
                specialties,
                HttpStatus.OK
        );
    }

    @Override
    public SpecialtyKeywordResponse getSpecialtyResponse(SpecialtyKeyword keywordService) {
        SpecialtyKeywordResponse response = new SpecialtyKeywordResponse();
        response.setId(keywordService.getId());
        response.setSpecial(keywordService.getKeyword());
        response.setDifficulty(keywordService.getDifficulty());
        response.setTimeline(keywordService.getTimeline());
        response.setCategory(keywordService.getCategory());
        if(keywordService.getMinimumAmount() != null && keywordService.getMaximumAmount() != null) {
            response.setPriceRange("%s - %s".formatted(
                    MoneyUtil.formatToNaira(BigDecimal.valueOf(keywordService.getMinimumAmount())),
                    MoneyUtil.formatToNaira(BigDecimal.valueOf(keywordService.getMaximumAmount()))
            ));
        }
        return response;
    }

    @Override
    public ApiResponse<List<SpecialtyKeywordResponse>> searchService(String query) {
        List<SpecialtyKeywordResponse> keywords = repository.fullTextSearch(query)
                .stream()
                .map(this::getSpecialtyResponse)
                .toList();
        return new ApiResponse<>(keywords);
    }
}

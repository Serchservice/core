package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.company.SpecialtyKeyword;
import com.serch.server.repositories.company.SpecialtyKeywordRepository;
import com.serch.server.services.company.responses.SpecialtyKeywordResponse;
import com.serch.server.services.company.services.SpecialtyKeywordService;
import com.serch.server.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    public List<SpecialtyKeywordResponse> getAllSpecialties(SerchCategory type) {
        return repository.findByCategory(type) == null ? List.of() : repository
                .findByCategory(type)
                .stream()
                .map(this::getSpecialtyResponse)
                .collect(Collectors.toList());
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
        List<SpecialtyKeyword> result = repository.fullTextSearch(query);
        List<SpecialtyKeywordResponse> keywords = new ArrayList<>();

        if(!result.isEmpty()) {
            keywords = result.stream() == null ? List.of() : result.stream()
                    .map(this::getSpecialtyResponse)
                    .toList();
        }
        return new ApiResponse<>(keywords);
    }
}

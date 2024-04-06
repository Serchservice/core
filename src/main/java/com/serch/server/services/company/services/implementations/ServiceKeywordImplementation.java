package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.company.SpecialtyService;
import com.serch.server.repositories.company.SpecialtyServiceRepository;
import com.serch.server.services.auth.responses.SpecialtyResponse;
import com.serch.server.services.company.services.KeywordService;
import com.serch.server.utils.MoneyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceKeywordImplementation implements KeywordService {
    private final SpecialtyServiceRepository specialtyServiceRepository;

    @Override
    public ApiResponse<List<SpecialtyResponse>> getAllSpecialties(SerchCategory type) {
        List<SpecialtyResponse> specialties = specialtyServiceRepository
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
    public SpecialtyResponse getSpecialtyResponse(SpecialtyService keywordService) {
        SpecialtyResponse response = new SpecialtyResponse();
        response.setId(keywordService.getId());
        response.setSpecial(keywordService.getKeyword());
        response.setDifficulty(keywordService.getDifficulty());
        response.setTimeline(keywordService.getTimeline());
        response.setCategory(keywordService.getCategory());
        if(keywordService.getMinimumAmount() != null && keywordService.getMaximumAmount() != null) {
            response.setPriceRange("%s - %s".formatted(
                    MoneyUtil.formatAmountToNaira(BigDecimal.valueOf(keywordService.getMinimumAmount())),
                    MoneyUtil.formatAmountToNaira(BigDecimal.valueOf(keywordService.getMaximumAmount()))
            ));
        }
        return response;
    }
}

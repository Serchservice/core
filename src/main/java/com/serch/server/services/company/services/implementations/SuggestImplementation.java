package com.serch.server.services.company.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.exceptions.others.CompanyException;
import com.serch.server.mappers.CompanyMapper;
import com.serch.server.models.company.ServiceSuggest;
import com.serch.server.repositories.company.ServiceSuggestRepository;
import com.serch.server.services.company.requests.ServiceSuggestRequest;
import com.serch.server.services.company.services.SuggestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SuggestImplementation implements SuggestService {
    private final ServiceSuggestRepository serviceSuggestRepository;

    @Override
    @Transactional
    public ApiResponse<String> suggest(ServiceSuggestRequest request) {
        if(Arrays.stream(SerchCategory.values()).anyMatch(category -> request.getService().equalsIgnoreCase(category.getType()))) {
            throw new CompanyException("Service already exists in the Serch platform");
        } else {
            ServiceSuggest suggest = CompanyMapper.INSTANCE.response(request);
            serviceSuggestRepository.save(suggest);
            return new ApiResponse<>("Suggestion saved", HttpStatus.OK);
        }
    }
}

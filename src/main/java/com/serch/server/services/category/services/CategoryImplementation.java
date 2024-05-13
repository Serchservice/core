package com.serch.server.services.category.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.models.trip.Trip;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.category.SerchCategoryResponse;
import com.serch.server.services.company.services.SpecialtyKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryImplementation implements CategoryService {
    private final SpecialtyKeywordService keywordService;
    private final TripRepository tripRepository;

    @Override
    public ApiResponse<List<SerchCategoryResponse>> categories() {
        return new ApiResponse<>(getSerchCategoryResponses());
    }

    private List<SerchCategoryResponse> getSerchCategoryResponses() {
        return Arrays.stream(SerchCategory.values())
                .filter(serchCategory -> serchCategory != SerchCategory.USER && serchCategory != SerchCategory.BUSINESS && serchCategory != SerchCategory.GUEST)
                .map(category -> {
                    SerchCategoryResponse response = new SerchCategoryResponse();
                    response.setCategory(category);
                    response.setType(category.getType());
                    response.setImage(category.getImage());
                    response.setInformation("What skills do you have?");
                    response.setSpecialties(keywordService.getAllSpecialties(category));
                    return response;
                })
                .toList();
    }

    @Override
    public ApiResponse<List<SerchCategoryResponse>> popular() {
        Pageable pageable = PageRequest.of(0, 3);

        List<Trip> trips = tripRepository.findPopularCategories(pageable);
        List<SerchCategoryResponse> categories = trips != null && !trips.isEmpty()
                ? trips.stream().map(trip -> {
                    SerchCategoryResponse response = new SerchCategoryResponse();
                    response.setCategory(trip.getProvider().getCategory());
                    response.setType(trip.getProvider().getCategory().getType());
                    response.setImage(trip.getProvider().getCategory().getImage());
                    response.setInformation("What skills do you have?");
                    response.setSpecialties(keywordService.getAllSpecialties(trip.getProvider().getCategory()));
                    return response;
                }).toList()
                : getSerchCategoryResponses().size() > 3
                ? getSerchCategoryResponses().subList(0, 3)
                : getSerchCategoryResponses();
        return new ApiResponse<>(categories);
    }
}

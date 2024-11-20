package com.serch.server.services.category.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.mappers.CompanyMapper;
import com.serch.server.models.trip.Trip;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.category.SerchCategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryImplementation implements CategoryService {
    private final TripRepository tripRepository;

    @Override
    public ApiResponse<List<SerchCategoryResponse>> categories() {
        return new ApiResponse<>(getSerchCategoryResponses());
    }

    private SerchCategoryResponse response(SerchCategory category) {
        SerchCategoryResponse response = CompanyMapper.INSTANCE.response(category);
        response.setCategory(category);

        return response;
    }

    private List<SerchCategoryResponse> getSerchCategoryResponses() {
        return Arrays.stream(SerchCategory.values())
                .filter(serchCategory -> serchCategory != SerchCategory.USER)
                .filter(serchCategory -> serchCategory != SerchCategory.BUSINESS)
                .filter(serchCategory -> serchCategory != SerchCategory.GUEST)
                .filter(serchCategory -> serchCategory != SerchCategory.PERSONAL_SHOPPER)
                .map(this::response)
                .toList();
    }

    @Override
    public ApiResponse<List<SerchCategoryResponse>> popular() {
        Pageable pageable = PageRequest.of(0, 3);

        List<Trip> trips = tripRepository.findPopularCategories(pageable);
        List<SerchCategoryResponse> categories = trips != null && !trips.isEmpty()
                ? trips.stream().map(trip -> response(trip.getProvider().getCategory())).toList()
                : getSerchCategoryResponses().size() > 3
                ? getSerchCategoryResponses().subList(0, 3)
                : getSerchCategoryResponses();
        return new ApiResponse<>(categories);
    }
}

package com.serch.server.domains.nearby.services.interest.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.bases.BaseLocation;
import com.serch.server.domains.nearby.services.interest.services.GoInterestService;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.interest.GoInterest;
import com.serch.server.domains.nearby.models.go.interest.GoInterestCategory;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.models.go.user.GoUserInterest;
import com.serch.server.domains.nearby.repositories.go.GoInterestCategoryRepository;
import com.serch.server.domains.nearby.repositories.go.GoInterestRepository;
import com.serch.server.domains.nearby.repositories.go.GoUserInterestRepository;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestCategoryResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestResponse;
import com.serch.server.domains.nearby.services.interest.responses.GoInterestUpdateResponse;
import com.serch.server.domains.nearby.utils.GoUtils;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoInterestImplementation implements GoInterestService {
    private final GoInterestRepository goInterestRepository;
    private final GoUserInterestRepository goUserInterestRepository;
    private final AuthUtil authUtil;
    private final GoInterestCategoryRepository goInterestCategoryRepository;
    private final GoUtils goUtils;

    @Override
    @Transactional
    public ApiResponse<GoInterestUpdateResponse> add(List<Long> interestIds) {
        GoUser user = authUtil.getGoUser();
        Set<Long> existingInterestIds = goUserInterestRepository.findInterestIdsByUser_Id(user.getId());

        List<Long> newInterestIds = interestIds.stream().filter(id -> !existingInterestIds.contains(id)).toList();
        if (!newInterestIds.isEmpty()) {
            newInterestIds.forEach(interest -> put(interest, user));
        }

        return getUpdate(user);
    }

    private ApiResponse<GoInterestUpdateResponse> getUpdate(GoUser user) {
        GoInterestUpdateResponse response = new GoInterestUpdateResponse();

        List<GoInterestCategoryResponse> taken = taken(user);
        response.setTaken(taken);
        response.setReserved(reserved(user, taken.stream().flatMap(c -> c.getInterests().stream()).map(GoInterestResponse::getId).toList()));

        return new ApiResponse<>(response);
    }

    private List<GoInterestCategoryResponse> taken(GoUser user) {
        List<GoUserInterest> interests = goUserInterestRepository.findByUser_Id(user.getId());
        List<GoInterestCategoryResponse> response = new ArrayList<>();

        if(interests != null && !interests.isEmpty()) {
            Map<GoInterestCategory, List<GoUserInterest>> cat = interests.stream()
                    .collect(Collectors.groupingBy(i -> i.getInterest().getCategory()));

            cat.forEach((value, list) -> response.add(prepare(
                    value,
                    list.stream().map(GoUserInterest::getInterest).toList(),
                    user.getLocation().getLatitude(),
                    user.getLocation().getLongitude(),
                    user.getSearchRadius()
            )));
        }

        return response;
    }

    private List<GoInterestCategoryResponse> reserved(GoUser user, List<Long> ids) {
        List<GoInterest> interests = goInterestRepository.findReserved(ids);
        List<GoInterestCategoryResponse> response = new ArrayList<>();

        if(interests != null && !interests.isEmpty()) {
            Map<GoInterestCategory, List<GoInterest>> cat = interests.stream()
                    .collect(Collectors.groupingBy(GoInterest::getCategory));

            cat.forEach((value, list) -> response.add(prepare(
                    value,
                    list,
                    user.getLocation().getLatitude(),
                    user.getLocation().getLongitude(),
                    user.getSearchRadius()
            )));
        }

        return response;
    }

    @Override
    @Transactional
    public ApiResponse<GoInterestUpdateResponse> delete(List<Long> ids) {
        GoUser user = authUtil.getGoUser();
        ids.removeIf(Objects::isNull);

        if (!ids.isEmpty()) {
            ids.forEach(id -> goUserInterestRepository.deleteByInterestAndUser(id, user.getId()));
            goInterestRepository.flush();
        }

        return getUpdate(user);
    }

    @Override
    public ApiResponse<GoInterestUpdateResponse> getUpdate() {
        return getUpdate(authUtil.getGoUser());
    }

    @Override
    public ApiResponse<List<GoInterestResponse>> get() {
        GoUser user = authUtil.getGoUser();

        List<GoUserInterest> interests = goUserInterestRepository.findByUser_Id(user.getId());

        List<GoInterestResponse> response = new ArrayList<>();
        if(interests != null && !interests.isEmpty()) {
            response = interests.stream().map(GoUserInterest::getInterest).toList().stream().map(category -> prepare(
                    category,
                    user.getLocation().getLatitude(),
                    user.getLocation().getLongitude(),
                    user.getSearchRadius()
            )).toList();
        }

        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<List<GoInterestResponse>> getAll(BaseLocation location) {
        Optional<GoUser> user = authUtil.getOptionalGoUser();

        List<GoInterest> interests = goInterestRepository.findAll();
        List<GoInterestResponse> response = new ArrayList<>();

        if(!interests.isEmpty()) {
            response = interests.stream().map(category -> prepare(
                    category,
                    user.isPresent() ? user.get().getLocation().getLatitude() : location.getLatitude(),
                    user.isPresent() ? user.get().getLocation().getLongitude() : location.getLongitude(),
                    user.isPresent() ? user.get().getSearchRadius() : 5000
            )).toList();
        }

        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<GoInterestResponse> get(Long id, BaseLocation location) {
        Optional<GoUser> user = authUtil.getOptionalGoUser();

        GoInterest interest = goInterestRepository.findById(id)
                .orElseThrow(() -> new SerchException("GoInterest not found"));

        return new ApiResponse<>(prepare(
                interest,
                user.isPresent() ? user.get().getLocation().getLatitude() : location.getLatitude(),
                user.isPresent() ? user.get().getLocation().getLongitude() : location.getLongitude(),
                user.isPresent() ? user.get().getSearchRadius() : 5000
        ));
    }

    @Override
    public ApiResponse<List<GoInterestCategoryResponse>> getCategory() {
        return new ApiResponse<>(taken(authUtil.getGoUser()));
    }

    @Override
    public ApiResponse<List<GoInterestCategoryResponse>> getAllCategories(BaseLocation location) {
        Optional<GoUser> user = authUtil.getOptionalGoUser();

        List<GoInterestCategory> categories = goInterestCategoryRepository.findAll();
        List<GoInterestCategoryResponse> response = new ArrayList<>();

        if(!categories.isEmpty()) {
            response = categories.stream()
                    .map(category -> prepare(
                            category,
                            category.getInterests(),
                            user.isPresent() ? user.get().getLocation().getLatitude() : location.getLatitude(),
                            user.isPresent() ? user.get().getLocation().getLongitude() : location.getLongitude(),
                            user.isPresent() ? user.get().getSearchRadius() : 5000
                    ))
                    .toList();
        }

        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<GoInterestCategoryResponse> getCategory(Long id, BaseLocation location) {
        Optional<GoUser> user = authUtil.getOptionalGoUser();

        GoInterestCategory category = goInterestCategoryRepository.findById(id)
                .orElseThrow(() -> new SerchException("GoInterest category not found"));

        return new ApiResponse<>(prepare(
                category,
                category.getInterests(),
                user.isPresent() ? user.get().getLocation().getLatitude() : location.getLatitude(),
                user.isPresent() ? user.get().getLocation().getLongitude() : location.getLongitude(),
                user.isPresent() ? user.get().getSearchRadius() : 5000
        ));
    }

    @Override
    public GoInterestResponse prepare(GoInterest interest, double lng, double lat, double radius) {
        GoInterestResponse response = GoMapper.instance.interest(interest);
        response.setPopularity(goUtils.calculateAndUpdatePopularity(interest));
        response.setNearbyPopularity(goUtils.calculateNearbyPopularity(interest, lat, lng, radius));

        return response;
    }

    @Override
    public GoInterestCategoryResponse prepare(GoInterestCategory category, List<GoInterest> interests, double lng, double lat, double radius) {
        GoInterestCategoryResponse response = GoMapper.instance.category(category);

        if(interests != null && !interests.isEmpty()) {
            response.setInterests(interests.stream().map(interest -> prepare(interest, lat, lng, radius)).toList());
        }

        return response;
    }

    @Override
    public GoUserInterest put(Long id, GoUser user) {
        return goUserInterestRepository.findByInterest_IdAndUser_Id(id, user.getId())
                .orElseGet(() -> {
                    GoInterest interest = goInterestRepository.findById(id)
                            .orElseThrow(() -> new SerchException("GoInterest not found"));

                    GoUserInterest userInterest = new GoUserInterest();
                    userInterest.setUser(user);
                    userInterest.setInterest(interest);
                    return goUserInterestRepository.save(userInterest);
                });
    }
}
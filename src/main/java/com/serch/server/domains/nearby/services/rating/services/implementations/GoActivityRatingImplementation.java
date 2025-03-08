package com.serch.server.domains.nearby.services.rating.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.activity.GoActivityRating;
import com.serch.server.domains.nearby.services.rating.requests.GoActivityRatingRequest;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.repositories.go.GoActivityRatingRepository;
import com.serch.server.domains.nearby.repositories.go.GoActivityRepository;
import com.serch.server.domains.nearby.services.rating.responses.GoActivityRatingResponse;
import com.serch.server.domains.nearby.services.rating.services.GoActivityRatingService;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class GoActivityRatingImplementation implements GoActivityRatingService {
    private final AuthUtil authUtil;
    private final GoActivityRepository goActivityRepository;
    private final GoActivityRatingRepository goActivityRatingRepository;

    @Override
    public ApiResponse<GoActivityRatingResponse> rate(GoActivityRatingRequest request) {
        GoUser user = authUtil.getGoUser();
        GoActivity activity = goActivityRepository.findById(request.getId()).orElseThrow(() -> new SerchException("Activity not found"));

        if(activity.isEnded()) {
            AtomicReference<GoActivityRating> goRating = new AtomicReference<>();

            goActivityRatingRepository.findByUserAndActivity(user.getId(), activity.getId()).ifPresentOrElse(rating -> {
                rating.setActivity(activity);
                rating.setRating(request.getRating());
                rating.setUpdatedAt(TimeUtil.now());
                goRating.set(goActivityRatingRepository.save(rating));
            }, () -> {
                GoActivityRating rating = new GoActivityRating();
                rating.setUser(user);
                rating.setActivity(activity);
                rating.setRating(request.getRating());
                goRating.set(goActivityRatingRepository.save(rating));
            });

            return new ApiResponse<>(
                    "Rating successfully received",
                    getRating(goRating.get()),
                    HttpStatus.OK
            );
        } else {
            throw new SerchException("You cannot rate an active activity. This activity must end before doing this");
        }
    }

    @Override
    public ApiResponse<List<GoActivityRatingResponse>> getRatings(Integer page, Integer size, String id) {
        GoActivity activity = goActivityRepository.findById(id).orElseThrow(() -> new SerchException("Activity not found"));

        if(activity.isEnded()) {
            Pageable pageable = PageRequest.of(
                    page == null ? 0 : page,
                    size == null ? 20 : size,
                    Sort.by(Sort.Direction.ASC, "createdAt")
            );

            Page<GoActivityRating> comments = goActivityRatingRepository.findByActivity_Id(activity.getId(), pageable);

            if(comments.isEmpty()) {
                return new ApiResponse<>(new ArrayList<>());
            } else {
                return new ApiResponse<>(comments.getContent().stream().map(this::getRating).toList());
            }
        }

        return new ApiResponse<>(new ArrayList<>());
    }

    private GoActivityRatingResponse getRating(GoActivityRating rating) {
        GoActivityRatingResponse response = GoMapper.instance.rating(rating);

        authUtil.getOptionalGoUser().ifPresentOrElse(
                user -> response.setIsCurrentUser(rating.getUser().getId().equals(user.getId())),
                () -> response.setIsCurrentUser(false)
        );

        return response;
    }
}
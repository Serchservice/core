package com.serch.server.domains.nearby.services.bcap.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.linked.dtos.LinkedDynamicUrlDto;
import com.serch.server.domains.linked.services.LinkedDynamicUrlService;
import com.serch.server.domains.nearby.repositories.go.*;
import com.serch.server.domains.nearby.services.bcap.requests.GoBCapCreateRequest;
import com.serch.server.domains.nearby.services.bcap.responses.GoBCapResponse;
import com.serch.server.domains.nearby.services.bcap.services.GoBCapLifecycleService;
import com.serch.server.domains.nearby.services.bcap.services.GoBCapService;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.GoBCap;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.activity.GoActivityRating;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.services.media.services.GoMediaService;
import com.serch.server.domains.nearby.utils.GoOptional;
import com.serch.server.domains.nearby.utils.GoUtils;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoBCapImplementation implements GoBCapService {
    private final GoMediaService mediaService;
    private final LinkedDynamicUrlService urlService;
    private final GoBCapLifecycleService lifecycleService;
    private final GoActivityRepository goActivityRepository;
    private final GoBCapRepository goBCapRepository;
    private final GoActivityRatingRepository goActivityRatingRepository;
    private final AuthUtil authUtil;
    private final GoUserInterestRepository goUserInterestRepository;
    private final GoActivityCommentRepository goActivityCommentRepository;
    private final GoUtils goUtils;

    @Override
    @Transactional
    public ApiResponse<GoBCapResponse> create(GoBCapCreateRequest request) {
        if(request.getMedia() == null || request.getMedia().isEmpty()) {
            throw new SerchException("You need to add videos or images that users can view");
        }

        GoUser user = authUtil.getGoUser();
        GoActivity activity = goActivityRepository.findById(request.getId())
                .orElseThrow(() -> new SerchException("Activity not found"));

        if(activity.getUser().getId().equals(user.getId())) {
            Optional<GoBCap> existing = goBCapRepository.findByActivity_Id(request.getId());
            if(existing.isPresent()) {
                throw new SerchException("A BCap for this activity already exists");
            } else {
                GoBCap cap = new GoBCap();
                cap.setActivity(activity);
                cap.setName(activity.getName());
                cap.setDescription(request.getDescription() != null && !request.getDescription().isEmpty()
                        ? request.getDescription()
                        : activity.getDescription()
                );
                cap.setUser(user);

                cap = goBCapRepository.save(cap);

                try {
                    cap.setMedia(mediaService.upload(request.getMedia(), cap));

                    lifecycleService.onCreated(cap);

                    return new ApiResponse<>(prepare(cap));
                } catch (Exception e) {
                    goBCapRepository.delete(cap);
                    throw new SerchException(e.getMessage());
                }
            }
        } else {
            throw new SerchException("You cannot create a BCap on an activity you did not create");
        }
    }

    private GoBCapResponse prepare(GoBCap cap) {
        GoBCapResponse response = GoMapper.instance.cap(cap);
        response.setRating(goActivityRatingRepository.findAverageRatingByActivity(cap.getActivity().getId()));

        authUtil.getOptionalGoUser().ifPresent(user -> {
            response.setIsCreatedByCurrentUser(cap.getUser().getId().equals(user.getId()));
            response.setCanActOnEvent(cap.getActivity().getAttendingUsers().stream().anyMatch(att -> att.getUser().getId().equals(user.getId())));
            response.setRatingFromCurrentUser(goActivityRatingRepository.findByUserAndActivity(user.getId(), cap.getActivity().getId()).map(GoActivityRating::getRating).orElse(null));
            response.setCanRate(goActivityRatingRepository.findByUserAndActivity(user.getId(), cap.getActivity().getId()).isEmpty());
            response.setCanComment(goActivityCommentRepository.findByUserAndActivity(user.getId(), cap.getActivity().getId()).isEmpty());
        });

        response.setHasComments(goActivityCommentRepository.existsByActivity_Id(cap.getActivity().getId()));
        response.setHasRatings(goActivityRatingRepository.existsByActivity_Id(cap.getActivity().getId()));
        response.setTotalComments(HelperUtil.format(goActivityCommentRepository.countByActivity_Id(cap.getActivity().getId())));
        response.setTotalRatings(HelperUtil.format(goActivityRatingRepository.countByActivity_Id(cap.getActivity().getId())));
        response.setLink(urlService.generate(LinkedDynamicUrlDto.nearby(
                "bcap",
                cap.getId(),
                cap.getName(),
                cap.getDescription(),
                cap.getMedia().getFirst().getFile()
        )));

        return response;
    }

    @Override
    public ApiResponse<GoBCapResponse> get(String id) {
        GoBCap cap = goBCapRepository.findById(id).orElseThrow(() -> new SerchException("Bcap not found"));

        return new ApiResponse<>(prepare(cap));
    }

    @Override
    public ApiResponse<List<GoBCapResponse>> getAll(Integer page, Integer size, Long interest, Boolean scoped, Double lat, Double lng) {
        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                Sort.by(Sort.Direction.ASC, "createdAt")
        );

        Page<GoBCap> caps = getBCaps(pageable, interest, scoped, lat, lng);

        if(caps.hasContent()) {
            return new ApiResponse<>(caps.map(this::prepare).stream().toList());
        }

        return new ApiResponse<>(new ArrayList<>());
    }

    private Page<GoBCap> getBCaps(Pageable pageable, Long interest, Boolean scoped, Double lat, Double lng) {
        if(scoped) {
            UUID userId = authUtil.getGoUser().getId();

            if(interest != null) {
                return goBCapRepository.findByInterest(interest, userId, pageable);
            } else {
                List<Long> interests = goUserInterestRepository.findInterestIdsByUserId(userId);

                if (!interests.isEmpty()) {
                    return goBCapRepository.findByInterests(interests, userId, pageable);
                } else {
                    return goBCapRepository.findAllBCaps(userId, pageable);
                }
            }
        } else {
            GoOptional go = goUtils.getOptional(lat, lng, 5000.0);

            if(interest != null) {
                return goBCapRepository.findByInterest(go.getUser(), interest, go.getLng(), go.getLat(), go.getRad(), pageable);
            } else {
                if (!go.getInterests().isEmpty()) {
                    return goBCapRepository.findByInterests(go.getUser(), go.getInterests(), go.getLng(), go.getLat(), go.getRad(), pageable);
                } else {
                    return goBCapRepository.findAllBCaps(go.getUser(), go.getLng(), go.getLat(), go.getRad(), pageable);
                }
            }
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(String id) {
        GoBCap cap = goBCapRepository.findById(id).orElseThrow(() -> new SerchException("Bcap not found"));

        if(cap.getUser().getId().equals(authUtil.getGoUser().getId())) {
            String activity = cap.getActivity().getId();

            if(!cap.getMedia().isEmpty()) {
                cap.getMedia().forEach(image -> mediaService.delete(image.getId()));
            }

            if(!cap.getActivity().getImages().isEmpty()) {
                cap.getActivity().getImages().forEach(image -> mediaService.delete(image.getId()));
            }

            goActivityCommentRepository.deleteByActivity(activity);
            goActivityRatingRepository.deleteByActivity(activity);
            goBCapRepository.delete(cap);
            return new ApiResponse<>("BCap deleted successfully", HttpStatus.OK);
        } else {
            throw new SerchException("You cannot delete a bcap you did not create");
        }
    }
}
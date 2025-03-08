package com.serch.server.domains.nearby.services.activity.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.bases.BaseLocation;
import com.serch.server.domains.linked.dtos.LinkedDynamicUrlDto;
import com.serch.server.domains.linked.services.LinkedDynamicUrlService;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.*;
import com.serch.server.domains.nearby.models.go.activity.GoActivity;
import com.serch.server.domains.nearby.models.go.activity.GoAttendingUser;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.models.go.user.GoUserInterest;
import com.serch.server.domains.nearby.repositories.go.*;
import com.serch.server.domains.nearby.services.account.services.GoLocationService;
import com.serch.server.domains.nearby.services.activity.requests.GoCreateActivityRequest;
import com.serch.server.domains.nearby.services.activity.responses.GoActivityPollResponse;
import com.serch.server.domains.nearby.services.activity.responses.GoActivityResponse;
import com.serch.server.domains.nearby.services.activity.services.GoActivityLifecycleService;
import com.serch.server.domains.nearby.services.activity.services.GoActivityService;
import com.serch.server.domains.nearby.services.interest.services.GoInterestService;
import com.serch.server.domains.nearby.services.media.services.GoMediaService;
import com.serch.server.domains.nearby.utils.GoOptional;
import com.serch.server.domains.nearby.utils.GoUtils;
import com.serch.server.domains.nearby.utils.GoValidator;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GoActivityImplementation implements GoActivityService {
    private final GoActivityRepository goActivityRepository;
    private final GoAttendingUserRepository goAttendingUserRepository;
    private final AuthUtil authUtil;
    private final LinkedDynamicUrlService urlService;
    private final GoMediaService mediaService;
    private final GoInterestService interestService;
    private final GoLocationService locationService;
    private final GoUserInterestRepository goUserInterestRepository;
    private final GoUtils goUtils;
    private final GoActivityLifecycleService lifecycle;
    private final GoActivityRatingRepository goActivityRatingRepository;
    private final GoBCapRepository goBCapRepository;
    private final GoActivityCommentRepository goActivityCommentRepository;

    @Override
    public ApiResponse<GoActivityResponse> create(GoCreateActivityRequest request) {
        GoUser user = authUtil.getGoUser();
        GoUserInterest interest = interestService.put(request.getInterest(), user);

        GoActivity activity = GoMapper.instance.activity(request);
        activity.setUser(user);
        activity.setName("%s with %s".formatted(interest.getInterest().getName(), user.getFullName()));
        activity.setInterest(interest.getInterest());

        GoValidator validator = GoValidator.chain(activity, request);
        if(validator.getCanContinue()) {
            activity = goActivityRepository.save(activity);

            locationService.put(activity, request.getLocation());
            if(request.getImages() != null && !request.getImages().isEmpty()) {
                activity.setImages(mediaService.upload(request, activity));
            }

            lifecycle.onCreated(activity);

            return new ApiResponse<>(toResponse(
                    activity,
                    user.getLocation().getLatitude(),
                    user.getLocation().getLongitude(),
                    user.getSearchRadius()
            ));
        } else {
            throw new SerchException(validator.getError());
        }
    }

    private GoActivityResponse toResponse(GoActivity activity, double lat, double lng, double radius) {
        GoActivityResponse response = GoMapper.instance.activity(activity);

        authUtil.getOptionalGoUser().ifPresentOrElse(
                user -> {
                    boolean isCreatedByUser = activity.getUser().getId().equals(user.getId());

                    response.setIsCreatedByCurrentUser(isCreatedByUser);
                    response.setStartTime(TimeUtil.formatTime(activity.getDate(), activity.getStartTime(), user.getTimezone()));
                    response.setEndTime(TimeUtil.formatTime(activity.getDate(), activity.getEndTime(), user.getTimezone()));
                    response.setTimestamp(TimeUtil.formatDate(user.getTimezone(), activity.getDate()));
                    response.setCanRate(goActivityRatingRepository.findByUserAndActivity(user.getId(), activity.getId()).isEmpty());
                    response.setCanComment(goActivityCommentRepository.findByUserAndActivity(user.getId(), activity.getId()).isEmpty());
                    response.setHasComments(goActivityCommentRepository.existsByActivity_Id(activity.getId()));
                    response.setHasRatings(goActivityRatingRepository.existsByActivity_Id(activity.getId()));

                    if(isCreatedByUser) {
                        response.setHasResponded(true);
                    } else {
                        response.setHasResponded(activity.getAttendingUsers().stream().anyMatch(att -> att.getUser().getId().equals(user.getId())));
                    }

                    response.setPoll(preparePollInformation(activity, user.getLocation().getBase(), user.getSearchRadius()));
                },
                () -> {
                    response.setIsCreatedByCurrentUser(false);
                    response.setStartTime(TimeUtil.formatTime(activity.getDate(), activity.getStartTime(), ""));
                    response.setEndTime(TimeUtil.formatTime(activity.getDate(), activity.getEndTime(), ""));
                    response.setTimestamp(TimeUtil.formatDate("", activity.getDate()));
                    response.setHasResponded(false);
                    response.setCanComment(false);
                    response.setCanRate(false);

                    response.setPoll(preparePollInformation(activity, new BaseLocation(lat, lng), 5000.0));
                }
        );

        response.setInterest(interestService.prepare(activity.getInterest(), lng, lat, radius));
        response.setBcap(goBCapRepository.findByActivity_Id(activity.getId()).map(GoBCap::getId).orElse(null));
        response.setTotalComments(HelperUtil.format(goActivityCommentRepository.countByActivity_Id(activity.getId())));
        response.setTotalRatings(HelperUtil.format(goActivityRatingRepository.countByActivity_Id(activity.getId())));
        response.setHasSimilarActivitiesFromCreator(goActivityRepository.existsByCreatorWithSameInterest(activity.getUser().getId(), activity.getInterest().getId(), activity.getId()));
        response.setHasSimilarActivitiesFromOtherCreators(goActivityRepository.existsByOtherCreatorsWithSameInterest(activity.getUser().getId(), activity.getInterest().getId()));

        if(activity.isEnded()) {
            response.setRating(goActivityRatingRepository.findAverageRatingByActivity(activity.getId()));
        }
        response.setLink(urlService.generate(LinkedDynamicUrlDto.nearby(
                "activity",
                activity.getId(),
                activity.getName(),
                activity.getDescription(),
                response.getImages().getFirst().getFile()
        )));

        return response;
    }

    private GoActivityPollResponse preparePollInformation(GoActivity activity, BaseLocation location, Double searchRadius) {
        GoActivityPollResponse poll = new GoActivityPollResponse();
        poll.setTotalAttendingUsers(goUtils.calculateTotalAttendingUsers(activity));
        poll.setTotalUsersWithSameSharedInterest(goUtils.calculateAndUpdatePopularity(activity.getInterest()));
        poll.setTotalNearbyUsersWithSameSharedInterest(goUtils.calculateNearbyPopularity(activity.getInterest(), location, searchRadius));

        return poll;
    }

    @Override
    @Transactional
    public ApiResponse<GoActivityResponse> attend(String id) {
        GoUser user = authUtil.getGoUser();
        GoActivity activity = goActivityRepository.findById(id).orElseThrow(() -> new SerchException("Activity not found"));

        // Check if the user is already attending the activity
        Optional<GoAttendingUser> existing = goAttendingUserRepository.findByActivity_IdAndUser_Id(activity.getId(), user.getId());

        if (existing.isPresent()) {
            goAttendingUserRepository.delete(existing.get());
            activity.getAttendingUsers().remove(existing.get());
        } else {
            GoAttendingUser attending = new GoAttendingUser();
            attending.setUser(user);
            attending.setActivity(activity);
            attending = goAttendingUserRepository.save(attending);

            if(activity.getAttendingUsers().isEmpty()) {
                activity.setAttendingUsers(List.of(attending));
            } else {
                activity.getAttendingUsers().add(attending);
            }
        }

        lifecycle.onAttending(activity);

        return new ApiResponse<>(toResponse(
                activity,
                user.getLocation().getLatitude(),
                user.getLocation().getLongitude(),
                user.getSearchRadius()
        ));
    }

    @Override
    public ApiResponse<List<GoActivityResponse>> getAll(Integer page, Integer size, Long interest, LocalDate timestamp, Boolean scoped, Double lat, Double lng) {
        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                Sort.by(Sort.Direction.DESC, "createdAt", "startTime")
        );
        Double rad = authUtil.getOptionalGoUser().map(GoUser::getSearchRadius).orElse(5000.00);

        Page<GoActivity> activities = getActivities(pageable, interest, timestamp, scoped, lat, lng, rad);
        if(activities.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            return new ApiResponse<>(activities.getContent().stream().map(activity -> toResponse(activity, lat, lng, rad)).toList());
        }
    }

    private Page<GoActivity> getActivities(Pageable pageable, Long interest, LocalDate timestamp, Boolean scoped, Double lat, Double lng, Double rad) {
        if(scoped != null && scoped) {
            UUID id = authUtil.getGoUser().getId();

            if(interest != null && timestamp != null) {
                return goActivityRepository.findByTimestampAndInterest(timestamp, interest, id, pageable);
            } else if(interest != null) {
                return goActivityRepository.findByInterest(interest, id, pageable);
            } else if(timestamp != null) {
                return goActivityRepository.findByTimestamp(timestamp, id, pageable);
            } else {
                return goActivityRepository.getHistory(id, pageable);
            }
        } else {
            GoOptional go = goUtils.getOptional(lat, lng, rad);

            if(interest != null && timestamp != null) {
                return goActivityRepository.findByTimestampAndInterest(go.getUser(), timestamp, interest, go.getLng(), go.getLat(), go.getRad(), pageable);
            } else if(interest != null) {
                return goActivityRepository.findByInterest(go.getUser(), interest, go.getLng(), go.getLat(), go.getRad(), pageable);
            } else if(timestamp != null) {
                return goActivityRepository.findByTimestamp(go.getUser(), timestamp, go.getLng(), go.getLat(), go.getRad(), pageable);
            } else {
                if (!go.getInterests().isEmpty()) {
                    return goActivityRepository.findByInterests(go.getUser(), go.getInterests(), go.getLng(), go.getLat(), go.getRad(), pageable);
                } else {
                    return goActivityRepository.getHistory(go.getUser(), go.getLng(), go.getLat(), go.getRad(), pageable);
                }
            }
        }
    }

    @Override
    public ApiResponse<List<GoActivityResponse>> getAllOngoing(Integer page, Integer size, Boolean scoped, Double lat, Double lng) {
         Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                Sort.by(Sort.Direction.DESC, "createdAt", "startTime")
        );
        Double rad = authUtil.getOptionalGoUser().map(GoUser::getSearchRadius).orElse(5000.00);

        Page<GoActivity> activities = getOngoingActivities(pageable, scoped, lat, lng, rad);
        if(activities.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            return new ApiResponse<>(activities.getContent().stream().map(activity -> toResponse(activity, lat, lng, rad)).toList());
        }
    }

    private Page<GoActivity> getOngoingActivities(Pageable pageable, Boolean scoped, Double lat, Double lng, Double rad) {
        if(scoped != null && scoped) {
            UUID id = authUtil.getGoUser().getId();
            List<Long> interests = goUserInterestRepository.findInterestIdsByUserId(id);

            if (!interests.isEmpty()) {
                return goActivityRepository.findOngoingByInterests(interests, id, pageable);
            } else {
                return goActivityRepository.findOngoing(id, pageable);
            }
        } else {
            GoOptional go = goUtils.getOptional(lat, lng, rad);

            if (!go.getInterests().isEmpty()) {
                return goActivityRepository.findOngoingByInterests(go.getUser(), go.getInterests(), go.getLng(), go.getLat(), go.getRad(), pageable);
            } else {
                return goActivityRepository.findOngoing(go.getUser(), go.getLng(), go.getLat(), go.getRad(), pageable);
            }
        }
    }

    @Override
    public ApiResponse<List<GoActivityResponse>> getAllUpcoming(Integer page, Integer size, Boolean scoped, Double lat, Double lng) {
         Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                Sort.by(Sort.Direction.DESC, "createdAt", "startTime")
        );
        Double rad = authUtil.getOptionalGoUser().map(GoUser::getSearchRadius).orElse(5000.00);

        Page<GoActivity> activities = getUpcomingActivities(pageable, scoped, lat, lng, rad);
        if(activities.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            return new ApiResponse<>(activities.getContent().stream().map(activity -> toResponse(activity, lat, lng, rad)).toList());
        }
    }

    private Page<GoActivity> getUpcomingActivities(Pageable pageable, Boolean scoped, Double lat, Double lng, Double rad) {
        if(scoped != null && scoped) {
            UUID id = authUtil.getGoUser().getId();
            List<Long> interests = goUserInterestRepository.findInterestIdsByUserId(id);

            if (!interests.isEmpty()) {
                return goActivityRepository.findUpcomingByInterests(interests, id, pageable);
            } else {
                return goActivityRepository.findUpcoming(id, pageable);
            }
        } else {
            GoOptional go = goUtils.getOptional(lat, lng, rad);

            if (!go.getInterests().isEmpty()) {
                return goActivityRepository.findUpcomingByInterests(go.getUser(), go.getInterests(), go.getLng(), go.getLat(), go.getRad(), pageable);
            } else {
                return goActivityRepository.findUpcoming(go.getUser(), go.getLng(), go.getLat(), go.getRad(), pageable);
            }
        }
    }

    @Override
    public ApiResponse<List<GoActivityResponse>> getAllAttended(Integer page, Integer size, Double lat, Double lng) {
        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        GoUser user = authUtil.getGoUser();

        Page<GoAttendingUser> list = goAttendingUserRepository.findAttended(user.getId(), pageable);
        if(list.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            return new ApiResponse<>(list.getContent().stream().map(att -> toResponse(
                    att.getActivity(), lat, lng, user.getSearchRadius()
            )).toList());
        }
    }

    @Override
    public ApiResponse<List<GoActivityResponse>> getAllAttending(Integer page, Integer size, Double lat, Double lng) {
        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        GoUser user = authUtil.getGoUser();

        Page<GoAttendingUser> list = goAttendingUserRepository.findAttending(user.getId(), pageable);
        if(list.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            return new ApiResponse<>(list.getContent().stream().map(att -> toResponse(att.getActivity(), lat, lng, user.getSearchRadius())).toList());
        }
    }

    @Override
    public ApiResponse<List<GoActivityResponse>> search(Integer page, Integer size, String q, Double lat, Double lng, Boolean scoped) {
        GoUser user = authUtil.getGoUser();
        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                Sort.by(Sort.Direction.DESC, "createdAt", "startTime")
        );

        Page<GoActivity> activities = scoped
                ? goActivityRepository.search(q, lat, lng, user.getSearchRadius(), pageable, user.getInterests().stream().map(in -> in.getInterest().getId()).toList())
                : goActivityRepository.search(q, lat, lng, user.getSearchRadius(), pageable);
        if(activities.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            return new ApiResponse<>(activities.getContent().stream().map(activity -> toResponse(activity, lat, lng, user.getSearchRadius())).toList());
        }
    }

    @Override
    public ApiResponse<List<GoActivityResponse>> getSimilar(Integer page, Integer size, String id, Double lat, Double lng) {
        GoActivity activity = goActivityRepository.findById(id).orElseThrow(() -> new SerchException("Activity not found"));

        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                Sort.by(Sort.Direction.DESC, "createdAt", "startTime")
        );

        Page<GoActivity> activities = goActivityRepository.findSimilar(activity.getUser().getId(), activity.getInterest().getId(), activity.getId(), pageable);

        if(activities.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            return new ApiResponse<>(activities.getContent().stream().map(ev -> toResponse(
                    ev,
                    lat,
                    lng,
                    authUtil.getOptionalGoUser().map(GoUser::getSearchRadius).orElse(5000.00)
            )).toList());
        }
    }

    @Override
    public ApiResponse<List<GoActivityResponse>> getRelated(Integer page, Integer size, String id, Double lat, Double lng) {
        GoActivity activity = goActivityRepository.findById(id).orElseThrow(() -> new SerchException("Activity not found"));

        Pageable pageable = PageRequest.of(
                page == null ? 0 : page,
                size == null ? 20 : size,
                Sort.by(Sort.Direction.DESC, "createdAt", "startTime")
        );

        Page<GoActivity> activities = goActivityRepository.findRelated(activity.getUser().getId(), activity.getInterest().getId(), activity.getId(), pageable);

        if(activities.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            return new ApiResponse<>(activities.getContent().stream().map(ev -> toResponse(
                    ev,
                    lat,
                    lng,
                    authUtil.getOptionalGoUser().map(GoUser::getSearchRadius).orElse(5000.00)
            )).toList());
        }
    }

    @Override
    public ApiResponse<GoActivityResponse> get(String id, Double lat, Double lng) {
        GoActivity activity = goActivityRepository.findById(id).orElseThrow(() -> new SerchException("Activity not found"));
        Double rad = authUtil.getOptionalGoUser().map(GoUser::getSearchRadius).orElse(5000.00);

        return new ApiResponse<>(toResponse(activity, lat, lng, rad));
    }

    @Override
    public ApiResponse<GoActivityResponse> end(String id) {
        GoUser user = authUtil.getGoUser();
        GoActivity activity = goActivityRepository.findById(id).orElseThrow(() -> new SerchException("Activity not found"));

        if(activity.getUser().getId().equals(user.getId())) {
            ZonedDateTime now = TimeUtil.now(user.getTimezone());
            LocalTime time = now.toLocalTime();

            if(activity.getEndTime().getHour() != time.getHour() || activity.getEndTime().getMinute() != time.getMinute()) {
                activity.setEndTime(time);
            }

            activity.setStatus(TripStatus.CLOSED);
            activity.setUpdatedAt(TimeUtil.now());
            activity = goActivityRepository.save(activity);

            lifecycle.onEnded(activity);

            return new ApiResponse<>(toResponse(
                    activity,
                    user.getLocation().getLatitude(),
                    user.getLocation().getLongitude(),
                    user.getSearchRadius()
            ));
        }

        throw new SerchException("You are not the creator of this activity.");
    }

    @Override
    public ApiResponse<GoActivityResponse> start(String id) {
        GoUser user = authUtil.getGoUser();
        GoActivity activity = goActivityRepository.findById(id).orElseThrow(() -> new SerchException("Activity not found"));

        if(activity.getUser().getId().equals(user.getId())) {
            ZonedDateTime now = TimeUtil.now(user.getTimezone());
            LocalTime time = now.toLocalTime();

            if(activity.getStartTime().getHour() != time.getHour() || activity.getStartTime().getMinute() != time.getMinute()) {
                activity.setStartTime(time);
            }

            activity.setStatus(TripStatus.ACTIVE);
            activity.setUpdatedAt(TimeUtil.now());
            activity = goActivityRepository.save(activity);

            lifecycle.onStarted(activity);

            return new ApiResponse<>(toResponse(
                    activity,
                    user.getLocation().getLatitude(),
                    user.getLocation().getLongitude(),
                    user.getSearchRadius()
            ));
        }

        throw new SerchException("You are not the creator of this activity.");
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(String id) {
        GoActivity activity = goActivityRepository.findById(id).orElseThrow(() -> new SerchException("Activity not found"));

        if(activity.getUser().getId().equals(authUtil.getGoUser().getId())) {
            if(!activity.getImages().isEmpty()) {
                activity.getImages().forEach(image -> mediaService.delete(image.getId()));
            }

            if(activity.isEnded()) {
                goBCapRepository.findByActivity_Id(activity.getId()).ifPresent(bcap -> {
                    if(!bcap.getMedia().isEmpty()) {
                        bcap.getMedia().forEach(image -> mediaService.delete(image.getId()));
                    }

                    goBCapRepository.delete(bcap);
                });
            }

            lifecycle.onDeleted(activity);
            goActivityCommentRepository.deleteByActivity(activity.getId());
            goActivityRatingRepository.deleteByActivity(activity.getId());
            goActivityRepository.delete(activity);
            return new ApiResponse<>("Activity deleted successfully", HttpStatus.OK);
        } else {
            throw new SerchException("You cannot delete an activity you did not create");
        }
    }
}
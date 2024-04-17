package com.serch.server.services.rating.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.auth.Role;
import com.serch.server.exceptions.others.RatingException;
import com.serch.server.mappers.RatingMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.rating.AppRating;
import com.serch.server.models.rating.Rating;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.trip.Trip;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.call.CallRepository;
import com.serch.server.repositories.rating.AppRatingRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.rating.requests.RateAppRequest;
import com.serch.server.services.rating.requests.RateRequest;
import com.serch.server.services.rating.responses.RatingChartResponse;
import com.serch.server.services.rating.responses.RatingResponse;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing ratings, including rating providers,
 * sharing ratings, rating the application, viewing ratings, and retrieving
 * statistical data about ratings.
 * <p></p>
 * It implements the {@link RatingService} interface
 * <p></p>
 * @see UserUtil
 * @see AppRatingRepository
 * @see UserRepository
 * @see GuestRepository
 * @see CallRepository
 * @see ScheduleRepository
 * @see TripRepository
 * @see RatingRepository
 * @see ProfileRepository
 */
@Service
@RequiredArgsConstructor
public class RatingImplementation implements RatingService {
    private final UserUtil userUtil;
    private final AppRatingRepository appRatingRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final CallRepository callRepository;
    private final ScheduleRepository scheduleRepository;
    private final TripRepository tripRepository;
    private final RatingRepository ratingRepository;
    private final ProfileRepository profileRepository;

    @Override
    public ApiResponse<String> rate(RateRequest request) {
        Rating rating = new Rating();

        String rater;
        String rated;
        if(request.getGuest() == null || request.getGuest().isEmpty()) {
            rater = String.valueOf(userUtil.getUser().getId());
        } else {
            rater = guestRepository.findById(request.getGuest())
                    .orElseThrow(() -> new RatingException("Guest not found"))
                    .getId();
        }

        if(request.getIsProvider()) {
            Call call = callRepository.findById(request.getId())
                    .orElseThrow(() -> new RatingException("Call not found"));
            rating.setT2f(call.getChannel());
            rated = String.valueOf(call.getCalled().getId());
        } else {
            rated = callRepository.findById(request.getId())
                    .map(Call::getChannel)
                    .orElse(
                            scheduleRepository.findById(request.getId())
                                    .map(Schedule::getId)
                                    .orElse(
                                            tripRepository.findById(request.getId())
                                                    .map(trip -> rated(trip, request))
                                                    .orElseThrow(() -> new RatingException("Event not found"))
                                    )
                    );
        }

        rating.setRater(rater);
        rating.setRated(rated);
        rating.setRating(request.getRating());
        rating.setComment(request.getComment());
        ratingRepository.save(rating);
        return new ApiResponse<>("Rating saved", HttpStatus.CREATED);
    }

    @Override
    public ApiResponse<String> share(RateRequest request) {
        Trip trip = tripRepository.findById(request.getId())
                .orElseThrow(() -> new RatingException("Event not found"));

        Rating first = new Rating();
        Rating second = new Rating();

        String rater;
        if(request.getGuest() == null || request.getGuest().isEmpty()) {
            rater = String.valueOf(userUtil.getUser().getId());
        } else {
            rater = guestRepository.findById(request.getGuest())
                    .orElseThrow(() -> new RatingException("Guest not found"))
                    .getId();
        }

        first.setRater(rater);
        first.setRated(rated(trip, request));
        first.setRating(request.getRating() / 2.0);
        first.setComment(request.getComment());
        ratingRepository.save(first);

        second.setRater(rater);
        second.setRated(rated(trip, request));
        second.setRating(request.getRating() / 2.0);
        second.setComment(request.getComment());
        ratingRepository.save(second);

        return new ApiResponse<>("Rating saved", HttpStatus.CREATED);
    }

    private String rated(Trip trip, RateRequest request) {
        if(request.getGuest() == null || request.getGuest().isEmpty()) {
            try {
                if(userUtil.getUser().isUser(UUID.fromString(trip.getAccount()))) {
                    if(trip.getInvitedProvider() != null && request.getIsInvited()) {
                        return String.valueOf(trip.getInvitedProvider().getId());
                    } else {
                        return String.valueOf(trip.getProvider().getId());
                    }
                } else if(trip.getProvider().isSameAs(userUtil.getUser().getId())) {
                    if(trip.getInvitedProvider() != null && request.getIsInvited()) {
                        return String.valueOf(trip.getInvitedProvider().getId());
                    } else {
                        return trip.getAccount();
                    }
                } else {
                    if(trip.getInvitedProvider() != null && request.getIsInvited()) {
                        return String.valueOf(trip.getProvider().getId());
                    } else {
                        return trip.getAccount();
                    }
                }
            } catch (Exception e) {
                throw new RatingException("Couldn't find the user being rated");
            }
        } else {
            if(trip.getInvitedProvider() != null && request.getIsInvited()) {
                return String.valueOf(trip.getInvitedProvider().getId());
            } else {
                return String.valueOf(trip.getProvider().getId());
            }
        }
    }

    @Override
    public ApiResponse<Double> rate(RateAppRequest request) {
        Optional<AppRating> existing = appRatingRepository.findByAccount(request.getAccount());
        if(existing.isPresent()) {
            existing.get().setRating(request.getRating());
            existing.get().setUpdatedAt(LocalDateTime.now());
            if(request.getComment() != null && !request.getComment().isEmpty()) {
                existing.get().setComment(request.getComment());
            }
            appRatingRepository.save(existing.get());
        } else {
            String app;
            try {
                Role role = userRepository.findById(UUID.fromString(request.getAccount()))
                        .orElse(new User())
                        .getRole();

                if(role == Role.ASSOCIATE_PROVIDER) {
                    app = "Serch Provider";
                } else if(role == Role.USER) {
                    app = "Serch User";
                } else {
                    app = "Serch Business";
                }
            } catch (Exception e) {
                app = "Serch User - Guest";
            }

            AppRating rating = RatingMapper.INSTANCE.rating(request);
            rating.setApp(app);
            appRatingRepository.save(rating);
        }
        return new ApiResponse<>(request.getRating());
    }

    @Override
    public ApiResponse<List<RatingResponse>> view() {
        List<RatingResponse> list = ratingRepository.findByRated(String.valueOf(userUtil.getUser().getId()))
                .stream()
                .sorted(Comparator.comparing(Rating::getCreatedAt))
                .map(this::getRatingResponse)
                .toList();
        return new ApiResponse<>(list);
    }

    private RatingResponse getRatingResponse(Rating rating) {
        RatingResponse response = RatingMapper.INSTANCE.response(rating);
        response.setName(getName(rating));
        response.setCategory(getCategory(rating));
        return response;
    }

    private String getName(Rating rating) {
        try {
            return guestRepository.findById(rating.getRater())
                    .map(Guest::getFirstName)
                    .orElse(
                            userRepository.findById(UUID.fromString(rating.getRater()))
                                    .map(User::getFirstName)
                                    .orElse("")
                    );
        } catch (Exception e) {
                return "";
        }
    }

    private String getCategory(Rating rating) {
        Optional<Guest> guest = guestRepository.findById(rating.getRater());
        if(guest.isPresent()) {
            return "Guest";
        } else {
            try {
                return profileRepository.findById(UUID.fromString(rating.getRater()))
                        .map(Profile::getCategory)
                        .orElse(SerchCategory.USER)
                        .getType();
            } catch (Exception e) {
                return "";
            }
        }
    }

    @Override
    public ApiResponse<List<RatingResponse>> good() {
        List<RatingResponse> list = ratingRepository.findGood(String.valueOf(userUtil.getUser().getId()))
                .stream()
                .sorted(Comparator.comparing(Rating::getCreatedAt))
                .map(this::getRatingResponse)
                .toList();
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<List<RatingResponse>> bad() {
        List<RatingResponse> list = ratingRepository.findBad(String.valueOf(userUtil.getUser().getId()))
                .stream()
                .sorted(Comparator.comparing(Rating::getCreatedAt))
                .map(this::getRatingResponse)
                .toList();
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<List<RatingChartResponse>> chart() {
        List<RatingChartResponse> list = ratingRepository.chart(String.valueOf(userUtil.getUser().getId()));
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<Double> app(String account) {
        if(account != null && !account.isEmpty()) {
            return new ApiResponse<>(
                    "App rating fetched",
                    appRatingRepository.findByAccount(account)
                            .orElse(new AppRating())
                            .getRating(),
                    HttpStatus.OK
            );
        } else {
            return new ApiResponse<>(
                    "App rating fetched",
                    appRatingRepository.findByAccount(String.valueOf(userUtil.getUser().getId()))
                            .orElse(new AppRating())
                            .getRating(),
                    HttpStatus.OK
            );
        }
    }
}

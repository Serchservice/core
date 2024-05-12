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
import com.serch.server.repositories.conversation.CallRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
        String rated;

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

        rating.setRater(getAccount(request.getGuest()));
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
        String rater = getAccount(request.getGuest());

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
    public ApiResponse<RatingResponse> rate(RateAppRequest request) {
        String account = getAccount(request.getAccount());
        if(request.getRating() != null) {
            Optional<AppRating> existing = appRatingRepository.findByAccount(account);
            if(existing.isPresent()) {
                existing.get().setRating(request.getRating());
                existing.get().setUpdatedAt(LocalDateTime.now());
                if(request.getComment() != null && !request.getComment().isEmpty()) {
                    existing.get().setComment(request.getComment());
                }
                appRatingRepository.save(existing.get());
            } else {
                AppRating rating = RatingMapper.INSTANCE.rating(request);
                rating.setApp(getApp(account));
                rating.setComment(request.getComment());
                rating.setAccount(account);
                appRatingRepository.save(rating);
            }

            RatingResponse response = new RatingResponse();
            response.setRating(request.getRating());
            response.setComment(request.getComment());
            return new ApiResponse<>(response);
        } else {
            throw new RatingException("There is no rating value");
        }
    }

    private String getAccount(String request) {
        if(request == null || request.isEmpty()) {
            return String.valueOf(userUtil.getUser().getId());
        } else {
            return guestRepository.findById(request)
                    .orElseThrow(() -> new RatingException("Guest not found"))
                    .getId();
        }
    }

    private String getApp(String account) {
        String app;
        try {
            Role role = userRepository.findById(UUID.fromString(account))
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
        return app;
    }

    @Override
    public ApiResponse<List<RatingResponse>> view() {
        List<Rating> ratings = ratingRepository.findByRated(String.valueOf(userUtil.getUser().getId()));
        return ratings(ratings);
    }

    private RatingResponse getRatingResponse(Rating rating) {
        RatingResponse response = RatingMapper.INSTANCE.response(rating);
        response.setName(getName(rating));
        response.setCategory(getCategory(rating).getType());
        response.setImage(getCategory(rating).getImage());
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

    private SerchCategory getCategory(Rating rating) {
        try {
            return profileRepository.findById(UUID.fromString(rating.getRater()))
                    .map(Profile::getCategory)
                    .orElse(SerchCategory.USER);
        } catch (Exception e) {
            return SerchCategory.GUEST;
        }
    }

    @Override
    public ApiResponse<List<RatingResponse>> good(String id) {
        String user = id == null ? String.valueOf(userUtil.getUser().getId()) : id;
        List<Rating> ratings = ratingRepository.findGood(user);
        return ratings(ratings);
    }

    @Override
    public ApiResponse<List<RatingResponse>> ratings(List<Rating> ratings) {
        List<RatingResponse> list = new ArrayList<>();

        if(!ratings.isEmpty()) {
            list = ratings.stream()
                    .sorted(Comparator.comparing(Rating::getCreatedAt))
                    .map(this::getRatingResponse)
                    .toList();
        }
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<List<RatingResponse>> bad(String id) {
        String user = id == null ? String.valueOf(userUtil.getUser().getId()) : id;
        List<Rating> ratings = ratingRepository.findBad(user);
        return ratings(ratings);
    }

    @Override
    public ApiResponse<List<RatingChartResponse>> chart(String id) {
        String user = id == null ? String.valueOf(userUtil.getUser().getId()) : id;
        List<Object[]> resultList = ratingRepository.chart(user);

        Map<String, RatingChartResponse> responseMap = new LinkedHashMap<>();

        // Populate the map with default values for current month, last month, and the month before that
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i >= -2; i--) {
            LocalDate date = currentDate.plusMonths(i);
            String monthName = date.format(DateTimeFormatter.ofPattern("MMM"));
            responseMap.put(monthName, new RatingChartResponse(monthName, 0.0, 0.0, 0.0, 0.0));
        }

        // Update the map with actual data
        for (Object[] result : resultList) {
            String month = (String) result[0];
            Double bad = (Double) result[1];
            Double good = (Double) result[2];
            Double average = (Double) result[3];
            Double total = (Double) result[4];

            // Apply threshold constraints
            bad = Math.min(Math.max(bad, 0.0), 5.0);
            good = Math.min(Math.max(good, 0.0), 5.0);
            average = Math.min(Math.max(average, 0.0), 5.0);
            total = Math.min(Math.max(total, 0.0), 5.0);

            responseMap.put(month, new RatingChartResponse(month, bad, good, average, total));
        }

        // Convert the map values to a list
        List<RatingChartResponse> chartResponses = new ArrayList<>(responseMap.values());
        return new ApiResponse<>(chartResponses);
    }

    @Override
    public ApiResponse<RatingResponse> app(String account) {
        AppRating rating = appRatingRepository.findByAccount(getAccount(account))
                .orElse(new AppRating());
        RatingResponse response = new RatingResponse();
        response.setRating(rating.getRating());
        response.setComment(rating.getComment());
        return new ApiResponse<>("App rating fetched", response, HttpStatus.OK);
    }
}
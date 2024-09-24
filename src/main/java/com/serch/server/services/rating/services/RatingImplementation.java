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
import com.serch.server.models.shared.Guest;
import com.serch.server.models.trip.Trip;
import com.serch.server.repositories.account.BusinessProfileRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.repositories.rating.AppRatingRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.rating.requests.RateAppRequest;
import com.serch.server.services.rating.requests.RateRequest;
import com.serch.server.services.rating.requests.RatingCalculation;
import com.serch.server.services.rating.responses.RatingChartResponse;
import com.serch.server.services.rating.responses.RatingResponse;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.serch.server.enums.auth.Role.USER;
import static com.serch.server.enums.call.CallType.T2F;

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
 * @see TripRepository
 * @see RatingRepository
 * @see ProfileRepository
 */
@Service
@RequiredArgsConstructor
public class RatingImplementation implements RatingService {
    private final RatingCalculationService calculationService;
    private final UserUtil userUtil;
    private final AppRatingRepository appRatingRepository;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final CallRepository callRepository;
    private final TripRepository tripRepository;
    private final RatingRepository ratingRepository;
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Override
    public ApiResponse<String> rate(RateRequest request) {
        callRepository.findById(request.getId())
                .ifPresentOrElse(call -> rateCall(call, request), () -> tripRepository.findById(request.getId())
                        .ifPresent(trip -> rateTrip(request, trip)));
        return new ApiResponse<>("Rating saved", HttpStatus.CREATED);
    }

    private void rateCall(Call call, RateRequest request) {
        if(call.getType() == T2F) {
            if(request.getRating() != null) {
                createRating(request, call.getChannel(), request.getRating(), call.getChannel(), false);
            }
            if(request.getRating() != null && request.getInvited() != null) {
                Profile rated = userUtil.getUser().isUser(call.getCalled().getId()) ? call.getCaller() : call.getCalled();

                createRating(request, rated.getId().toString(), request.getInvited(), call.getChannel(), true);
                updateUserRating(rated);
            } else if(request.getInvited() != null) {
                Profile rated = userUtil.getUser().isUser(call.getCalled().getId()) ? call.getCaller() : call.getCalled();

                createRating(request, rated.getId().toString(), request.getInvited(), call.getChannel(), false);
                updateUserRating(rated);
            }
        } else {
            if(request.getRating() != null) {
                createRating(request, call.getChannel(), request.getRating(), call.getChannel(), false);
            }
        }
    }

    private void createRating(RateRequest request, String rated, double value, String event, boolean skip) {
        Rating rating = new Rating();
        rating.setRater(getAccount(request.getGuest()));
        rating.setRated(rated);
        rating.setEvent(event);
        rating.setRating(value);

        if(!skip) {
            rating.setComment(request.getComment());
        }
        ratingRepository.save(rating);
    }

    private void rateTrip(RateRequest request, Trip trip) {
        if((request.getGuest() != null && !request.getGuest().isEmpty()) || userUtil.getUser().getRole() == USER) {
            rateTripAsUserOrGuest(request, trip);
        } else if(trip.getInvited() != null && trip.getInvited().getProvider() != null) {
            rateTripAsInvited(request, trip);
        } else {
            rateTripAsProvider(request, trip);
        }
    }

    private void rateTripAsUserOrGuest(RateRequest request, Trip trip) {
        if(request.getRating() != null && request.getIsBoth() && trip.getInvited() != null && trip.getInvited().getProvider() != null) {
            double value = request.getRating() / 2.0;

            createRating(request, trip.getProvider().getId().toString(), value, trip.getId(), true);
            updateUserRating(trip.getProvider());

            createRating(request, trip.getInvited().getProvider().getId().toString(), value, trip.getId(), true);
            updateUserRating(trip.getInvited().getProvider());
        } else {
            if (request.getRating() != null) {
                createRating(request, trip.getProvider().getId().toString(), request.getRating(), trip.getId(), false);
                updateUserRating(trip.getProvider());
            }

            if (request.getInvited() != null && trip.getInvited() != null && trip.getInvited().getProvider() != null) {
                createRating(
                        request, trip.getInvited().getProvider().getId().toString(),
                        request.getInvited(), trip.getInvited().getId().toString(),
                        false
                );
                updateUserRating(trip.getInvited().getProvider());
            }
        }
    }

    private void rateTripAsProvider(RateRequest request, Trip trip) {
        if(request.getRating() != null && request.getIsBoth() && trip.getInvited() != null && trip.getInvited().getProvider() != null) {
            double value = request.getRating() / 2.0;

            rateUserOrGuestInTrip(request, trip, value, true);
            createRating(request, trip.getInvited().getProvider().getId().toString(), value, trip.getId(), true);
            updateUserRating(trip.getInvited().getProvider());
        } else {
            if (request.getRating() != null) {
                rateUserOrGuestInTrip(request, trip, request.getRating(), false);
            }

            if (request.getInvited() != null && trip.getInvited() != null && trip.getInvited().getProvider() != null) {
                createRating(
                        request, trip.getInvited().getProvider().getId().toString(),
                        request.getInvited(), trip.getInvited().getId().toString(),
                        false
                );
                updateUserRating(trip.getInvited().getProvider());
            }
        }
    }

    private void rateUserOrGuestInTrip(RateRequest request, Trip trip, double value, boolean skip) {
        try {
            profileRepository.findById(UUID.fromString(trip.getAccount()))
                    .ifPresent(profile -> {
                        createRating(request, profile.getId().toString(), value, trip.getId(), skip);
                        updateUserRating(profile);
                    });
        } catch (Exception e) {
            guestRepository.findById(trip.getAccount())
                    .ifPresent(guest -> {
                        createRating(request, guest.getId(), value, trip.getId(), skip);
                        updateGuestRating(guest);
                    });
        }
    }

    private void rateTripAsInvited(RateRequest request, Trip trip) {
        if(request.getRating() != null && request.getIsBoth()) {
            double value = request.getRating() / 2.0;

            rateUserOrGuestInTrip(request, trip, value, true);
            createRating(request, trip.getProvider().getId().toString(), value, trip.getId(), true);
            updateUserRating(trip.getProvider());
        } else {
            if (request.getRating() != null) {
                rateUserOrGuestInTrip(request, trip, request.getRating(), false);
            }

            if (request.getInvited() != null) {
                createRating(request, trip.getProvider().getId().toString(), request.getInvited(), trip.getId(), true);
                updateUserRating(trip.getProvider());
            }
        }
    }

    private String getAccount(String account) {
        if (account == null || account.isEmpty()) {
            return String.valueOf(userUtil.getUser().getId());
        } else {
            return guestRepository.findById(account).orElseThrow(() -> new RatingException("Guest not found")).getId();
        }
    }

    private void updateUserRating(Profile profile) {
        if(profile.isAssociate()) {
            List<RatingCalculation> ratings = new ArrayList<>();
            profile.getBusiness().getAssociates().forEach(associate ->
                    ratings.addAll(ratingRepository.findByRated(associate.getId().toString())
                            .stream().map(RatingMapper.INSTANCE::calculation).toList())
            );
            profile.getBusiness().setRating(calculationService.getUpdatedRating(ratings, profile.getBusiness().getRating()));
            businessProfileRepository.save(profile.getBusiness());
        }

        List<RatingCalculation> ratings = ratingRepository.findByRated(profile.getId().toString())
                .stream().map(RatingMapper.INSTANCE::calculation).toList();
        profile.setRating(calculationService.getUpdatedRating(ratings, profile.getRating()));
        profileRepository.save(profile);
    }

    private void updateGuestRating(Guest profile) {
        List<RatingCalculation> ratings = ratingRepository.findByRated(profile.getId())
                .stream().map(RatingMapper.INSTANCE::calculation).toList();

        profile.setRating(calculationService.getUpdatedRating(ratings, profile.getRating()));
        guestRepository.save(profile);
    }

    @Override
    public ApiResponse<RatingResponse> rate(RateAppRequest request) {
        String account = getAccount(request.getAccount());
        if(request.getRating() != null) {
            Optional<AppRating> existing = appRatingRepository.findByAccount(account);
            if(existing.isPresent()) {
                existing.get().setRating(request.getRating());
                existing.get().setUpdatedAt(TimeUtil.now());
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

    private String getApp(String account) {
        String app;
        try {
            Role role = userRepository.findById(UUID.fromString(account)).orElse(new User()).getRole();
            if(role == Role.ASSOCIATE_PROVIDER) {
                app = "Serch Provider";
            } else if(role == USER) {
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

    private RatingResponse getRatingResponse(Rating rating) {
        RatingResponse response = RatingMapper.INSTANCE.response(rating);
        response.setName("");
        response.setCategory(getCategory(rating).getType());
        response.setImage(getCategory(rating).getImage());
        return response;
    }

    private SerchCategory getCategory(Rating rating) {
        try {
            return profileRepository.findById(UUID.fromString(rating.getRater())).map(Profile::getCategory).orElse(SerchCategory.USER);
        } catch (Exception e) {
            return SerchCategory.GUEST;
        }
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
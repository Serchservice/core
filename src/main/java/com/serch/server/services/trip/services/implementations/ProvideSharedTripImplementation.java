package com.serch.server.services.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.chat.MessageStatus;
import com.serch.server.exceptions.others.TripException;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.SharedStatus;
import com.serch.server.models.trip.PriceChat;
import com.serch.server.models.trip.PriceHaggle;
import com.serch.server.models.trip.Trip;
import com.serch.server.repositories.shared.SharedStatusRepository;
import com.serch.server.repositories.trip.PriceChatRepository;
import com.serch.server.repositories.trip.PriceHaggleRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.trip.requests.*;
import com.serch.server.services.trip.responses.PriceDiscussion;
import com.serch.server.services.trip.responses.PriceDiscussionResponse;
import com.serch.server.services.trip.services.ProvideSharedTripService;
import com.serch.server.services.trip.services.TripService;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;

import static com.serch.server.enums.trip.TripConnectionStatus.*;

/**
 * This class implements its wrapper class {@link ProvideSharedTripService}.
 * It contains the logic and implementations necessary for the wrapper class methods.
 * <p></p>
 * @see TripService
 * @see SharedStatusRepository
 * @see PriceHaggleRepository
 * @see PriceChatRepository
 * @see TripRepository
 */
@Service
@RequiredArgsConstructor
public class ProvideSharedTripImplementation implements ProvideSharedTripService {
    private final TripService tripService;
    private final SharedStatusRepository sharedStatusRepository;
    private final PriceHaggleRepository priceHaggleRepository;
    private final PriceChatRepository priceChatRepository;
    private final TripRepository tripRepository;

    @Value("${application.web.socket.topic.trip}")
    private String TRIP_TOPIC;

    @Value("${application.payment.trip.share.user}")
    private Integer TRIP_USER_SHARE;

    @Value("${application.payment.trip.share.serch}")
    private Integer TRIP_SERCH_SHARE;

    @Override
    public ApiResponse<PriceDiscussionResponse> options(String guestId, String linkId) {
        SharedStatus sharedStatus = sharedStatusRepository.findBySharedGuestAndExpired(linkId, guestId, false)
                .orElseThrow(() -> new TripException("Couldn't find active link session"));

        PriceHaggle haggle = new PriceHaggle();
        haggle.setAmount(sharedStatus.getAmount());
        haggle.setSharedStatus(sharedStatus);
        priceHaggleRepository.save(haggle);

        PriceDiscussionResponse response = new PriceDiscussionResponse();
        response.setOptions(MoneyUtil.generateOptionsFromAmount(haggle.getAmount()));
        response.setAmount(MoneyUtil.formatToNaira(haggle.getAmount()));
        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<PriceDiscussionResponse> chat(PriceChatRequest request) {
        SharedStatus sharedStatus = sharedStatusRepository.findBySharedGuestAndExpired(
                request.getLinkId(), request.getGuestId(), false)
                .orElseThrow(() -> new TripException("Couldn't find active link session"));
        PriceHaggle haggle = priceHaggleRepository.findBySharedStatus_Id(sharedStatus.getId())
                .orElseThrow(() -> new TripException("Cannot start chat without initiating conversation"));

        PriceChat chat = new PriceChat();
        chat.setHaggle(haggle);
        chat.setSender(request.getSender());
        chat.setMessage(request.getMessage());
        priceChatRepository.save(chat);


        PriceDiscussionResponse response = new PriceDiscussionResponse();
        response.setOptions(MoneyUtil.generateOptionsFromAmount(haggle.getAmount()));
        response.setDiscussions(
                priceChatRepository.findByHaggle_Id(haggle.getId())
                        .stream()
                        .sorted(Comparator.comparing(PriceChat::getCreatedAt))
                        .map(priceChat -> {
                            PriceDiscussion discussion = new PriceDiscussion();
                            discussion.setIsProvider(!priceChat.getSender().equals(request.getGuestId()));
                            discussion.setMessage(priceChat.getMessage());
                            discussion.setStatus(MessageStatus.SENT);
                            discussion.setLabel(TimeUtil.formatDay(priceChat.getCreatedAt()));
                            return discussion;
                        })
                        .toList()
        );
        response.setAmount(MoneyUtil.formatToNaira(haggle.getAmount()));
        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<String> change(Integer amount, String guestId, String linkId) {
        SharedStatus sharedStatus = sharedStatusRepository.findBySharedGuestAndExpired(linkId, guestId, false)
                .orElseThrow(() -> new TripException("Couldn't find active link session"));
        PriceHaggle haggle = priceHaggleRepository.findBySharedStatus_Id(sharedStatus.getId())
                .orElseThrow(() -> new TripException("Cannot change price without initiating conversation"));

        haggle.setAmount(BigDecimal.valueOf(amount));
        haggle.setUpdatedAt(LocalDateTime.now());
        priceHaggleRepository.save(haggle);
        return new ApiResponse<>("Success", MoneyUtil.formatToNaira(haggle.getAmount()), HttpStatus.OK);
    }

    @Override
    public ApiResponse<String> request(ProvideSharedTripRequest request) {
        SharedStatus sharedStatus = sharedStatusRepository.findBySharedGuestAndExpired(
                        request.getLinkId(), request.getGuest(), false)
                .orElseThrow(() -> new TripException("Couldn't find active link session"));
        PriceHaggle haggle = priceHaggleRepository.findBySharedStatus_Id(sharedStatus.getId())
                .orElseThrow(() -> new TripException("Cannot change price without initiating conversation"));
        Profile provider = sharedStatus.getSharedLogin().getSharedLink().getProvider();

        if(tripRepository.existsByStatusAndProvider(ON_TRIP, ACCEPTED, provider.getId())) {
            throw new TripException("%s is currently on a trip".formatted(provider.getFullName()));
        } else if(tripRepository.existsByStatusAndAccount(ON_TRIP, ACCEPTED, request.getGuest())) {
            throw new TripException("You have an active trip");
        } else {
//            Trip trip = new Trip();
//            trip.setAccount(request.getGuest());
//            trip.setProvider(provider);
//            trip.setAddress(TripMapper.INSTANCE.address(request.getAddress()));
//            Trip saved = tripRepository.save(trip);
//
//            BigDecimal user = MoneyUtil.getDecimal(TRIP_USER_SHARE, haggle.getAmount());
//            BigDecimal serch = MoneyUtil.getDecimal(TRIP_SERCH_SHARE, haggle.getAmount());
//
//            SharedPricing pricing = new SharedPricing();
//            pricing.setStatus(sharedStatus);
//            pricing.setTrip(saved);
//            pricing.setAmount(haggle.getAmount());
//            pricing.setUser(user);
//            pricing.setProvider(haggle.getAmount().subtract(serch.add(user)));
//            sharedPricingRepository.save(pricing);
            return new ApiResponse<>("Success", HttpStatus.CREATED);
        }
    }

    @Override
    public ApiResponse<String> cancel(ProvideSharedTripCancelRequest request) {
        Trip trip = tripRepository.findByIdAndAccount(request.getTrip(), request.getGuest())
                .orElseThrow(() -> new TripException("Trip not found"));

        return tripService.getCancelResponse(trip, request.getReason());
    }

    @Override
    public ApiResponse<String> end(String tripId, String guestId) {
        Trip trip = tripRepository.findByIdAndAccount(tripId, guestId)
                .orElseThrow(() -> new TripException("Trip not found"));

        tripService.updateTripWhenEnded(trip.getShared().getAmount().intValue(), trip);
        return new ApiResponse<>("Trip ended", trip.getId(), HttpStatus.OK);
    }
}
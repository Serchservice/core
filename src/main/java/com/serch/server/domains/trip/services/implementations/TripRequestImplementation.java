package com.serch.server.domains.trip.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.file.services.FileService;
import com.serch.server.core.notification.services.NotificationService;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.domains.shared.services.SharedService;
import com.serch.server.domains.trip.requests.QuotationRequest;
import com.serch.server.domains.trip.requests.TripAcceptRequest;
import com.serch.server.domains.trip.requests.TripCancelRequest;
import com.serch.server.domains.trip.requests.TripInviteRequest;
import com.serch.server.domains.trip.responses.TripResponse;
import com.serch.server.domains.trip.services.*;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.trip.TripMode;
import com.serch.server.exceptions.others.TripException;
import com.serch.server.mappers.TripMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.trip.*;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.trip.*;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.serch.server.enums.account.ProviderStatus.BUSY;
import static com.serch.server.enums.auth.Role.USER;
import static com.serch.server.enums.trip.TripMode.FROM_GUEST;
import static com.serch.server.enums.trip.TripMode.FROM_USER;
import static com.serch.server.enums.trip.TripStatus.ACTIVE;
import static com.serch.server.enums.trip.TripType.REQUEST;

@Service
@RequiredArgsConstructor
public class TripRequestImplementation implements TripRequestService {
    private static final Logger log = LoggerFactory.getLogger(TripRequestImplementation.class);
    private final FileService uploadService;
    private final NotificationService tripNotification;
    private final TripHistoryService historyService;
    private final ActiveService activeService;
    private final TripPayService payService;
    private final SharedService sharedService;
    private final TripService tripService;
    private final TripAuthenticationService authenticationService;
    private final AuthUtil authUtil;
    private final SimpMessagingTemplate messaging;
    private final ShoppingLocationRepository shoppingLocationRepository;
    private final ShoppingItemRepository shoppingItemRepository;
    private final TripInviteRepository tripInviteRepository;
    private final ActiveRepository activeRepository;
    private final ProfileRepository profileRepository;
    private final TripInviteQuotationRepository tripInviteQuotationRepository;
    private final TripRepository tripRepository;
    private final MapViewRepository mapViewRepository;

    @Value("${application.map.search-radius}")
    private String MAP_SEARCH_RADIUS;

    @Override
    @Transactional
    public ApiResponse<TripResponse> invite(TripInviteRequest request) {
        if(!authUtil.getGuestId().isEmpty()) {
            return guestInvite(request);
        } else {
            if(request.getProvider() == null && request.getCategory() == null) {
                throw new TripException("You must select a category or pick a provider in order to create an invite");
            } else {
                validateInviteRequest(request, null);

                TripInvite trip = create(request, String.valueOf(authUtil.getUser().getId()), FROM_USER, null);

                if(request.getAmount() != null && request.getProvider() != null) {
                    createAndSendQuotation(getQuotationRequestFromInvite(request), trip);
                }

                SerchCategory category = request.getCategory();
                if(request.getProvider() != null) {
                    category = profileRepository.findById(request.getProvider()).map(Profile::getCategory).orElse(request.getCategory());
                }

                if(category == SerchCategory.PERSONAL_SHOPPER) {
                    saveShoppingData(request, trip);
                }

                pingServiceProviders(request, trip, authUtil.getUser().getFullName(), String.valueOf(authUtil.getUser().getId()));

                return new ApiResponse<>(
                        "Request received. You will be notified when there is a quotation for this request.",
                        historyService.response(trip.getId(), String.valueOf(authUtil.getUser().getId())),
                        HttpStatus.OK
                );
            }
        }
    }

    private QuotationRequest getQuotationRequestFromInvite(TripInviteRequest request) {
        QuotationRequest quotation = new QuotationRequest();
        quotation.setAmount(request.getAmount());

        return quotation;
    }

    private void validateInviteRequest(TripInviteRequest request, String guest) {
        boolean canShop = request.getShoppingItems() != null && request.getShoppingItems().isEmpty();

        if(request.getCategory() == SerchCategory.PERSONAL_SHOPPER && canShop) {
            throw new TripException("You must add items you want to buy in order to proceed");
        } else if (request.getProvider() == null && guest == null) {
            if(HelperUtil.isUploadEmpty(request.getAudio()) && request.getProblem().isEmpty()) {
                throw new TripException("You must either describe your problem or send it as an audio file");
            }
        } else if (request.getProvider() != null && guest != null) {
            if(HelperUtil.isUploadEmpty(request.getAudio()) && request.getProblem().isEmpty()) {
                throw new TripException("You must either describe your problem or send it as an audio file");
            }
        }
    }

    @Transactional
    protected TripInvite create(TripInviteRequest request, String account, TripMode mode, String linkId) {
        TripInvite trip = TripMapper.INSTANCE.request(request);

        if(request.getProvider() != null) {
            trip.setCategory(profileRepository.findById(request.getProvider())
                    .map(Profile::getCategory)
                    .orElse(request.getCategory()));
            trip.setSelected(request.getProvider());
        }

        trip.setAccount(account);
        trip.setMode(mode);
        trip.setLinkId(linkId);

        trip = tripInviteRepository.save(trip);

        if(!HelperUtil.isUploadEmpty(request.getAudio())) {
            String url = uploadService.uploadTrip(request.getAudio(), trip.getId()).getFile();
            trip.setAudio(url);

            return tripInviteRepository.save(trip);
        } else {
            return trip;
        }
    }

    @Transactional
    protected void saveShoppingData(TripInviteRequest request, TripInvite trip) {
        if(request.getShoppingLocation() != null) {
            ShoppingLocation shoppingLocation = TripMapper.INSTANCE.shopping(request.getShoppingLocation());
            shoppingLocation.setInvite(trip);
            shoppingLocationRepository.save(shoppingLocation);
        }

        request.getShoppingItems().forEach(shoppingItem -> {
            ShoppingItem item = TripMapper.INSTANCE.shopping(shoppingItem);
            item.setAmount(BigDecimal.valueOf(shoppingItem.getAmount()));
            item.setInvite(trip);
            shoppingItemRepository.save(item);
        });
    }

    @Transactional
    protected void pingServiceProviders(TripInviteRequest request, TripInvite trip, String name, String account) {
        if(request.getProvider() != null) {
            messaging.convertAndSend(
                    "/platform/%s/trip/requested".formatted(String.valueOf(request.getProvider())),
                    historyService.response(trip.getId(), String.valueOf(request.getProvider()))
            );

            log.info(String.format("Sending notification trip invitation ping request to %s", request.getProvider()));
            tripNotification.send(
                    String.valueOf(request.getProvider()),
                    String.format("%s wants your service now", name),
                    "You have a new trip request. Tap to view details",
                    account, null, true
            );
        } else {
            List<Active> actives = activeRepository.sortAllWithinDistance(
                    request.getLatitude(), request.getLongitude(),
                    Double.parseDouble(MAP_SEARCH_RADIUS), request.getCategory().name()
            );

            if(actives != null && !actives.isEmpty()) {
                actives.forEach(active -> {
                    messaging.convertAndSend(
                            "/platform/%s/trip/requested".formatted(String.valueOf(active.getProfile().getId())),
                            historyService.response(trip.getId(), String.valueOf(active.getProfile().getId()))
                    );

                    log.info(String.format("Sending notification trip invitation ping request to %s", active.getProfile().getId()));
                    tripNotification.send(
                            String.valueOf(active.getProfile().getId()),
                            String.format("%s wants your service now", name),
                            "You have a new trip request. Tap to view details",
                            account, null, true
                    );
                });
            }
        }
    }

    @Transactional
    protected ApiResponse<TripResponse> guestInvite(TripInviteRequest request) {
        validateInviteRequest(request, authUtil.getGuestId());

        SharedLink link = authUtil.getShared().getSharedLink();
        Guest guest = authUtil.getGuest();

        if(link.isExpired()) {
            throw new TripException("Link has expired");
        }

        request.setCategory(link.getProvider().getCategory());
        TripInvite trip = create(request, guest.getId(), FROM_GUEST, link.getId());
        trip.setSelected(link.getProvider().getId());
        tripInviteRepository.save(trip);

        if(request.getCategory() == SerchCategory.PERSONAL_SHOPPER) {
            saveShoppingData(request, trip);
        }

        messaging.convertAndSend(
                "/platform/%s/trip/requested".formatted(String.valueOf(link.getProvider().getId())),
                historyService.response(trip.getId(), String.valueOf(link.getProvider().getId()))
        );
        tripNotification.send(
                String.valueOf(link.getProvider().getId()),
                String.format("%s wants your service now", guest.getFullName()),
                "You have a new trip request. Tap to view details",
                guest.getId(), null, true
        );

        return new ApiResponse<>(
                "Request received. You will be notified when there is a quotation for this request.",
                historyService.response(trip.getId(), guest.getId()),
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> sendQuotation(QuotationRequest request) {
        if(request.getAmount() != null) {
            TripInvite trip = tripInviteRepository.findById(request.getId())
                    .orElseThrow(() -> new TripException("Trip request not found"));

            if(!authUtil.getGuestId().isEmpty()) {
                return new ApiResponse<>(
                        "Quotation sent. Wait for response",
                        response(trip.getId(), authUtil.getGuestId(), authUtil.getGuest().getFullName(), request),
                        HttpStatus.OK
                );
            } else if(authUtil.getUser().getRole() == USER) {
                return new ApiResponse<>(
                        "Quotation sent. Wait for response",
                        response(trip.getId(), String.valueOf(authUtil.getUser().getId()), authUtil.getUser().getFullName(), request),
                        HttpStatus.OK
                );
            } else {
                createAndSendQuotation(request, trip);

                return new ApiResponse<>(
                        "Quotation sent. Wait for response",
                        historyService.response(trip.getId(), String.valueOf(authUtil.getUser().getId())),
                        HttpStatus.OK
                );
            }
        } else {
            throw new TripException("Your quotation must have an amount");
        }
    }

    private void createAndSendQuotation(QuotationRequest request, TripInvite trip) {
        Profile profile = profileRepository.findById(authUtil.getUser().isProvider() ? authUtil.getUser().getId() : trip.getSelected())
                .orElseThrow(() -> new TripException("Provider not found"));
        BigDecimal amount = BigDecimal.valueOf(request.getAmount());

        TripInviteQuotation quote = tripInviteQuotationRepository
                .findByInvite_IdAndProvider_id(trip.getId(), authUtil.getUser().getId())
                .orElseGet(() -> {
                    TripInviteQuotation quotation = new TripInviteQuotation();
                    quotation.setAmount(amount);
                    quotation.setUpdatedAt(TimeUtil.now());
                    quotation.setProvider(profile);
                    quotation.setInvite(trip);

                    return tripInviteQuotationRepository.save(quotation);
                });

        quote.setAmount(amount);
        quote.setAccount(null);
        quote.setUpdatedAt(TimeUtil.now());
        quote.setProvider(profile);
        tripInviteQuotationRepository.save(quote);

        messaging.convertAndSend(
                "/platform/%s/trip/requested".formatted(trip.getAccount()),
                historyService.response(trip.getId(), trip.getAccount())
        );
        messaging.convertAndSend(
                "/platform/%s/trip/requested/%s".formatted(trip.getAccount(), trip.getId()),
                historyService.response(trip.getId(), trip.getAccount())
        );

        tripNotification.send(
                String.valueOf(String.valueOf(authUtil.getUser().isProvider() ? trip.getAccount() : profile.getId())),
                String.format("%s sent in a quotation for your request", authUtil.getUser().getFullName()),
                String.format("Trip is now being charged at %s", MoneyUtil.formatToNaira(amount)),
                String.valueOf(authUtil.getUser().getId()), null, true
        );
    }

    @Transactional
    protected TripResponse response(String trip, String acct, String name, QuotationRequest req) {
        BigDecimal amount = BigDecimal.valueOf(req.getAmount());

        TripInviteQuotation quote = tripInviteQuotationRepository
                .findByIdAndInvite_Id(req.getQuoteId(), req.getId())
                .orElseThrow(() -> new TripException("No quote found. You will be notified when there is any."));

        quote.setAmount(amount);
        quote.setUpdatedAt(TimeUtil.now());
        quote.setAccount(acct);
        tripInviteQuotationRepository.save(quote);

        messaging.convertAndSend(
                "/platform/%s/trip/requested".formatted(String.valueOf(quote.getProvider().getId())),
                historyService.response(trip, String.valueOf(quote.getProvider().getId()))
        );
        messaging.convertAndSend(
                "/platform/%s/trip/requested/%s".formatted(String.valueOf(quote.getProvider().getId()), trip),
                historyService.response(trip, String.valueOf(quote.getProvider().getId()))
        );

        tripNotification.send(
                String.valueOf(quote.getProvider().getId()),
                String.format("%s updated the quotation you gave", name),
                String.format("Trip is now being charged at %s", MoneyUtil.formatToNaira(amount)),
                acct, trip, true
        );

        return historyService.response(trip, acct);
    }

    @Override
    @Transactional
    public ApiResponse<TripResponse> accept(TripAcceptRequest request) {
        TripInviteQuotation quote;
        String account;

        if(!authUtil.getGuestId().isEmpty()) {
            quote = tripInviteQuotationRepository
                    .findByIdAndInvite_IdAndInvite_Account(request.getQuoteId(), request.getTrip(), authUtil.getGuestId())
                    .orElseThrow(() -> new TripException("No trip found for trip " + request.getTrip()));
            account = authUtil.getGuestId();
        } else if(authUtil.getUser().getRole() == USER) {
            quote = tripInviteQuotationRepository
                    .findByIdAndInvite_IdAndInvite_Account(request.getQuoteId(), request.getTrip(), String.valueOf(authUtil.getUser().getId()))
                    .orElseThrow(() -> new TripException("No trip found for trip " + request.getTrip()));
            account = String.valueOf(authUtil.getUser().getId());
        } else {
            quote = tripInviteQuotationRepository.findByInvite_IdAndProvider_id(request.getTrip(), authUtil.getUser().getId())
                    .orElseThrow(() -> new TripException("No trip found for trip " + request.getTrip()));
            account = String.valueOf(authUtil.getUser().getId());
        }

        String requestedId = quote.getInvite().getId();
        Trip saved = buildTripFromRequest(quote);
        tripInviteRepository.delete(quote.getInvite());
        tripInviteRepository.flush();
        tripInviteQuotationRepository.delete(quote);
        tripInviteQuotationRepository.flush();

        saveMapView(saved);

        sharedService.create(saved.getLinkId(), saved.getAccount(), saved);
        InitializePaymentData data = payService.pay(saved, saved.getProvider().getId());
        authenticationService.create(saved, null);
        activeService.toggle(saved.getProvider().getUser(), BUSY, null);

        tripService.updateOthers(saved);

        messaging.convertAndSend(
                "/platform/%s/trip/requested/history".formatted(String.valueOf(saved.getProvider().getId())),
                historyService.requested(String.valueOf(saved.getProvider().getId()), null, null, null, true, saved.getId())
        );
        messaging.convertAndSend(
                "/platform/%s/trip/active/history".formatted(String.valueOf(saved.getProvider().getId())),
                historyService.active(String.valueOf(saved.getProvider().getId()), null, null, null, true, saved.getId())
        );

        messaging.convertAndSend(
                "/platform/%s/trip/requested/history".formatted(saved.getAccount()),
                historyService.requested(saved.getAccount(), authUtil.getLinkId(), null, null, true, saved.getId())
        );
        messaging.convertAndSend(
                "/platform/%s/trip/active/history".formatted(saved.getAccount()),
                historyService.active(saved.getAccount(), authUtil.getLinkId(), null, null, true, saved.getId())
        );

        if(saved.getProvider().isAssociate()) {
            messaging.convertAndSend(
                    "/platform/%s/trip/requested/history".formatted(String.valueOf(saved.getProvider().getBusiness().getId())),
                    historyService.requested(String.valueOf(saved.getProvider().getBusiness().getId()), null, null, null, true, saved.getId())
            );

            messaging.convertAndSend(
                    "/platform/%s/trip/active/history".formatted(String.valueOf(saved.getProvider().getBusiness().getId())),
                    historyService.active(String.valueOf(saved.getProvider().getBusiness().getId()), null, null, null, true, saved.getId())
            );
        }

        return new ApiResponse<>(historyService.response(saved.getId(), account, data, true, requestedId));
    }

    private void saveMapView(Trip saved) {
        MapView mapView = TripMapper.INSTANCE.view(activeService.getLocation(saved.getProvider().getUser()));
        mapView.setTrip(saved);
        mapViewRepository.save(mapView);
    }

    @Transactional
    protected Trip buildTripFromRequest(TripInviteQuotation quote) {
        Trip trip = TripMapper.INSTANCE.trip(quote.getInvite());

        trip.setType(REQUEST);
        trip.setStatus(ACTIVE);
        trip.setProvider(quote.getProvider());
        trip.setAmount(quote.getAmount());

        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public ApiResponse<String> cancel(TripCancelRequest request) {
        TripInvite trip = tripInviteRepository.findById(request.getTrip())
                .orElseThrow(() -> new TripException("Trip request not found"));

        if(!authUtil.getGuestId().isEmpty()) {
            if(trip.getAccount().equals(authUtil.getGuestId())) {
                return updateListWhenCancelled(trip);
            }
        } else if(String.valueOf(authUtil.getUser().getId()).equals(trip.getAccount())) {
            return updateListWhenCancelled(trip);
        }

        throw new TripException("Error while cancelling trip request. Request might not be made by you");
    }

    @Transactional
    protected ApiResponse<String> updateListWhenCancelled(TripInvite trip) {
        if(trip.getQuotes() != null && !trip.getQuotes().isEmpty()) {
            trip.getQuotes().forEach(q -> {
                messaging.convertAndSend(
                        "/platform/%s/trip/requested/history".formatted(String.valueOf(q.getProvider().getId())),
                        historyService.requested(String.valueOf(q.getProvider().getId()), null, null, null, true, trip.getId())
                );

                messaging.convertAndSend(
                        "/platform/%s/trip/history".formatted(String.valueOf(q.getProvider().getId())),
                        historyService.history(String.valueOf(q.getProvider().getId()), null, null, null, true, trip.getId())
                );
            });
        }

        messaging.convertAndSend(
                "/platform/%s/trip/requested/history".formatted(trip.getAccount()),
                historyService.requested(trip.getAccount(), authUtil.getLinkId(), null, null, true, trip.getId())
        );
        messaging.convertAndSend(
                "/platform/%s/trip/history".formatted(trip.getAccount()),
                historyService.history(trip.getAccount(), authUtil.getLinkId(), null, null, true, trip.getId())
        );

        tripInviteRepository.delete(trip);
        return new ApiResponse<>("Request is cancelled", HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponse<String> cancel(TripCancelRequest request, Long quoteId) {
        TripInviteQuotation quote = tripInviteQuotationRepository.findById(quoteId)
                .orElseThrow(() -> new TripException("No trip found for quote " + quoteId));

        messaging.convertAndSend(
                "/platform/%s/trip/requested/%s".formatted(quote.getAccount(), quote.getInvite().getId()),
                historyService.response(quote.getInvite().getId(), quote.getAccount())
        );
        messaging.convertAndSend(
                "/platform/%s/trip/requested/history".formatted(quote.getAccount()),
                historyService.requested(quote.getAccount(), authUtil.getLinkId(), null, null, true, quote.getInvite().getId())
        );

        tripInviteQuotationRepository.delete(quote);
        return new ApiResponse<>("Your quotation is cancelled", HttpStatus.OK);
    }
}

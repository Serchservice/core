package com.serch.server.admin.services.scopes.account.services.implementation;

import com.serch.server.admin.enums.EventType;
import com.serch.server.admin.enums.FeatureActivity;
import com.serch.server.admin.exceptions.PermissionException;
import com.serch.server.admin.mappers.AccountUserScopeMapper;
import com.serch.server.admin.services.account.services.AdminProfileService;
import com.serch.server.admin.services.responses.AnalysisResponse;
import com.serch.server.admin.services.responses.ChartMetric;
import com.serch.server.admin.services.responses.CommonProfileResponse;
import com.serch.server.admin.services.responses.auth.AccountMFAChallengeResponse;
import com.serch.server.admin.services.scopes.account.responses.user.*;
import com.serch.server.admin.services.scopes.account.services.AccountUserScopeService;
import com.serch.server.admin.services.scopes.common.services.CommonAccountAnalysisService;
import com.serch.server.admin.services.scopes.common.services.CommonAuthService;
import com.serch.server.admin.services.scopes.common.services.CommonProfileService;
import com.serch.server.bases.ApiResponse;
import com.serch.server.core.file.services.FileBuilderService;
import com.serch.server.core.file.services.FileService;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.call.CallType;
import com.serch.server.enums.shop.Weekday;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.models.account.AccountReport;
import com.serch.server.models.account.BusinessProfile;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.bookmark.Bookmark;
import com.serch.server.models.certificate.Certificate;
import com.serch.server.models.company.SpeakWithSerch;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.conversation.ChatRoom;
import com.serch.server.models.rating.Rating;
import com.serch.server.models.referral.Referral;
import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shared.SharedLogin;
import com.serch.server.models.shop.Shop;
import com.serch.server.models.shop.ShopWeekday;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.models.trip.Trip;
import com.serch.server.repositories.account.*;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.bookmark.BookmarkRepository;
import com.serch.server.repositories.certificate.CertificateRepository;
import com.serch.server.repositories.company.SpeakWithSerchRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.repositories.conversation.ChatMessageRepository;
import com.serch.server.repositories.conversation.ChatRoomRepository;
import com.serch.server.repositories.rating.AppRatingRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.referral.ReferralProgramRepository;
import com.serch.server.repositories.referral.ReferralRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.repositories.shared.GuestRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.shared.SharedLoginRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.shop.ShopWeekdayRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.repositories.trip.ActiveRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.repositories.trip.TripShareRepository;
import com.serch.server.domains.rating.responses.RatingChartResponse;
import com.serch.server.domains.rating.services.RatingService;
import com.serch.server.domains.schedule.responses.ScheduleTimeResponse;
import com.serch.server.domains.schedule.services.ScheduleService;
import com.serch.server.domains.shared.services.SharedService;
import com.serch.server.domains.transaction.services.WalletService;
import com.serch.server.utils.AdminUtil;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AccountUserScopeImplementation implements AccountUserScopeService {
    private final CommonAuthService authService;
    private final CommonAccountAnalysisService analysisService;
    private final ScheduleService scheduleService;
    private final SharedService sharedService;
    private final CommonProfileService profileService;
    private final WalletService walletService;
    private final RatingService ratingService;
    private final AdminProfileService adminProfileService;
    private final UserRepository userRepository;
    private final GuestRepository guestRepository;
    private final TripRepository tripRepository;
    private final SharedLinkRepository sharedLinkRepository;
    private final TransactionRepository transactionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ScheduleRepository scheduleRepository;
    private final CallRepository callRepository;
    private final TripShareRepository tripShareRepository;
    private final WalletRepository walletRepository;
    private final SharedLoginRepository sharedLoginRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ReferralProgramRepository referralProgramRepository;
    private final ReferralRepository referralRepository;
    private final SpeakWithSerchRepository speakWithSerchRepository;
    private final ProfileRepository profileRepository;
    private final AccountReportRepository accountReportRepository;
    private final ShopRepository shopRepository;
    private final ShopWeekdayRepository shopWeekdayRepository;
    private final CertificateRepository certificateRepository;
    private final RatingRepository ratingRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final ActiveRepository activeRepository;
    private final AdditionalInformationRepository additionalInformationRepository;
    private final AccountDeleteRepository accountDeleteRepository;
    private final AppRatingRepository appRatingRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final SpecialtyRepository specialtyRepository;

    @Override
    public ApiResponse<AccountUserScopeProfileResponse> profile(String id) {
        UUID uuid = HelperUtil.parseUUID(id);

        if(uuid != null) {
            User user = userRepository.findById(uuid).orElseThrow(() -> new SerchException("User not found"));
            AccountUserScopeProfileResponse response = AccountUserScopeMapper.instance.profile(user);

            if(user.isBusiness()) {
                BusinessProfile profile = businessProfileRepository.findById(user.getId()).orElseThrow(() -> new SerchException("Profile not found"));

                response.setId(profile.getId().toString());
                response.setAvatar(profile.getAvatar());
                response.setDetails(AccountUserScopeMapper.instance.details(profile));
                response.setOrganization(AccountUserScopeMapper.instance.organization(profile));
                adminProfileService.updateProfile(profile.getUpdatedAt(), profile.getCreatedAt(), profile.getUser(), response);
            } else {
                Profile profile = profileRepository.findById(user.getId()).orElseThrow(() -> new SerchException("Profile not found"));

                response.setAvatar(profile.getAvatar());
                response.setId(profile.getId().toString());
                response.setDetails(AccountUserScopeMapper.instance.details(profile));
                adminProfileService.updateProfile(profile.getUpdatedAt(), profile.getCreatedAt(), profile.getUser(), response);

                if(user.isProvider()) {
                    activeRepository.findByProfile_Id(user.getId())
                            .ifPresent(active -> response.setActive(AccountUserScopeMapper.instance.active(active)));

                    if(profile.isAssociate()) {
                        AccountUserScopeProfileResponse.Organization org = AccountUserScopeMapper.instance.organization(profile.getBusiness());
                        org.setAdmin(profileService.fromUser(profile.getBusiness().getUser()));
                        response.setOrganization(org);
                    } else {
                        additionalInformationRepository.findByProfile_Id(user.getId())
                                .ifPresent(additional -> response.setAdditional(AccountUserScopeMapper.instance.additional(additional)));
                    }

                    response.setSkills(
                            specialtyRepository.findByProfile_Id(user.getId())
                                    .stream()
                                    .map(AccountUserScopeMapper.instance::skill)
                                    .toList()
                    );
                }
            }

            accountDeleteRepository.findByUser_EmailAddress(user.getEmailAddress())
                    .ifPresent(delete -> response.setDelete(AccountUserScopeMapper.instance.delete(delete)));
            appRatingRepository.findByAccount(id)
                    .ifPresent(rating -> response.setAppRating(AccountUserScopeMapper.instance.rating(rating)));
            phoneInformationRepository.findByUser_Id(user.getId())
                    .ifPresent(phone -> response.setPhoneInformation(AccountUserScopeMapper.instance.phone(phone)));
            response.setYears(analysisService.years(user));

            return new ApiResponse<>(response);
        } else {
            Guest guest = guestRepository.findById(id).orElseThrow(() -> new SerchException("Guest not found"));
            AccountUserScopeProfileResponse response = AccountUserScopeMapper.instance.profile(guest);
            response.setDevice(AccountUserScopeMapper.instance.device(guest));

            AccountUserScopeProfileResponse.Details details = AccountUserScopeMapper.instance.details(guest);
            details.setCategory(SerchCategory.GUEST.getType());
            details.setImage(SerchCategory.GUEST.getImage());
            response.setDetails(details);

            accountDeleteRepository.findByUser_EmailAddress(guest.getEmailAddress())
                    .ifPresent(delete -> response.setDelete(AccountUserScopeMapper.instance.delete(delete)));
            appRatingRepository.findByAccount(id)
                    .ifPresent(rating -> response.setAppRating(AccountUserScopeMapper.instance.rating(rating)));
            response.setYears(analysisService.years(guest));

            return new ApiResponse<>(response);
        }
    }

    @Override
    public ApiResponse<AccountUserScopeAuthResponse> auth(UUID id, Integer page, Integer size) {
        User user = userRepository.findById(id).orElseThrow(() -> new SerchException("User not found"));

        AccountUserScopeAuthResponse response = authService.auth(user.getId(), page, size, new AccountUserScopeAuthResponse());
        response.setMfa(authService.mfa(user));

        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<List<AccountUserScopeRatingResponse>> rating(String id, Integer page, Integer size) {
        List<AccountUserScopeRatingResponse> response = new ArrayList<>();

        Page<Rating> ratings = ratingRepository.findByRated(id, HelperUtil.getPageable(page, size));
        if(ratings != null && ratings.hasContent()) {
            response = ratings.getContent().stream().map(rating -> {
                AccountUserScopeRatingResponse data = AccountUserScopeMapper.instance.rating(rating);
                if(rating.getEvent() != null && !rating.getEvent().isEmpty()) {
                    data.setType(callRepository.findById(rating.getEvent()).isPresent() ? EventType.CALL : EventType.TRIP);
                }
                data.setProfile(profileService.fromId(rating.getRater()));

                return data;
            }).toList();
        }

        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<List<RatingChartResponse>> rating(String id) {
        return new ApiResponse<>(ratingService.buildChart(id));
    }

    @Override
    public ApiResponse<AccountUserScopeWalletResponse> wallet(UUID id) {
        Wallet wallet = walletRepository.findByUser_Id(id).orElse(null);

        if(wallet != null) {
            return new ApiResponse<>(walletService.buildWallet(wallet, AccountUserScopeMapper.instance.wallet(wallet)));
        } else {
            return new ApiResponse<>("No wallet found");
        }
    }

    @Override
    public ApiResponse<AccountUserScopeCertificateResponse> certificate(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new SerchException("User not found"));

        if(user.isUser()) {
            return new ApiResponse<>("Success", HttpStatus.OK);
        } else {
            Optional<Certificate> certificate = certificateRepository.findByUser(user.getId());
            AccountUserScopeCertificateResponse response;

            if(certificate.isPresent()) {
                response = AccountUserScopeMapper.instance.certificate(certificate.get());
                response.setIsGenerated(true);
            } else {
                response = new AccountUserScopeCertificateResponse();
                response.setIsGenerated(false);
                response.setDocument(FileBuilderService.instance.supabase(HelperUtil.dummyCertificate));
            }

            return new ApiResponse<>(response);
        }
    }

    @Override
    public ApiResponse<AnalysisResponse> analysis(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new PermissionException("User not found"));

        AnalysisResponse response = new AnalysisResponse();
        response.setAuth(analysisService.auth(user, null));
        response.setStatus(analysisService.accountStatus(user, null));
        response.setYears(analysisService.years(user));

        return new ApiResponse<>(response);
    }

    @Override
    public ApiResponse<List<AccountUserScopeTransactionResponse>> transactions(UUID id, Integer page, Integer size) {
        Page<Transaction> transactions = transactionRepository.findByUser(
                String.valueOf(id),
                walletRepository.findByUser_Id(id).map(Wallet::getId).orElse(""),
                HelperUtil.getPageable(page, size)
        );

        if(transactions != null && transactions.hasContent()) {
            return new ApiResponse<>(transactions.getContent().stream().map(transaction -> {
                AccountUserScopeTransactionResponse response = AccountUserScopeMapper.instance.transaction(transaction);
                if(transaction.getEvent() != null && !transaction.getEvent().isEmpty()) {
                    response.setEventType(callRepository.findById(transaction.getEvent()).isPresent() ? EventType.CALL : EventType.TRIP);
                }

                return response;
            }).toList());
        } else {
            return new ApiResponse<>(new ArrayList<>());
        }
    }

    @Override
    public ApiResponse<List<AccountMFAChallengeResponse>> challenges(UUID id, Integer page, Integer size) {
        return new ApiResponse<>(authService.challenges(id, page, size));
    }

    @Override
    public ApiResponse<List<AccountUserScopeReportResponse>> reports(UUID id, Integer page, Integer size) {
        Page<AccountReport> reports = accountReportRepository.findByUser_Id(id, HelperUtil.getPageable(page, size));

        if(reports != null && reports.hasContent()) {
            return new ApiResponse<>(reports.getContent().stream().map(report -> {
                AccountUserScopeReportResponse response = AccountUserScopeMapper.instance.report(report);
                response.setReporter(profileService.fromUser(report.getUser()));

                return response;
            }).toList());
        } else {
            return new ApiResponse<>(new ArrayList<>());
        }
    }

    @Override
    public ApiResponse<List<CommonProfileResponse>> associates(UUID id, Integer page, Integer size) {
        Page<Profile> associates = profileRepository.findActiveAssociatesByBusinessId(id, HelperUtil.getPageable(page, size));

        if(associates != null && associates.hasContent()) {
            return new ApiResponse<>(associates.getContent().stream().map(user -> profileService.fromUser(user.getUser())).toList());
        } else {
            return new ApiResponse<>(new ArrayList<>());
        }
    }

    @Override
    public ApiResponse<List<AccountUserScopeShopResponse>> shops(UUID id, Integer page, Integer size) {
        Page<Shop> shops = shopRepository.findByUser_Id(id, HelperUtil.getPageable(page, size));

        if(shops != null && shops.hasContent()) {
            return new ApiResponse<>(shops.getContent().stream().map(shop -> {
                AccountUserScopeShopResponse response = AccountUserScopeMapper.instance.shop(shop);
                response.setOpen(shop.isOpen());
                if(shop.getWeekdays() != null && !shop.getWeekdays().isEmpty()) {
                    response.setWeekdays(shop.getWeekdays().stream().map(this::buildShopWeekday).toList());
                    DayOfWeek currentDay = LocalDateTime.now().getDayOfWeek();
                    shopWeekdayRepository.findByDayAndShop_Id(Weekday.valueOf(currentDay.name()), shop.getId())
                            .ifPresent(weekday -> response.setCurrent(buildShopWeekday(weekday)));
                }
                if(shop.getServices() != null && !shop.getServices().isEmpty()) {
                    response.setServices(shop.getServices().stream().map(AccountUserScopeMapper.instance::service).toList());
                }

                return response;
            }).toList());
        } else {
            return new ApiResponse<>(new ArrayList<>());
        }
    }

    private AccountUserScopeShopResponse.Weekday buildShopWeekday(ShopWeekday weekday) {
        AccountUserScopeShopResponse.Weekday day = AccountUserScopeMapper.instance.weekday(weekday);
        day.setDay(weekday.getDay().getDay());
        day.setClosing(TimeUtil.toString(weekday.getClosing()));
        day.setOpening(TimeUtil.toString(weekday.getOpening()));
        day.setOpen(weekday.getShop().isOpen());

        return day;
    }

    @Override
    public ApiResponse<List<AccountUserScopeScheduleResponse>> schedules(UUID id, Integer page, Integer size) {
        Page<Schedule> schedules = scheduleRepository.findByUserId(id, HelperUtil.getPageable(page, size));

        if(schedules != null && schedules.hasContent()) {
            return new ApiResponse<>(schedules.getContent().stream().map(AccountUserScopeMapper.instance::schedule).toList());
        } else {
            return new ApiResponse<>(new ArrayList<>());
        }
    }

    @Override
    public ApiResponse<List<ScheduleTimeResponse>> times(UUID id) {
        return scheduleService.times(id);
    }

    @Override
    public ApiResponse<List<AccountUserScopeTicketResponse>> tickets(UUID id, Integer page, Integer size) {
        Page<SpeakWithSerch> tickets = speakWithSerchRepository.findByUserId(id, HelperUtil.getPageable(page, size));
        if(tickets != null && tickets.hasContent()) {
            return new ApiResponse<>(
                    tickets.getContent().stream().map(ticket -> {
                        AccountUserScopeTicketResponse data = AccountUserScopeMapper.instance.ticket(ticket);
                        if(ticket.getAssignedAdmin() != null) {
                            data.setAdmin(profileService.fromUser(ticket.getAssignedAdmin().getUser()));
                        }

                        return data;
                    }).toList()
            );
        } else {
            return new ApiResponse<>(new ArrayList<>());
        }
    }

    @Override
    public ApiResponse<AccountUserScopeReferralResponse> referral(UUID id, Integer page, Integer size) {
        ReferralProgram program = referralProgramRepository.findByUser_Id(id).orElse(null);
        if(program != null) {
            AccountUserScopeReferralResponse response = AccountUserScopeMapper.instance.program(program);
            response.setReferrals(getReferrals(id, page, size));

            return new ApiResponse<>(response);
        }

        return new ApiResponse<>("Success", HttpStatus.OK);
    }

    private List<AccountUserScopeReferralResponse.Referral> getReferrals(UUID id, Integer page, Integer size) {
        Page<Referral> referrals = referralRepository.findByReferredBy_User_Id(id, HelperUtil.getPageable(page, size));
        if(referrals != null && referrals.hasContent()) {
            return referrals.getContent().stream().map(referral -> {
                AccountUserScopeReferralResponse.Referral data = AccountUserScopeMapper.instance.referral(referral);
                data.setProfile(profileService.fromUser(referral.getReferral()));

                return data;
            }).toList();
        }

        return new ArrayList<>();
    }

    @Override
    public ApiResponse<List<AccountUserScopeReferralResponse.Referral>> referrals(UUID id, Integer page, Integer size) {
        return new ApiResponse<>(getReferrals(id, page, size));
    }

    @Override
    public ApiResponse<List<AccountUserScopeBookmarkResponse>> bookmarks(UUID id, Integer page, Integer size) {
        Page<Bookmark> bookmarks = bookmarkRepository.findByUserId(id, HelperUtil.getPageable(page, size));

        if(bookmarks != null && bookmarks.hasContent()) {
            return new ApiResponse<>(bookmarks.getContent().stream().map(AccountUserScopeMapper.instance::bookmark).toList());
        } else {
            return new ApiResponse<>(new ArrayList<>());
        }
    }

    @Override
    public ApiResponse<List<AccountUserScopeCallResponse>> calls(UUID id, Integer page, Integer size) {
        Page<Call> calls = callRepository.findByUserId(id, HelperUtil.getPageable(page, size));

        if(calls != null && calls.hasContent()) {
            return new ApiResponse<>(calls.getContent().stream().map(AccountUserScopeMapper.instance::call).toList());
        } else {
            return new ApiResponse<>(new ArrayList<>());
        }
    }

    @Override
    public ApiResponse<List<AccountUserScopeTripResponse>> trips(String id, Integer page, Integer size) {
        UUID uuid = HelperUtil.parseUUID(id);
        Page<Trip> trips;

        if(uuid != null) {
            User user = userRepository.findById(uuid).orElseThrow(() -> new SerchException("User not found"));
            if(user.isUser()) {
                trips = tripRepository.findByAccount(id, HelperUtil.getPageable(page, size));
            } else {
                trips = tripRepository.findByProviderId(uuid, HelperUtil.getPageable(page, size));
            }
        } else {
            trips = tripRepository.findByAccount(id, HelperUtil.getPageable(page, size));
        }

        if(trips != null && trips.hasContent()) {
            return new ApiResponse<>(trips.getContent().stream().map(AccountUserScopeMapper.instance::trip).toList());
        } else {
            return new ApiResponse<>(new ArrayList<>());
        }
    }

    @Override
    public ApiResponse<List<AccountUserScopeChatRoomResponse>> chatRooms(UUID id, Integer page, Integer size) {
        Page<ChatRoom> rooms = chatRoomRepository.findByUserId(id, HelperUtil.getPageable(page, size));

        if(rooms != null && rooms.hasContent()) {
            return new ApiResponse<>(rooms.getContent().stream().map(AccountUserScopeMapper.instance::room).toList());
        }

        return new ApiResponse<>(new ArrayList<>());
    }

    @Override
    public ApiResponse<List<AccountUserScopeSharedLinkResponse>> sharedLinks(UUID id, Integer page, Integer size) {
        Page<SharedLink> links = sharedLinkRepository.findByUserId(id, HelperUtil.getPageable(page, size));

        if(links != null && links.hasContent()) {
            return new ApiResponse<>(links.getContent().stream().map(link -> {
                AccountUserScopeSharedLinkResponse response = AccountUserScopeMapper.instance.link(link);
                response.setCannotLogin(link.cannotLogin());
                response.setStatus(link.status());
                response.setIsExpired(link.isExpired());
                response.setNextStatus(link.nextLoginStatus());

                return response;
            }).toList());
        }

        return new ApiResponse<>(new ArrayList<>());
    }

    @Override
    public ApiResponse<List<AccountUserScopeSharedAccountResponse>> sharedAccounts(String id, Integer page, Integer size) {
        UUID uuid = HelperUtil.parseUUID(id);

        List<AccountUserScopeSharedAccountResponse> response = new ArrayList<>();
        if(uuid != null) {
            User user = userRepository.findById(uuid).orElseThrow(() -> new SerchException("User not found"));
            if(user.isUser()) {
                return new ApiResponse<>(getAccountList(page, size, user.getEmailAddress()));
            }
        } else {
            Guest guest = guestRepository.findById(id).orElseThrow(() -> new SerchException("Guest not found"));
            return new ApiResponse<>(getAccountList(page, size, guest.getEmailAddress()));
        }

        return new ApiResponse<>(response);
    }

    private List<AccountUserScopeSharedAccountResponse> getAccountList(Integer page, Integer size, String emailAddress) {
        List<AccountUserScopeSharedAccountResponse> response = new ArrayList<>();

        Page<SharedLogin> logins = sharedLoginRepository.findByGuest_EmailAddress(emailAddress, HelperUtil.getPageable(page, size));
        if(logins != null && logins.hasContent()) {
            response.addAll(
                    logins.getContent().stream().map(login -> {
                        AccountUserScopeSharedAccountResponse account = sharedService.buildWithLogin(login, new AccountUserScopeSharedAccountResponse());
                        account.setCanLogin(!login.getSharedLink().cannotLogin());
                        account.setCreatedAt(login.getCreatedAt());
                        account.setUpdatedAt(login.getUpdatedAt());

                        return account;
                    }).toList()
            );
        }

        User user = userRepository.findByEmailAddressIgnoreCase(emailAddress).orElse(null);
        if(user != null) {
            AccountUserScopeSharedAccountResponse account = sharedService.buildWithUser(user, new AccountUserScopeSharedAccountResponse());
            account.setCanLogin(true);
            account.setCreatedAt(user.getCreatedAt());
            account.setUpdatedAt(user.getUpdatedAt());

            response.add(account);
        }

        return response;
    }

    @Override
    public ApiResponse<List<ChartMetric>> fetchAuthChart(UUID id, Integer year) {
        User user = userRepository.findById(id).orElseThrow(() -> new PermissionException("User not found"));
        return new ApiResponse<>(analysisService.auth(user, year));
    }

    @Override
    public ApiResponse<List<ChartMetric>> fetchAccountStatusChart(UUID id, Integer year) {
        User user = userRepository.findById(id).orElseThrow(() -> new PermissionException("User not found"));
        return new ApiResponse<>(analysisService.accountStatus(user, year));
    }

    @Override
    public ApiResponse<List<AccountUserScopeActivityResponse>> fetchActivity(String id, Integer year) {
        UUID uuid = HelperUtil.parseUUID(id);

        List<AccountUserScopeActivityResponse> response;
        if(uuid != null) {
            User user = userRepository.findById(uuid).orElseThrow(() -> new SerchException("User not found"));
            response = Arrays.stream(FeatureActivity.values())
                    .map(act -> buildChatActivityResponse(user, act, year))
                    .toList();
        } else {
            Guest guest = guestRepository.findById(id).orElseThrow(() -> new SerchException("Guest not found"));
            response = Arrays.stream(FeatureActivity.values())
                    .map(act -> buildChatActivityResponse(guest, act, year))
                    .toList();
        }

        return new ApiResponse<>(response);
    }

    private AccountUserScopeActivityResponse buildChatActivityResponse(User user, FeatureActivity act, Integer year) {
        return switch(act) {
            case CHAT -> {
                if(user.isUser() || user.isProvider()) {
                    AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                    response.setFeature(act.getValue());
                    response.setActivities(getMonthlyActivities(year, day -> Math.min(1.0, chatMessageRepository.countMessagesSentByUser(user.getId(), day, day.plusDays(1)) / 100.0)));

                    yield response;
                } else {
                    yield new AccountUserScopeActivityResponse();
                }
            }
            case TRIP -> {
                AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                response.setFeature(act.getValue());
                response.setActivities(getMonthlyActivities(year, day -> {
                    if(user.isUser()) {
                        return Math.min(1.0, tripRepository.countByAccountAndDate(String.valueOf(user.getId()), day, day.plusDays(1)) / 100.0);
                    } else {
                        return Math.min(1.0, tripRepository.countByAccountAndDate(user.getId(), day, day.plusDays(1)) / 100.0);
                    }
                }));

                yield response;
            }
            case CHATROOM -> {
                if(user.isUser() || user.isProvider()) {
                    AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                    response.setFeature(act.getValue());
                    response.setActivities(getMonthlyActivities(year, day -> Math.min(1.0, chatRoomRepository.countByAccountAndDate(user.getId(), day, day.plusDays(1)) / 100.0)));

                    yield response;
                } else {
                    yield new AccountUserScopeActivityResponse();
                }
            }
            case SCHEDULE -> {
                AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                response.setFeature(act.getValue());
                response.setActivities(getMonthlyActivities(year, day -> Math.min(1.0, scheduleRepository.countByIdAndDate(user.getId(), day, day.plusDays(1)) / 100.0)));

                yield response;
            }
            case TIP2FIX -> {
                if(user.isUser() || user.isProvider()) {
                    AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                    response.setFeature(act.getValue());
                    response.setActivities(getMonthlyActivities(year, day -> Math.min(1.0, callRepository.countByIdAndType(user.getId(), CallType.T2F, day, day.plusDays(1)) / 100.0)));

                    yield response;
                } else {
                    yield new AccountUserScopeActivityResponse();
                }
            }
            case VOICE_CALL -> {
                if(user.isUser() || user.isProvider()) {
                    AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                    response.setFeature(act.getValue());
                    response.setActivities(getMonthlyActivities(year, day -> Math.min(1.0, callRepository.countByIdAndType(user.getId(), CallType.VOICE, day, day.plusDays(1)) / 100.0)));

                    yield response;
                } else {
                    yield new AccountUserScopeActivityResponse();
                }
            }
            case SHARED_LINK -> {
                if(user.isUser() || user.isProvider()) {
                    AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                    response.setFeature(act.getValue());
                    response.setActivities(getMonthlyActivities(year, day -> Math.min(1.0, sharedLinkRepository.countByIdAndDateRange(user.getId(), day, day.plusDays(1)) / 100.0)));

                    yield response;
                } else {
                    yield new AccountUserScopeActivityResponse();
                }
            }
            case TRANSACTION -> {
                AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                response.setFeature(act.getValue());
                response.setActivities(getMonthlyActivities(year, day -> Math.min(
                        1.0,
                        transactionRepository.countByUserAndDate(
                                String.valueOf(user.getId()),
                                walletRepository.findByUser_Id(user.getId()).map(Wallet::getId).orElse(""),
                                day, day.plusDays(1)
                        ) / 100.0
                )));

                yield response;
            }
            case SHARED_INVITE -> {
                if(user.isProvider()) {
                    AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                    response.setFeature(act.getValue());
                    response.setActivities(getMonthlyActivities(year, day -> Math.min(1.0, tripShareRepository.countByIdAndDateRange(user.getId(), day, day.plusDays(1)) / 100.0)));

                    yield response;
                } else {
                    yield new AccountUserScopeActivityResponse();
                }
            }
        };
    }

    private List<AccountUserScopeActivityResponse.MonthlyActivity> getMonthlyActivities(Integer year, Function<ZonedDateTime, Double> calc) {
        ZonedDateTime start = AdminUtil.getStartYear(year);

        return IntStream.rangeClosed(1, 12)
                .mapToObj(month -> {
                    YearMonth yearMonth = YearMonth.of(start.getYear(), month);
                    ZonedDateTime startMonth = start.withMonth(month);

                    AccountUserScopeActivityResponse.MonthlyActivity monthly = new AccountUserScopeActivityResponse.MonthlyActivity();
                    monthly.setMonth(startMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));

                    List<AccountUserScopeActivityResponse.DailyActivity> dailyActivities = IntStream.rangeClosed(1, yearMonth.lengthOfMonth())
                            .mapToObj(day -> {
                                ZonedDateTime currentDay = startMonth.withDayOfMonth(day);

                                AccountUserScopeActivityResponse.DailyActivity daily = new AccountUserScopeActivityResponse.DailyActivity();
                                daily.setDay(day);
                                daily.setValue(calc.apply(currentDay));

                                return daily;
                            })
                            .collect(Collectors.toList());

                    monthly.setDays(dailyActivities);

                    return monthly;
                })
                .collect(Collectors.toList());
    }

    private AccountUserScopeActivityResponse buildChatActivityResponse(Guest guest, FeatureActivity act, Integer year) {
        return switch(act) {
            case TRIP -> {
                AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                response.setFeature(act.getValue());
                response.setActivities(getMonthlyActivities(year, day -> Math.min(1.0, tripRepository.countByAccountAndDate(guest.getId(), day, day.plusDays(1)) / 100.0)));

                yield response;
            }
            case SHARED_LINK -> {
                AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                response.setFeature(act.getValue());
                response.setActivities(getMonthlyActivities(year, day -> Math.min(1.0, sharedLinkRepository.countByIdAndDateRange(guest.getId(), day, day.plusDays(1)) / 100.0)));

                yield response;
            }
            case TRANSACTION -> {
                AccountUserScopeActivityResponse response = new AccountUserScopeActivityResponse();
                response.setFeature(act.getValue());
                response.setActivities(getMonthlyActivities(year, day -> Math.min(1.0, transactionRepository.countByAccountAndDate(guest.getId(), day, day.plusDays(1)) / 100.0)));

                yield response;
            }
            default -> new AccountUserScopeActivityResponse();
        };
    }
}
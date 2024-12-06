package com.serch.server.admin.mappers;

import com.serch.server.admin.services.scopes.account.responses.user.*;
import com.serch.server.models.account.*;
import com.serch.server.models.auth.User;
import com.serch.server.models.bookmark.Bookmark;
import com.serch.server.models.certificate.Certificate;
import com.serch.server.models.company.SpeakWithSerch;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.conversation.ChatRoom;
import com.serch.server.models.rating.AppRating;
import com.serch.server.models.rating.Rating;
import com.serch.server.models.referral.Referral;
import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.models.shared.Guest;
import com.serch.server.models.shared.SharedLink;
import com.serch.server.models.shop.Shop;
import com.serch.server.models.shop.ShopSpecialty;
import com.serch.server.models.shop.ShopWeekday;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.transaction.Wallet;
import com.serch.server.models.trip.Active;
import com.serch.server.models.trip.Trip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountUserScopeMapper {
    AccountUserScopeMapper instance = Mappers.getMapper(AccountUserScopeMapper.class);

    @Mapping(target = "amount", source = "amount", ignore = true)
    AccountUserScopeSharedLinkResponse link(SharedLink link);

    AccountUserScopeChatRoomResponse room(ChatRoom room);

    AccountUserScopeTripResponse trip(Trip trip);

    AccountUserScopeCallResponse call(Call call);

    @Mapping(target = "id", source = "bookmarkId")
    AccountUserScopeBookmarkResponse bookmark(Bookmark bookmark);

    @Mapping(target = "referralLink", source = "referLink")
    AccountUserScopeReferralResponse program(ReferralProgram program);

    @Mapping(target = "id", source = "referId")
    AccountUserScopeReferralResponse.Referral referral(Referral referral);

    AccountUserScopeTicketResponse ticket(SpeakWithSerch serch);

    AccountUserScopeReportResponse report(AccountReport report);

    @Mapping(target = "sender", source = "sender", ignore = true)
    AccountUserScopeTransactionResponse transaction(Transaction transaction);

    AccountUserScopeScheduleResponse schedule(Schedule schedule);

    @Mappings({
            @Mapping(target = "phone", source = "phoneNumber"),
            @Mapping(target = "category", source = "category.type"),
            @Mapping(target = "image", source = "category.image"),
            @Mapping(target = "services", source = "services"),
            @Mapping(target = "weekdays", source = "weekdays")
    })
    AccountUserScopeShopResponse shop(Shop shop);

    AccountUserScopeShopResponse.Service service(ShopSpecialty specialty);

    @Mappings({
            @Mapping(target = "opening", source = "opening", ignore = true),
            @Mapping(target = "closing", source = "closing", ignore = true),
            @Mapping(target = "day", source = "day", ignore = true),
    })
    AccountUserScopeShopResponse.Weekday weekday(ShopWeekday weekday);

    AccountUserScopeCertificateResponse certificate(Certificate certificate);

    @Mappings({
            @Mapping(target = "balance", source = "balance", ignore = true),
            @Mapping(target = "deposit", source = "deposit", ignore = true),
            @Mapping(target = "uncleared", source = "uncleared", ignore = true),
            @Mapping(target = "payout", source = "payout", ignore = true)
    })
    AccountUserScopeWalletResponse wallet(Wallet wallet);

    AccountUserScopeRatingResponse rating(Rating rating);

    @Mappings({
            @Mapping(target = "setting", source = "setting"),
            @Mapping(target = "verification", source = "verification"),
    })
    AccountUserScopeProfileResponse profile(User user);

    @Mappings({
            @Mapping(target = "messagingToken", source = "fcmToken"),
            @Mapping(target = "category", source = "category.type"),
            @Mapping(target = "image", source = "category.image"),
            @Mapping(target = "gender", source = "gender.type"),
            @Mapping(target = "role", source = "user.role"),
            @Mapping(target = "status", source = "user.status"),
    })
    AccountUserScopeProfileResponse.Details details(Profile profile);

    AccountUserScopeProfileResponse.Additional additional(AdditionalInformation additionalInformation);

    AccountUserScopeProfileResponse.Active active(Active active);

    @Mapping(target = "number", source = "phoneNumber")
    AccountUserScopeProfileResponse.PhoneInformation phone(PhoneInformation phone);

    @Mappings({
            @Mapping(target = "name", source = "businessName"),
            @Mapping(target = "category", source = "category.type"),
            @Mapping(target = "image", source = "category.image"),
            @Mapping(target = "logo", source = "businessLogo"),
            @Mapping(target = "description", source = "businessDescription"),
            @Mapping(target = "address", source = "businessAddress"),
    })
    AccountUserScopeProfileResponse.Organization organization(BusinessProfile profile);

    @Mappings({
            @Mapping(target = "messagingToken", source = "fcmToken"),
            @Mapping(target = "category", source = "category.type"),
            @Mapping(target = "image", source = "category.image"),
            @Mapping(target = "gender", source = "gender.type"),
            @Mapping(target = "role", source = "user.role"),
            @Mapping(target = "status", source = "user.status"),
    })
    AccountUserScopeProfileResponse.Details details(BusinessProfile profile);

    @Mapping(target = "skill", source = "specialty")
    AccountUserScopeProfileResponse.Skill skill(Specialty skill);

    @Mapping(target = "device", source = "device", ignore = true)
    AccountUserScopeProfileResponse profile(Guest guest);

    AccountUserScopeProfileResponse.Device device(Guest guest);

    @Mappings({
            @Mapping(target = "messagingToken", source = "fcmToken"),
            @Mapping(target = "gender", source = "gender.type"),
    })
    AccountUserScopeProfileResponse.Details details(Guest profile);

    AccountUserScopeProfileResponse.Delete delete(AccountDelete delete);

    AccountUserScopeProfileResponse.AppRating rating(AppRating rating);
}
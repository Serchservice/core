package com.serch.server.services.removal;

import com.serch.server.enums.auth.Role;
import com.serch.server.enums.company.IssueStatus;
import com.serch.server.models.account.AccountDelete;
import com.serch.server.models.business.BusinessProfile;
import com.serch.server.models.auth.User;
import com.serch.server.repositories.account.*;
import com.serch.server.repositories.auth.SessionRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.auth.mfa.MFAFactorRepository;
import com.serch.server.repositories.bookmark.BookmarkRepository;
import com.serch.server.repositories.business.BusinessProfileRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.repositories.company.IssueRepository;
import com.serch.server.repositories.conversation.ChatMessageRepository;
import com.serch.server.repositories.conversation.ChatRoomRepository;
import com.serch.server.repositories.rating.RatingRepository;
import com.serch.server.repositories.referral.ReferralProgramRepository;
import com.serch.server.repositories.referral.ReferralRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.subscription.SubscriptionAssociateRepository;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.repositories.trip.ActiveRepository;
import com.serch.server.repositories.trip.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountRemovalImplementation implements AccountRemovalService {
    private final AccountDeleteRepository accountDeleteRepository;
    private final ProfileRepository profileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final SpecialtyRepository specialtyRepository;
    private final AccountReportRepository accountReportRepository;
    private final AccountSettingRepository accountSettingRepository;
    private final AdditionalInformationRepository additionalInformationRepository;
    private final PhoneInformationRepository phoneInformationRepository;
    private final MFAFactorRepository mFAFactorRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;
    private final IssueRepository issueRepository;
    private final CallRepository callRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RatingRepository ratingRepository;
    private final ReferralRepository referralRepository;
    private final ReferralProgramRepository referralProgramRepository;
    private final ScheduleRepository scheduleRepository;
    private final ShopRepository shopRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final ActiveRepository activeRepository;
    private final TripRepository tripRepository;
    private final SubscriptionAssociateRepository subscriptionAssociateRepository;

    @Override
    public void remove() {
        LocalDateTime date = LocalDateTime.now().minusYears(5);
        List<IssueStatus> statuses = Arrays.asList(IssueStatus.RESOLVED, IssueStatus.CLOSED);

        List<AccountDelete> list = accountDeleteRepository.findByStatusInAndCreatedAtBefore(statuses, date);
        if(!list.isEmpty()) {
            list.forEach(accountDelete -> {
                if(accountDelete.getUser().getRole() == Role.BUSINESS) {
                    removeBusiness(accountDelete.getUser());
                } else if(accountDelete.getUser().getRole() == Role.USER) {
                    removeUser(accountDelete.getUser());
                } else if(accountDelete.getUser().getRole() == Role.PROVIDER) {
                    removeUser(accountDelete.getUser());
                } else {
                    removeAssociate(accountDelete.getUser());
                }
                userRepository.delete(accountDelete.getUser());
            });
        }
    }

    private void removeFromOtherTables(User user) {
        /// ACCOUNT MODELS
        accountReportRepository.deleteAll(accountReportRepository.findByUser_Id(user.getId()));
        accountReportRepository.deleteAll(accountReportRepository.findByAccount(String.valueOf(user.getId())));
        accountSettingRepository.findByUser_Id(user.getId()).ifPresent(accountSettingRepository::delete);
        additionalInformationRepository.findByProfile_Id(user.getId()).ifPresent(additionalInformationRepository::delete);
        phoneInformationRepository.findByUser_Id(user.getId()).ifPresent(phoneInformationRepository::delete);
        specialtyRepository.deleteAll(specialtyRepository.findByProfile_Id(user.getId()));

        /// AUTH MODELS
        mFAFactorRepository.findByUser_Id(user.getId()).ifPresent(mFAFactorRepository::delete);
        sessionRepository.deleteAll(sessionRepository.findByUser_Id(user.getId()));

        /// OTHERS
        /// TODO::: Add Conversation Models and Certificate, Verified
        bookmarkRepository.deleteAll(bookmarkRepository.findByUserId(user.getId()));
        issueRepository.deleteAll(issueRepository.findByUser_Id(user.getId()));
        callRepository.deleteAll(callRepository.findByUserId(user.getId()));
        chatMessageRepository.deleteAll(chatMessageRepository.findByUserId(user.getId()));
        chatRoomRepository.deleteAll(chatRoomRepository.findByUserId(user.getId()));
        ratingRepository.deleteAll(ratingRepository.findByRated(String.valueOf(user.getId())));
        ratingRepository.deleteAll(ratingRepository.findByRater(String.valueOf(user.getId())));

        /// REFERRAL
        referralRepository.findByReferral_Id(user.getId()).ifPresent(referralRepository::delete);
        referralRepository.deleteAll(referralRepository.findByReferredBy_User_EmailAddress(user.getEmailAddress()));
        referralProgramRepository.findByUser_Id(user.getId()).ifPresent(referralProgramRepository::delete);

        /// SCHEDULE
        scheduleRepository.deleteAll(scheduleRepository.findByUser_Id(user.getId()));

        shopRepository.deleteAll(shopRepository.findByUser_Id(user.getId()));
        subscriptionRepository.findByUser_Id(user.getId()).ifPresent(subscriptionRepository::delete);
        transactionRepository.deleteAll(transactionRepository.findBySender_User_Id(user.getId()));
        transactionRepository.deleteAll(transactionRepository.findByAccount(String.valueOf(user.getId())));
        walletRepository.findByUser_Id(user.getId()).ifPresent(walletRepository::delete);
        activeRepository.findByProfile_Id(user.getId()).ifPresent(activeRepository::delete);
        tripRepository.deleteAll(tripRepository.findByProviderId(user.getId()));
        tripRepository.deleteAll(tripRepository.findByAccount(String.valueOf(user.getId())));
    }

    private void removeAssociate(User user) {
        profileRepository.findById(user.getId())
                .ifPresent(profile -> {
                    BusinessProfile business = profile.getBusiness();
                    business.getAssociates().remove(profile);
                    businessProfileRepository.save(business);
                    subscriptionAssociateRepository.deleteAll(subscriptionAssociateRepository.findByProfile_Id(user.getId()));
                    removeFromOtherTables(user);
                    profileRepository.delete(profile);
                });
    }

    private void removeUser(User user) {
        profileRepository.findById(user.getId())
                .ifPresent(profile -> {
                    removeFromOtherTables(user);
                    profileRepository.delete(profile);
                });
    }

    private void removeBusiness(User user) {
        businessProfileRepository.findById(user.getId())
                .ifPresent(business -> {
                    removeFromOtherTables(user);
                    businessProfileRepository.delete(business);
                });
    }
}

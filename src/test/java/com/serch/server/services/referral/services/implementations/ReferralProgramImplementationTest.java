package com.serch.server.services.referral.services.implementations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.referral.ReferralReward;
import com.serch.server.exceptions.others.ReferralException;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.mfa.MFAFactor;
import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.repositories.referral.ReferralProgramRepository;
import com.serch.server.repositories.referral.ReferralRepository;
import com.serch.server.repositories.shared.SharedLinkRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.services.referral.responses.ReferralProgramData;
import com.serch.server.services.referral.responses.ReferralProgramResponse;
import com.serch.server.services.referral.services.ReferralService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {ReferralProgramImplementation.class})
@ExtendWith(SpringExtension.class)
class ReferralProgramImplementationTest {
    @Autowired
    private ReferralProgramImplementation referralProgramImplementation;

    @MockBean
    private ReferralProgramRepository referralProgramRepository;

    @MockBean
    private ReferralRepository referralRepository;

    @MockBean
    private ReferralService referralService;

    @MockBean
    private SharedLinkRepository sharedLinkRepository;

    @MockBean
    private TripRepository tripRepository;

    /**
     * Method under test: {@link ReferralProgramImplementation#create(User)}
     */
    @Test
    void testCreate() {
        // Arrange
        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(new User());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(new User());

        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(mfaFactor);
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(program);
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor2 = new MFAFactor();
        mfaFactor2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor2.setId(UUID.randomUUID());
        mfaFactor2.setMfaChallenges(new ArrayList<>());
        mfaFactor2.setRecoveryCodes(new ArrayList<>());
        mfaFactor2.setSecret("Secret");
        mfaFactor2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor2.setUser(user);

        MFAFactor mfaFactor3 = new MFAFactor();
        mfaFactor3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor3.setId(UUID.randomUUID());
        mfaFactor3.setMfaChallenges(new ArrayList<>());
        mfaFactor3.setRecoveryCodes(new ArrayList<>());
        mfaFactor3.setSecret("Secret");
        mfaFactor3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor3.setUser(new User());

        ReferralProgram program2 = new ReferralProgram();
        program2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program2.setId(UUID.randomUUID());
        program2.setMilestone(1);
        program2.setReferLink("Refer Link");
        program2.setReferralCode("Referral Code");
        program2.setReward(ReferralReward.REFER_TIERED);
        program2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program2.setUser(new User());

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(mfaFactor3);
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(program2);
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program3 = new ReferralProgram();
        program3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program3.setCredits(new BigDecimal("2.3"));
        program3.setId(UUID.randomUUID());
        program3.setMilestone(1);
        program3.setReferLink("Refer Link");
        program3.setReferralCode("Referral Code");
        program3.setReward(ReferralReward.REFER_TIERED);
        program3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program3.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor2);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program3);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram referralProgram = new ReferralProgram();
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);
        when(referralProgramRepository.save(Mockito.<ReferralProgram>any())).thenReturn(referralProgram);

        MFAFactor mfaFactor4 = new MFAFactor();
        mfaFactor4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setId(UUID.randomUUID());
        mfaFactor4.setMfaChallenges(new ArrayList<>());
        mfaFactor4.setRecoveryCodes(new ArrayList<>());
        mfaFactor4.setSecret("Secret");
        mfaFactor4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setUser(new User());

        ReferralProgram program4 = new ReferralProgram();
        program4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program4.setId(UUID.randomUUID());
        program4.setMilestone(1);
        program4.setReferLink("Refer Link");
        program4.setReferralCode("Referral Code");
        program4.setReward(ReferralReward.REFER_TIERED);
        program4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program4.setUser(new User());

        User user4 = new User();
        user4.setAccountStatus(AccountStatus.SUSPENDED);
        user4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setEmailAddress("42 Main St");
        user4.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setFirstName("Jane");
        user4.setId(UUID.randomUUID());
        user4.setLastName("Doe");
        user4.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setMfaEnabled(true);
        user4.setMfaFactor(mfaFactor4);
        user4.setPassword("iloveyou");
        user4.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setPasswordRecoveryToken("ABC123");
        user4.setProgram(program4);
        user4.setRecoveryCodeEnabled(true);
        user4.setRole(Role.USER);
        user4.setSessions(new ArrayList<>());
        user4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor5 = new MFAFactor();
        mfaFactor5.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor5.setId(UUID.randomUUID());
        mfaFactor5.setMfaChallenges(new ArrayList<>());
        mfaFactor5.setRecoveryCodes(new ArrayList<>());
        mfaFactor5.setSecret("Secret");
        mfaFactor5.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor5.setUser(user4);

        MFAFactor mfaFactor6 = new MFAFactor();
        mfaFactor6.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor6.setId(UUID.randomUUID());
        mfaFactor6.setMfaChallenges(new ArrayList<>());
        mfaFactor6.setRecoveryCodes(new ArrayList<>());
        mfaFactor6.setSecret("Secret");
        mfaFactor6.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor6.setUser(new User());

        ReferralProgram program5 = new ReferralProgram();
        program5.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program5.setId(UUID.randomUUID());
        program5.setMilestone(1);
        program5.setReferLink("Refer Link");
        program5.setReferralCode("Referral Code");
        program5.setReward(ReferralReward.REFER_TIERED);
        program5.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program5.setUser(new User());

        User user5 = new User();
        user5.setAccountStatus(AccountStatus.SUSPENDED);
        user5.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setEmailAddress("42 Main St");
        user5.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setFirstName("Jane");
        user5.setId(UUID.randomUUID());
        user5.setLastName("Doe");
        user5.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setMfaEnabled(true);
        user5.setMfaFactor(mfaFactor6);
        user5.setPassword("iloveyou");
        user5.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setPasswordRecoveryToken("ABC123");
        user5.setProgram(program5);
        user5.setRecoveryCodeEnabled(true);
        user5.setRole(Role.USER);
        user5.setSessions(new ArrayList<>());
        user5.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program6 = new ReferralProgram();
        program6.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program6.setCredits(new BigDecimal("2.3"));
        program6.setId(UUID.randomUUID());
        program6.setMilestone(1);
        program6.setReferLink("Refer Link");
        program6.setReferralCode("Referral Code");
        program6.setReward(ReferralReward.REFER_TIERED);
        program6.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program6.setUser(user5);

        User user6 = new User();
        user6.setAccountStatus(AccountStatus.SUSPENDED);
        user6.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setEmailAddress("42 Main St");
        user6.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setFirstName("Jane");
        user6.setId(UUID.randomUUID());
        user6.setLastName("Doe");
        user6.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setMfaEnabled(true);
        user6.setMfaFactor(mfaFactor5);
        user6.setPassword("iloveyou");
        user6.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setPasswordRecoveryToken("ABC123");
        user6.setProgram(program6);
        user6.setRecoveryCodeEnabled(true);
        user6.setRole(Role.USER);
        user6.setSessions(new ArrayList<>());
        user6.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        // Act
        referralProgramImplementation.create(user6);

        // Assert
        verify(referralProgramRepository).save(Mockito.<ReferralProgram>any());
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#verify(String)}
     */
    @Test
    void testVerify() {
        // Arrange
        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram referralProgram = new ReferralProgram();
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);
        Optional<ReferralProgram> ofResult = Optional.of(referralProgram);
        when(referralProgramRepository.findByReferralCode(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        User actualVerifyResult = referralProgramImplementation.verify("Code");

        // Assert
        verify(referralProgramRepository).findByReferralCode(Mockito.<String>any());
        assertSame(user3, actualVerifyResult);
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#verify(String)}
     */
    @Test
    void testVerify2() {
        // Arrange
        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        User user4 = new User();
        user4.setAccountStatus(AccountStatus.SUSPENDED);
        user4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setEmailAddress("42 Main St");
        user4.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setFirstName("Jane");
        user4.setId(UUID.randomUUID());
        user4.setLastName("Doe");
        user4.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setMfaEnabled(true);
        user4.setMfaFactor(new MFAFactor());
        user4.setPassword("iloveyou");
        user4.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setPasswordRecoveryToken("ABC123");
        user4.setProgram(new ReferralProgram());
        user4.setRecoveryCodeEnabled(true);
        user4.setRole(Role.USER);
        user4.setSessions(new ArrayList<>());
        user4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor2 = new MFAFactor();
        mfaFactor2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor2.setId(UUID.randomUUID());
        mfaFactor2.setMfaChallenges(new ArrayList<>());
        mfaFactor2.setRecoveryCodes(new ArrayList<>());
        mfaFactor2.setSecret("Secret");
        mfaFactor2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor2.setUser(user4);

        User user5 = new User();
        user5.setAccountStatus(AccountStatus.SUSPENDED);
        user5.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setEmailAddress("42 Main St");
        user5.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setFirstName("Jane");
        user5.setId(UUID.randomUUID());
        user5.setLastName("Doe");
        user5.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setMfaEnabled(true);
        user5.setMfaFactor(new MFAFactor());
        user5.setPassword("iloveyou");
        user5.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setPasswordRecoveryToken("ABC123");
        user5.setProgram(new ReferralProgram());
        user5.setRecoveryCodeEnabled(true);
        user5.setRole(Role.USER);
        user5.setSessions(new ArrayList<>());
        user5.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program2 = new ReferralProgram();
        program2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program2.setCredits(new BigDecimal("2.3"));
        program2.setId(UUID.randomUUID());
        program2.setMilestone(1);
        program2.setReferLink("Refer Link");
        program2.setReferralCode("Referral Code");
        program2.setReward(ReferralReward.REFER_TIERED);
        program2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program2.setUser(user5);

        User user6 = new User();
        user6.setAccountStatus(AccountStatus.SUSPENDED);
        user6.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setEmailAddress("42 Main St");
        user6.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setFirstName("Jane");
        user6.setId(UUID.randomUUID());
        user6.setLastName("Doe");
        user6.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setMfaEnabled(true);
        user6.setMfaFactor(mfaFactor2);
        user6.setPassword("iloveyou");
        user6.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setPasswordRecoveryToken("ABC123");
        user6.setProgram(program2);
        user6.setRecoveryCodeEnabled(true);
        user6.setRole(Role.USER);
        user6.setSessions(new ArrayList<>());
        user6.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor3 = new MFAFactor();
        mfaFactor3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor3.setId(UUID.randomUUID());
        mfaFactor3.setMfaChallenges(new ArrayList<>());
        mfaFactor3.setRecoveryCodes(new ArrayList<>());
        mfaFactor3.setSecret("Secret");
        mfaFactor3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor3.setUser(user6);

        User user7 = new User();
        user7.setAccountStatus(AccountStatus.SUSPENDED);
        user7.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user7.setEmailAddress("42 Main St");
        user7.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user7.setFirstName("Jane");
        user7.setId(UUID.randomUUID());
        user7.setLastName("Doe");
        user7.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user7.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user7.setMfaEnabled(true);
        user7.setMfaFactor(new MFAFactor());
        user7.setPassword("iloveyou");
        user7.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user7.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user7.setPasswordRecoveryToken("ABC123");
        user7.setProgram(new ReferralProgram());
        user7.setRecoveryCodeEnabled(true);
        user7.setRole(Role.USER);
        user7.setSessions(new ArrayList<>());
        user7.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor4 = new MFAFactor();
        mfaFactor4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setId(UUID.randomUUID());
        mfaFactor4.setMfaChallenges(new ArrayList<>());
        mfaFactor4.setRecoveryCodes(new ArrayList<>());
        mfaFactor4.setSecret("Secret");
        mfaFactor4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setUser(user7);

        User user8 = new User();
        user8.setAccountStatus(AccountStatus.SUSPENDED);
        user8.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user8.setEmailAddress("42 Main St");
        user8.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user8.setFirstName("Jane");
        user8.setId(UUID.randomUUID());
        user8.setLastName("Doe");
        user8.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user8.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user8.setMfaEnabled(true);
        user8.setMfaFactor(new MFAFactor());
        user8.setPassword("iloveyou");
        user8.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user8.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user8.setPasswordRecoveryToken("ABC123");
        user8.setProgram(new ReferralProgram());
        user8.setRecoveryCodeEnabled(true);
        user8.setRole(Role.USER);
        user8.setSessions(new ArrayList<>());
        user8.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program3 = new ReferralProgram();
        program3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program3.setCredits(new BigDecimal("2.3"));
        program3.setId(UUID.randomUUID());
        program3.setMilestone(1);
        program3.setReferLink("Refer Link");
        program3.setReferralCode("Referral Code");
        program3.setReward(ReferralReward.REFER_TIERED);
        program3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program3.setUser(user8);

        User user9 = new User();
        user9.setAccountStatus(AccountStatus.SUSPENDED);
        user9.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setEmailAddress("42 Main St");
        user9.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setFirstName("Jane");
        user9.setId(UUID.randomUUID());
        user9.setLastName("Doe");
        user9.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setMfaEnabled(true);
        user9.setMfaFactor(mfaFactor4);
        user9.setPassword("iloveyou");
        user9.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setPasswordRecoveryToken("ABC123");
        user9.setProgram(program3);
        user9.setRecoveryCodeEnabled(true);
        user9.setRole(Role.USER);
        user9.setSessions(new ArrayList<>());
        user9.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program4 = new ReferralProgram();
        program4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program4.setCredits(new BigDecimal("2.3"));
        program4.setId(UUID.randomUUID());
        program4.setMilestone(1);
        program4.setReferLink("Refer Link");
        program4.setReferralCode("Referral Code");
        program4.setReward(ReferralReward.REFER_TIERED);
        program4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program4.setUser(user9);

        User user10 = new User();
        user10.setAccountStatus(AccountStatus.SUSPENDED);
        user10.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setEmailAddress("42 Main St");
        user10.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setFirstName("Jane");
        user10.setId(UUID.randomUUID());
        user10.setLastName("Doe");
        user10.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setMfaEnabled(true);
        user10.setMfaFactor(mfaFactor3);
        user10.setPassword("iloveyou");
        user10.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setPasswordRecoveryToken("ABC123");
        user10.setProgram(program4);
        user10.setRecoveryCodeEnabled(true);
        user10.setRole(Role.USER);
        user10.setSessions(new ArrayList<>());
        user10.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        ReferralProgram referralProgram = mock(ReferralProgram.class);
        when(referralProgram.getUser()).thenReturn(user10);
        doNothing().when(referralProgram).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(referralProgram).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(referralProgram).setId(Mockito.<UUID>any());
        doNothing().when(referralProgram).setCredits(Mockito.<BigDecimal>any());
        doNothing().when(referralProgram).setMilestone(Mockito.<Integer>any());
        doNothing().when(referralProgram).setReferLink(Mockito.<String>any());
        doNothing().when(referralProgram).setReferralCode(Mockito.<String>any());
        doNothing().when(referralProgram).setReward(Mockito.<ReferralReward>any());
        doNothing().when(referralProgram).setUser(Mockito.<User>any());
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);
        Optional<ReferralProgram> ofResult = Optional.of(referralProgram);
        when(referralProgramRepository.findByReferralCode(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        User actualVerifyResult = referralProgramImplementation.verify("Code");

        // Assert
        verify(referralProgram).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(referralProgram).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(referralProgram).setId(Mockito.<UUID>any());
        verify(referralProgram).getUser();
        verify(referralProgram).setCredits(Mockito.<BigDecimal>any());
        verify(referralProgram).setMilestone(Mockito.<Integer>any());
        verify(referralProgram).setReferLink(Mockito.<String>any());
        verify(referralProgram).setReferralCode(Mockito.<String>any());
        verify(referralProgram).setReward(Mockito.<ReferralReward>any());
        verify(referralProgram).setUser(Mockito.<User>any());
        verify(referralProgramRepository).findByReferralCode(Mockito.<String>any());
        assertSame(user10, actualVerifyResult);
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#verifyLink(String)}
     */
    @Test
    void testVerifyLink() {
        // Arrange
        when(referralService.getAvatar(Mockito.<User>any())).thenReturn("Avatar");

        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram referralProgram = new ReferralProgram();
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);
        Optional<ReferralProgram> ofResult = Optional.of(referralProgram);
        when(referralProgramRepository.findByReferLink(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        ApiResponse<ReferralProgramResponse> actualVerifyLinkResult = referralProgramImplementation.verifyLink("Link");

        // Assert
        verify(referralProgramRepository).findByReferLink(Mockito.<String>any());
        verify(referralService).getAvatar(Mockito.<User>any());
        ReferralProgramResponse data = actualVerifyLinkResult.getData();
        assertEquals("Avatar", data.getAvatar());
        assertEquals("Jane Doe", data.getName());
        ReferralProgramData data2 = data.getData();
        assertEquals("Refer Link", data2.getReferLink());
        assertEquals("Referral Code", data2.getReferralCode());
        assertEquals("Referral Link found", actualVerifyLinkResult.getMessage());
        assertEquals(
                "This referral program rewards you for your successful referrals in tiers. As you refer more friends"
                        + " to our platform, you'll unlock increasing rewards. For every total of 5, 10, 15, etc. referrals made"
                        + " by your referrals, you'll earn credits. The more active your network becomes, the higher your rewards"
                        + " will be, providing ongoing incentives to grow your referral network and earn more.",
                data2.getDescription());
        assertEquals("User", data.getRole());
        assertEquals(200, actualVerifyLinkResult.getCode().intValue());
        assertEquals(500, data2.getCredit().intValue());
        assertEquals(ReferralReward.REFER_TIERED, data2.getReward());
        assertEquals(HttpStatus.OK, actualVerifyLinkResult.getStatus());
        BigDecimal expectedCredits = new BigDecimal("2.3");
        assertEquals(expectedCredits, data2.getCredits());
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#verifyLink(String)}
     */
    @Test
    void testVerifyLink2() {
        // Arrange
        when(referralService.getAvatar(Mockito.<User>any())).thenThrow(new ReferralException("An error occurred"));

        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram referralProgram = new ReferralProgram();
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);
        Optional<ReferralProgram> ofResult = Optional.of(referralProgram);
        when(referralProgramRepository.findByReferLink(Mockito.<String>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(ReferralException.class, () -> referralProgramImplementation.verifyLink("Link"));
        verify(referralProgramRepository).findByReferLink(Mockito.<String>any());
        verify(referralService).getAvatar(Mockito.<User>any());
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#verifyCode(String)}
     */
    @Test
    void testVerifyCode() {
        // Arrange
        when(referralService.getAvatar(Mockito.<User>any())).thenReturn("Avatar");

        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram referralProgram = new ReferralProgram();
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);
        Optional<ReferralProgram> ofResult = Optional.of(referralProgram);
        when(referralProgramRepository.findByReferralCode(Mockito.<String>any())).thenReturn(ofResult);

        // Act
        ApiResponse<ReferralProgramResponse> actualVerifyCodeResult = referralProgramImplementation.verifyCode("Code");

        // Assert
        verify(referralProgramRepository).findByReferralCode(Mockito.<String>any());
        verify(referralService).getAvatar(Mockito.<User>any());
        ReferralProgramResponse data = actualVerifyCodeResult.getData();
        assertEquals("Avatar", data.getAvatar());
        assertEquals("Jane Doe", data.getName());
        ReferralProgramData data2 = data.getData();
        assertEquals("Refer Link", data2.getReferLink());
        assertEquals("Referral Code found", actualVerifyCodeResult.getMessage());
        assertEquals("Referral Code", data2.getReferralCode());
        assertEquals(
                "This referral program rewards you for your successful referrals in tiers. As you refer more friends"
                        + " to our platform, you'll unlock increasing rewards. For every total of 5, 10, 15, etc. referrals made"
                        + " by your referrals, you'll earn credits. The more active your network becomes, the higher your rewards"
                        + " will be, providing ongoing incentives to grow your referral network and earn more.",
                data2.getDescription());
        assertEquals("User", data.getRole());
        assertEquals(200, actualVerifyCodeResult.getCode().intValue());
        assertEquals(500, data2.getCredit().intValue());
        assertEquals(ReferralReward.REFER_TIERED, data2.getReward());
        assertEquals(HttpStatus.OK, actualVerifyCodeResult.getStatus());
        BigDecimal expectedCredits = new BigDecimal("2.3");
        assertEquals(expectedCredits, data2.getCredits());
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#verifyCode(String)}
     */
    @Test
    void testVerifyCode2() {
        // Arrange
        when(referralService.getAvatar(Mockito.<User>any())).thenThrow(new ReferralException("An error occurred"));

        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram referralProgram = new ReferralProgram();
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);
        Optional<ReferralProgram> ofResult = Optional.of(referralProgram);
        when(referralProgramRepository.findByReferralCode(Mockito.<String>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(ReferralException.class, () -> referralProgramImplementation.verifyCode("Code"));
        verify(referralProgramRepository).findByReferralCode(Mockito.<String>any());
        verify(referralService).getAvatar(Mockito.<User>any());
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#performChecks()}
     */
    @Test
    void testPerformChecks() {
        // Arrange
        when(referralProgramRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        referralProgramImplementation.performChecks();

        // Assert that nothing has changed
        verify(referralProgramRepository).findAll();
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#performChecks()}
     */
    @Test
    void testPerformChecks2() {
        // Arrange
        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram referralProgram = new ReferralProgram();
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);

        ArrayList<ReferralProgram> referralProgramList = new ArrayList<>();
        referralProgramList.add(referralProgram);
        when(referralProgramRepository.findAll()).thenReturn(referralProgramList);
        when(referralRepository.countReferralsOfReferralsByReferredBy(Mockito.<ReferralProgram>any())).thenReturn(3);

        // Act
        referralProgramImplementation.performChecks();

        // Assert that nothing has changed
        verify(referralRepository).countReferralsOfReferralsByReferredBy(Mockito.<ReferralProgram>any());
        verify(referralProgramRepository).findAll();
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#performChecks()}
     */
    @Test
    void testPerformChecks3() {
        // Arrange
        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        ReferralProgram referralProgram = mock(ReferralProgram.class);
        when(referralProgram.getMilestone()).thenReturn(1);
        when(referralProgram.getReward()).thenReturn(ReferralReward.REFER_TIERED);
        doNothing().when(referralProgram).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(referralProgram).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(referralProgram).setId(Mockito.<UUID>any());
        doNothing().when(referralProgram).setCredits(Mockito.<BigDecimal>any());
        doNothing().when(referralProgram).setMilestone(Mockito.<Integer>any());
        doNothing().when(referralProgram).setReferLink(Mockito.<String>any());
        doNothing().when(referralProgram).setReferralCode(Mockito.<String>any());
        doNothing().when(referralProgram).setReward(Mockito.<ReferralReward>any());
        doNothing().when(referralProgram).setUser(Mockito.<User>any());
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);

        ArrayList<ReferralProgram> referralProgramList = new ArrayList<>();
        referralProgramList.add(referralProgram);
        when(referralProgramRepository.findAll()).thenReturn(referralProgramList);
        when(referralRepository.countReferralsOfReferralsByReferredBy(Mockito.<ReferralProgram>any())).thenReturn(3);

        // Act
        referralProgramImplementation.performChecks();

        // Assert that nothing has changed
        verify(referralProgram).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(referralProgram).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(referralProgram).setId(Mockito.<UUID>any());
        verify(referralProgram).getMilestone();
        verify(referralProgram).getReward();
        verify(referralProgram).setCredits(Mockito.<BigDecimal>any());
        verify(referralProgram).setMilestone(Mockito.<Integer>any());
        verify(referralProgram).setReferLink(Mockito.<String>any());
        verify(referralProgram).setReferralCode(Mockito.<String>any());
        verify(referralProgram).setReward(Mockito.<ReferralReward>any());
        verify(referralProgram).setUser(Mockito.<User>any());
        verify(referralRepository).countReferralsOfReferralsByReferredBy(Mockito.<ReferralProgram>any());
        verify(referralProgramRepository).findAll();
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#performChecks()}
     */
    @Test
    void testPerformChecks4() {
        // Arrange
        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        ReferralProgram referralProgram = mock(ReferralProgram.class);
        when(referralProgram.getMilestone()).thenThrow(new ReferralException("An error occurred"));
        when(referralProgram.getReward()).thenReturn(ReferralReward.REFER_TIERED);
        doNothing().when(referralProgram).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(referralProgram).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(referralProgram).setId(Mockito.<UUID>any());
        doNothing().when(referralProgram).setCredits(Mockito.<BigDecimal>any());
        doNothing().when(referralProgram).setMilestone(Mockito.<Integer>any());
        doNothing().when(referralProgram).setReferLink(Mockito.<String>any());
        doNothing().when(referralProgram).setReferralCode(Mockito.<String>any());
        doNothing().when(referralProgram).setReward(Mockito.<ReferralReward>any());
        doNothing().when(referralProgram).setUser(Mockito.<User>any());
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);

        ArrayList<ReferralProgram> referralProgramList = new ArrayList<>();
        referralProgramList.add(referralProgram);
        when(referralProgramRepository.findAll()).thenReturn(referralProgramList);
        when(referralRepository.countReferralsOfReferralsByReferredBy(Mockito.<ReferralProgram>any())).thenReturn(3);

        // Act and Assert
        assertThrows(ReferralException.class, () -> referralProgramImplementation.performChecks());
        verify(referralProgram).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(referralProgram).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(referralProgram).setId(Mockito.<UUID>any());
        verify(referralProgram).getMilestone();
        verify(referralProgram).getReward();
        verify(referralProgram).setCredits(Mockito.<BigDecimal>any());
        verify(referralProgram).setMilestone(Mockito.<Integer>any());
        verify(referralProgram).setReferLink(Mockito.<String>any());
        verify(referralProgram).setReferralCode(Mockito.<String>any());
        verify(referralProgram).setReward(Mockito.<ReferralReward>any());
        verify(referralProgram).setUser(Mockito.<User>any());
        verify(referralRepository).countReferralsOfReferralsByReferredBy(Mockito.<ReferralProgram>any());
        verify(referralProgramRepository).findAll();
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#performChecks()}
     */
    @Test
    void testPerformChecks5() {
        // Arrange
        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        ReferralProgram referralProgram = mock(ReferralProgram.class);
        when(referralProgram.getCredits()).thenReturn(new BigDecimal("2.3"));
        when(referralProgram.getMilestone()).thenReturn(3);
        when(referralProgram.getReward()).thenReturn(ReferralReward.REFER_TIERED);
        doNothing().when(referralProgram).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(referralProgram).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(referralProgram).setId(Mockito.<UUID>any());
        doNothing().when(referralProgram).setCredits(Mockito.<BigDecimal>any());
        doNothing().when(referralProgram).setMilestone(Mockito.<Integer>any());
        doNothing().when(referralProgram).setReferLink(Mockito.<String>any());
        doNothing().when(referralProgram).setReferralCode(Mockito.<String>any());
        doNothing().when(referralProgram).setReward(Mockito.<ReferralReward>any());
        doNothing().when(referralProgram).setUser(Mockito.<User>any());
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);

        ArrayList<ReferralProgram> referralProgramList = new ArrayList<>();
        referralProgramList.add(referralProgram);

        MFAFactor mfaFactor2 = new MFAFactor();
        mfaFactor2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor2.setId(UUID.randomUUID());
        mfaFactor2.setMfaChallenges(new ArrayList<>());
        mfaFactor2.setRecoveryCodes(new ArrayList<>());
        mfaFactor2.setSecret("Secret");
        mfaFactor2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor2.setUser(new User());

        ReferralProgram program2 = new ReferralProgram();
        program2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program2.setId(UUID.randomUUID());
        program2.setMilestone(1);
        program2.setReferLink("Refer Link");
        program2.setReferralCode("Referral Code");
        program2.setReward(ReferralReward.REFER_TIERED);
        program2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program2.setUser(new User());

        User user4 = new User();
        user4.setAccountStatus(AccountStatus.SUSPENDED);
        user4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setEmailAddress("42 Main St");
        user4.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setFirstName("Jane");
        user4.setId(UUID.randomUUID());
        user4.setLastName("Doe");
        user4.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setMfaEnabled(true);
        user4.setMfaFactor(mfaFactor2);
        user4.setPassword("iloveyou");
        user4.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setPasswordRecoveryToken("ABC123");
        user4.setProgram(program2);
        user4.setRecoveryCodeEnabled(true);
        user4.setRole(Role.USER);
        user4.setSessions(new ArrayList<>());
        user4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor3 = new MFAFactor();
        mfaFactor3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor3.setId(UUID.randomUUID());
        mfaFactor3.setMfaChallenges(new ArrayList<>());
        mfaFactor3.setRecoveryCodes(new ArrayList<>());
        mfaFactor3.setSecret("Secret");
        mfaFactor3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor3.setUser(user4);

        MFAFactor mfaFactor4 = new MFAFactor();
        mfaFactor4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setId(UUID.randomUUID());
        mfaFactor4.setMfaChallenges(new ArrayList<>());
        mfaFactor4.setRecoveryCodes(new ArrayList<>());
        mfaFactor4.setSecret("Secret");
        mfaFactor4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setUser(new User());

        ReferralProgram program3 = new ReferralProgram();
        program3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program3.setId(UUID.randomUUID());
        program3.setMilestone(1);
        program3.setReferLink("Refer Link");
        program3.setReferralCode("Referral Code");
        program3.setReward(ReferralReward.REFER_TIERED);
        program3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program3.setUser(new User());

        User user5 = new User();
        user5.setAccountStatus(AccountStatus.SUSPENDED);
        user5.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setEmailAddress("42 Main St");
        user5.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setFirstName("Jane");
        user5.setId(UUID.randomUUID());
        user5.setLastName("Doe");
        user5.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setMfaEnabled(true);
        user5.setMfaFactor(mfaFactor4);
        user5.setPassword("iloveyou");
        user5.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user5.setPasswordRecoveryToken("ABC123");
        user5.setProgram(program3);
        user5.setRecoveryCodeEnabled(true);
        user5.setRole(Role.USER);
        user5.setSessions(new ArrayList<>());
        user5.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program4 = new ReferralProgram();
        program4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program4.setCredits(new BigDecimal("2.3"));
        program4.setId(UUID.randomUUID());
        program4.setMilestone(1);
        program4.setReferLink("Refer Link");
        program4.setReferralCode("Referral Code");
        program4.setReward(ReferralReward.REFER_TIERED);
        program4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program4.setUser(user5);

        User user6 = new User();
        user6.setAccountStatus(AccountStatus.SUSPENDED);
        user6.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setEmailAddress("42 Main St");
        user6.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setFirstName("Jane");
        user6.setId(UUID.randomUUID());
        user6.setLastName("Doe");
        user6.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setMfaEnabled(true);
        user6.setMfaFactor(mfaFactor3);
        user6.setPassword("iloveyou");
        user6.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setPasswordRecoveryToken("ABC123");
        user6.setProgram(program4);
        user6.setRecoveryCodeEnabled(true);
        user6.setRole(Role.USER);
        user6.setSessions(new ArrayList<>());
        user6.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram referralProgram2 = new ReferralProgram();
        referralProgram2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram2.setCredits(new BigDecimal("2.3"));
        referralProgram2.setId(UUID.randomUUID());
        referralProgram2.setMilestone(1);
        referralProgram2.setReferLink("Refer Link");
        referralProgram2.setReferralCode("Referral Code");
        referralProgram2.setReward(ReferralReward.REFER_TIERED);
        referralProgram2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram2.setUser(user6);
        when(referralProgramRepository.save(Mockito.<ReferralProgram>any())).thenReturn(referralProgram2);
        when(referralProgramRepository.findAll()).thenReturn(referralProgramList);
        when(referralRepository.countReferralsOfReferralsByReferredBy(Mockito.<ReferralProgram>any())).thenReturn(3);

        // Act
        referralProgramImplementation.performChecks();

        // Assert that nothing has changed
        verify(referralProgram).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(referralProgram, atLeast(1)).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(referralProgram).setId(Mockito.<UUID>any());
        verify(referralProgram).getCredits();
        verify(referralProgram, atLeast(1)).getMilestone();
        verify(referralProgram, atLeast(1)).getReward();
        verify(referralProgram, atLeast(1)).setCredits(Mockito.<BigDecimal>any());
        verify(referralProgram, atLeast(1)).setMilestone(Mockito.<Integer>any());
        verify(referralProgram).setReferLink(Mockito.<String>any());
        verify(referralProgram).setReferralCode(Mockito.<String>any());
        verify(referralProgram).setReward(Mockito.<ReferralReward>any());
        verify(referralProgram).setUser(Mockito.<User>any());
        verify(referralRepository).countReferralsOfReferralsByReferredBy(Mockito.<ReferralProgram>any());
        verify(referralProgramRepository).save(Mockito.<ReferralProgram>any());
        verify(referralProgramRepository).findAll();
    }

    /**
     * Method under test: {@link ReferralProgramImplementation#performChecks()}
     */
    @Test
    void testPerformChecks6() {
        // Arrange
        User user = new User();
        user.setAccountStatus(AccountStatus.SUSPENDED);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setEmailAddress("42 Main St");
        user.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setFirstName("Jane");
        user.setId(UUID.randomUUID());
        user.setLastName("Doe");
        user.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setMfaEnabled(true);
        user.setMfaFactor(new MFAFactor());
        user.setPassword("iloveyou");
        user.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user.setPasswordRecoveryToken("ABC123");
        user.setProgram(new ReferralProgram());
        user.setRecoveryCodeEnabled(true);
        user.setRole(Role.USER);
        user.setSessions(new ArrayList<>());
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user);

        User user2 = new User();
        user2.setAccountStatus(AccountStatus.SUSPENDED);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setEmailAddress("42 Main St");
        user2.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setFirstName("Jane");
        user2.setId(UUID.randomUUID());
        user2.setLastName("Doe");
        user2.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setMfaEnabled(true);
        user2.setMfaFactor(new MFAFactor());
        user2.setPassword("iloveyou");
        user2.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user2.setPasswordRecoveryToken("ABC123");
        user2.setProgram(new ReferralProgram());
        user2.setRecoveryCodeEnabled(true);
        user2.setRole(Role.USER);
        user2.setSessions(new ArrayList<>());
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user2);

        User user3 = new User();
        user3.setAccountStatus(AccountStatus.SUSPENDED);
        user3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setEmailAddress("42 Main St");
        user3.setEmailConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setFirstName("Jane");
        user3.setId(UUID.randomUUID());
        user3.setLastName("Doe");
        user3.setLastSignedIn(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setLastUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setMfaEnabled(true);
        user3.setMfaFactor(mfaFactor);
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(program);
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        ReferralProgram referralProgram = mock(ReferralProgram.class);
        when(referralProgram.getCredits()).thenReturn(new BigDecimal("2.3"));
        when(referralProgram.getMilestone()).thenReturn(3);
        when(referralProgram.getReward()).thenReturn(ReferralReward.REFER_TIERED);
        doNothing().when(referralProgram).setCreatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(referralProgram).setUpdatedAt(Mockito.<LocalDateTime>any());
        doNothing().when(referralProgram).setId(Mockito.<UUID>any());
        doNothing().when(referralProgram).setCredits(Mockito.<BigDecimal>any());
        doNothing().when(referralProgram).setMilestone(Mockito.<Integer>any());
        doNothing().when(referralProgram).setReferLink(Mockito.<String>any());
        doNothing().when(referralProgram).setReferralCode(Mockito.<String>any());
        doNothing().when(referralProgram).setReward(Mockito.<ReferralReward>any());
        doNothing().when(referralProgram).setUser(Mockito.<User>any());
        referralProgram.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setCredits(new BigDecimal("2.3"));
        referralProgram.setId(UUID.randomUUID());
        referralProgram.setMilestone(1);
        referralProgram.setReferLink("Refer Link");
        referralProgram.setReferralCode("Referral Code");
        referralProgram.setReward(ReferralReward.REFER_TIERED);
        referralProgram.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        referralProgram.setUser(user3);

        ArrayList<ReferralProgram> referralProgramList = new ArrayList<>();
        referralProgramList.add(referralProgram);
        when(referralProgramRepository.save(Mockito.<ReferralProgram>any()))
                .thenThrow(new ReferralException("An error occurred"));
        when(referralProgramRepository.findAll()).thenReturn(referralProgramList);
        when(referralRepository.countReferralsOfReferralsByReferredBy(Mockito.<ReferralProgram>any())).thenReturn(3);

        // Act and Assert
        assertThrows(ReferralException.class, () -> referralProgramImplementation.performChecks());
        verify(referralProgram).setCreatedAt(Mockito.<LocalDateTime>any());
        verify(referralProgram, atLeast(1)).setUpdatedAt(Mockito.<LocalDateTime>any());
        verify(referralProgram).setId(Mockito.<UUID>any());
        verify(referralProgram).getCredits();
        verify(referralProgram, atLeast(1)).getMilestone();
        verify(referralProgram, atLeast(1)).getReward();
        verify(referralProgram, atLeast(1)).setCredits(Mockito.<BigDecimal>any());
        verify(referralProgram, atLeast(1)).setMilestone(Mockito.<Integer>any());
        verify(referralProgram).setReferLink(Mockito.<String>any());
        verify(referralProgram).setReferralCode(Mockito.<String>any());
        verify(referralProgram).setReward(Mockito.<ReferralReward>any());
        verify(referralProgram).setUser(Mockito.<User>any());
        verify(referralRepository).countReferralsOfReferralsByReferredBy(Mockito.<ReferralProgram>any());
        verify(referralProgramRepository).save(Mockito.<ReferralProgram>any());
        verify(referralProgramRepository).findAll();
    }
}

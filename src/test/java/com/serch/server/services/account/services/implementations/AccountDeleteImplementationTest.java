package com.serch.server.services.account.services.implementations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.referral.ReferralReward;
import com.serch.server.exceptions.account.AccountException;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.mfa.MFAFactor;
import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.repositories.account.AccountDeleteRepository;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.utils.UserUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ContextConfiguration(classes = {AccountDeleteImplementation.class})
@ExtendWith(SpringExtension.class)
class AccountDeleteImplementationTest {
    @MockBean
    private AccountDeleteRepository accountDeleteRepository;

    @MockBean
    private ProfileRepository profileRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserUtil userUtil;

    @Autowired
    private AccountDeleteImplementation accountDeleteImplementation;

    /**
     * Method under test: {@link AccountDeleteImplementation#delete()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testDelete() {
        // TODO: Complete this test.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@3e3599db testClass = com.serch.server.services.account.services.implementations.DiffblueFakeClass1, locations = [], classes = [com.serch.server.ServerApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@2ddd4f07, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@4188f562, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@15eaf800, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@79e1d2cd, org.springframework.boot.test.context.SpringBootTestAnnotation@2a73b843], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
        //       at java.base/java.util.Optional.map(Optional.java:260)
        //   See https://diff.blue/R026 to resolve this issue.

        // Arrange and Act
        accountDeleteImplementation.delete();
    }

    /**
     * Method under test: {@link AccountDeleteImplementation#delete(UUID)}
     */
    @Test
    void testDelete2() {
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

        MFAFactor mfaFactor2 = new MFAFactor();
        mfaFactor2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor2.setId(UUID.randomUUID());
        mfaFactor2.setMfaChallenges(new ArrayList<>());
        mfaFactor2.setRecoveryCodes(new ArrayList<>());
        mfaFactor2.setSecret("Secret");
        mfaFactor2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor2.setUser(user3);

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

        MFAFactor mfaFactor3 = new MFAFactor();
        mfaFactor3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor3.setId(UUID.randomUUID());
        mfaFactor3.setMfaChallenges(new ArrayList<>());
        mfaFactor3.setRecoveryCodes(new ArrayList<>());
        mfaFactor3.setSecret("Secret");
        mfaFactor3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor3.setUser(user4);

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
        user6.setMfaFactor(mfaFactor3);
        user6.setPassword("iloveyou");
        user6.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user6.setPasswordRecoveryToken("ABC123");
        user6.setProgram(program2);
        user6.setRecoveryCodeEnabled(true);
        user6.setRole(Role.USER);
        user6.setSessions(new ArrayList<>());
        user6.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program3 = new ReferralProgram();
        program3.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program3.setCredits(new BigDecimal("2.3"));
        program3.setId(UUID.randomUUID());
        program3.setMilestone(1);
        program3.setReferLink("Refer Link");
        program3.setReferralCode("Referral Code");
        program3.setReward(ReferralReward.REFER_TIERED);
        program3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program3.setUser(user6);

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
        user7.setMfaFactor(mfaFactor2);
        user7.setPassword("iloveyou");
        user7.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user7.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user7.setPasswordRecoveryToken("ABC123");
        user7.setProgram(program3);
        user7.setRecoveryCodeEnabled(true);
        user7.setRole(Role.USER);
        user7.setSessions(new ArrayList<>());
        user7.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        when(userUtil.getUser()).thenReturn(user7);

        // Act and Assert
        assertThrows(AccountException.class, () -> accountDeleteImplementation.delete(UUID.randomUUID()));
        verify(userUtil).getUser();
    }
}

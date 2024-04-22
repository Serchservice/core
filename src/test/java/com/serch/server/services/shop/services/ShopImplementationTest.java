package com.serch.server.services.shop.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.referral.ReferralReward;
import com.serch.server.enums.shop.ShopStatus;
import com.serch.server.exceptions.others.ShopException;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.mfa.MFAFactor;
import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.models.shop.Shop;
import com.serch.server.models.shop.ShopService;
import com.serch.server.repositories.shop.ShopRepository;
import com.serch.server.repositories.shop.ShopServiceRepository;
import com.serch.server.services.shop.requests.AddShopServiceRequest;
import com.serch.server.services.shop.requests.CreateShopRequest;
import com.serch.server.services.shop.requests.RemoveShopServiceRequest;
import com.serch.server.services.shop.requests.UpdateShopRequest;
import com.serch.server.services.shop.responses.SearchShopResponse;
import com.serch.server.utils.UserUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ContextConfiguration(classes = {ShopImplementation.class})
@ExtendWith(SpringExtension.class)
class ShopImplementationTest {
    @Autowired
    private ShopImplementation shopImplementation;

    @MockBean
    private ShopRepository shopRepository;

    @MockBean
    private ShopServiceRepository shopServiceRepository;

    @MockBean
    private UserUtil userUtil;

    /**
     * Method under test: {@link ShopImplementation#createShop(CreateShopRequest)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testCreateShop() {
        // TODO: Complete this test.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@7ea94dc4 testClass = com.serch.server.services.shop.services.DiffblueFakeClass149, locations = [], classes = [com.serch.server.ServerApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@7e57b685, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@3d4fa0d0, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@c74e0a2, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@7e9ed765, org.springframework.boot.test.context.SpringBootTestAnnotation@ae803bcd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
        //       at java.base/java.util.Optional.map(Optional.java:260)
        //   See https://diff.blue/R026 to resolve this issue.

        // Arrange
        CreateShopRequest request = new CreateShopRequest();
        request.setAddress("42 Main St");
        request.setCategory(SerchCategory.MECHANIC);
        request.setLatitude(10.0d);
        request.setLongitude(10.0d);
        request.setName("Name");
        request.setPhoneNumber("6625550144");
        request.setPlace("Place");
        request.setServices(new ArrayList<>());

        // Act
        shopImplementation.createShop(request);
    }

    /**
     * Method under test: {@link ShopImplementation#updateShop(UpdateShopRequest)}
     */
    @Test
    void testUpdateShop() {
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

        MFAFactor mfaFactor4 = new MFAFactor();
        mfaFactor4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setId(UUID.randomUUID());
        mfaFactor4.setMfaChallenges(new ArrayList<>());
        mfaFactor4.setRecoveryCodes(new ArrayList<>());
        mfaFactor4.setSecret("Secret");
        mfaFactor4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setUser(user8);

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
        user9.setMfaFactor(new MFAFactor());
        user9.setPassword("iloveyou");
        user9.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setPasswordRecoveryToken("ABC123");
        user9.setProgram(new ReferralProgram());
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
        user10.setMfaFactor(mfaFactor4);
        user10.setPassword("iloveyou");
        user10.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setPasswordRecoveryToken("ABC123");
        user10.setProgram(program4);
        user10.setRecoveryCodeEnabled(true);
        user10.setRole(Role.USER);
        user10.setSessions(new ArrayList<>());
        user10.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setCategory(SerchCategory.MECHANIC);
        shop.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setId("42");
        shop.setLatitude(10.0d);
        shop.setLongitude(10.0d);
        shop.setName("Name");
        shop.setPhoneNumber("6625550144");
        shop.setPlace("Place");
        shop.setRating(10.0d);
        shop.setServices(new ArrayList<>());
        shop.setStatus(ShopStatus.OPEN);
        shop.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setUser(user10);
        Optional<Shop> ofResult = Optional.of(shop);
        when(shopRepository.findById(Mockito.<String>any())).thenReturn(ofResult);

        UpdateShopRequest request = new UpdateShopRequest();
        request.setAddress("42 Main St");
        request.setCategory(SerchCategory.MECHANIC);
        request.setLatitude(10.0d);
        request.setLongitude(10.0d);
        request.setName("Name");
        request.setPhoneNumber("6625550144");
        request.setPlace("Place");
        request.setShopId("42");

        // Act and Assert
        assertThrows(ShopException.class, () -> shopImplementation.updateShop(request));
        verify(userUtil).getUser();
        verify(shopRepository).findById(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ShopImplementation#updateShop(UpdateShopRequest)}
     */
    @Test
    void testUpdateShop2() {
        // Arrange
        when(userUtil.getUser()).thenThrow(new ShopException("An error occurred"));

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

        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setCategory(SerchCategory.MECHANIC);
        shop.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setId("42");
        shop.setLatitude(10.0d);
        shop.setLongitude(10.0d);
        shop.setName("Name");
        shop.setPhoneNumber("6625550144");
        shop.setPlace("Place");
        shop.setRating(10.0d);
        shop.setServices(new ArrayList<>());
        shop.setStatus(ShopStatus.OPEN);
        shop.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setUser(user3);
        Optional<Shop> ofResult = Optional.of(shop);
        when(shopRepository.findById(Mockito.<String>any())).thenReturn(ofResult);

        UpdateShopRequest request = new UpdateShopRequest();
        request.setAddress("42 Main St");
        request.setCategory(SerchCategory.MECHANIC);
        request.setLatitude(10.0d);
        request.setLongitude(10.0d);
        request.setName("Name");
        request.setPhoneNumber("6625550144");
        request.setPlace("Place");
        request.setShopId("42");

        // Act and Assert
        assertThrows(ShopException.class, () -> shopImplementation.updateShop(request));
        verify(userUtil).getUser();
        verify(shopRepository).findById(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ShopImplementation#fetchShops()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testFetchShops() {
        // TODO: Complete this test.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@1ecb95a testClass = com.serch.server.services.shop.services.DiffblueFakeClass693, locations = [], classes = [com.serch.server.ServerApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@7e57b685, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@3d4fa0d0, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@c74e0a2, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@7e9ed765, org.springframework.boot.test.context.SpringBootTestAnnotation@ae803bcd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
        //       at java.base/java.util.Optional.map(Optional.java:260)
        //   See https://diff.blue/R026 to resolve this issue.

        // Arrange and Act
        shopImplementation.fetchShops();
    }

    /**
     * Method under test:
     * {@link ShopImplementation#removeService(RemoveShopServiceRequest)}
     */
    @Test
    void testRemoveService() {
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
        user8.setMfaFactor(mfaFactor4);
        user8.setPassword("iloveyou");
        user8.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user8.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user8.setPasswordRecoveryToken("ABC123");
        user8.setProgram(program4);
        user8.setRecoveryCodeEnabled(true);
        user8.setRole(Role.USER);
        user8.setSessions(new ArrayList<>());
        user8.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setCategory(SerchCategory.MECHANIC);
        shop.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setId("42");
        shop.setLatitude(10.0d);
        shop.setLongitude(10.0d);
        shop.setName("Name");
        shop.setPhoneNumber("6625550144");
        shop.setPlace("Place");
        shop.setRating(10.0d);
        shop.setServices(new ArrayList<>());
        shop.setStatus(ShopStatus.OPEN);
        shop.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setUser(user8);

        ShopService shopService = new ShopService();
        shopService.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shopService.setId(1L);
        shopService.setService("Service");
        shopService.setShop(shop);
        shopService.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<ShopService> ofResult = Optional.of(shopService);
        when(shopServiceRepository.findByIdAndShop_Id(Mockito.<Long>any(), Mockito.<String>any())).thenReturn(ofResult);

        RemoveShopServiceRequest request = new RemoveShopServiceRequest();
        request.setId(1L);
        request.setShop("Shop");

        // Act and Assert
        assertThrows(ShopException.class, () -> shopImplementation.removeService(request));
        verify(shopServiceRepository).findByIdAndShop_Id(Mockito.<Long>any(), Mockito.<String>any());
        verify(userUtil).getUser();
    }

    /**
     * Method under test:
     * {@link ShopImplementation#removeService(RemoveShopServiceRequest)}
     */
    @Test
    void testRemoveService2() {
        // Arrange
        when(userUtil.getUser()).thenThrow(new ShopException("An error occurred"));

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

        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setCategory(SerchCategory.MECHANIC);
        shop.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setId("42");
        shop.setLatitude(10.0d);
        shop.setLongitude(10.0d);
        shop.setName("Name");
        shop.setPhoneNumber("6625550144");
        shop.setPlace("Place");
        shop.setRating(10.0d);
        shop.setServices(new ArrayList<>());
        shop.setStatus(ShopStatus.OPEN);
        shop.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setUser(user);

        ShopService shopService = new ShopService();
        shopService.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shopService.setId(1L);
        shopService.setService("Service");
        shopService.setShop(shop);
        shopService.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<ShopService> ofResult = Optional.of(shopService);
        when(shopServiceRepository.findByIdAndShop_Id(Mockito.<Long>any(), Mockito.<String>any())).thenReturn(ofResult);

        RemoveShopServiceRequest request = new RemoveShopServiceRequest();
        request.setId(1L);
        request.setShop("Shop");

        // Act and Assert
        assertThrows(ShopException.class, () -> shopImplementation.removeService(request));
        verify(shopServiceRepository).findByIdAndShop_Id(Mockito.<Long>any(), Mockito.<String>any());
        verify(userUtil).getUser();
    }

    /**
     * Method under test:
     * {@link ShopImplementation#addService(AddShopServiceRequest)}
     */
    @Test
    void testAddService() {
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

        MFAFactor mfaFactor4 = new MFAFactor();
        mfaFactor4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setId(UUID.randomUUID());
        mfaFactor4.setMfaChallenges(new ArrayList<>());
        mfaFactor4.setRecoveryCodes(new ArrayList<>());
        mfaFactor4.setSecret("Secret");
        mfaFactor4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setUser(user8);

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
        user9.setMfaFactor(new MFAFactor());
        user9.setPassword("iloveyou");
        user9.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setPasswordRecoveryToken("ABC123");
        user9.setProgram(new ReferralProgram());
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
        user10.setMfaFactor(mfaFactor4);
        user10.setPassword("iloveyou");
        user10.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setPasswordRecoveryToken("ABC123");
        user10.setProgram(program4);
        user10.setRecoveryCodeEnabled(true);
        user10.setRole(Role.USER);
        user10.setSessions(new ArrayList<>());
        user10.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setCategory(SerchCategory.MECHANIC);
        shop.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setId("42");
        shop.setLatitude(10.0d);
        shop.setLongitude(10.0d);
        shop.setName("Name");
        shop.setPhoneNumber("6625550144");
        shop.setPlace("Place");
        shop.setRating(10.0d);
        shop.setServices(new ArrayList<>());
        shop.setStatus(ShopStatus.OPEN);
        shop.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setUser(user10);
        Optional<Shop> ofResult = Optional.of(shop);
        when(shopRepository.findById(Mockito.<String>any())).thenReturn(ofResult);

        AddShopServiceRequest request = new AddShopServiceRequest();
        request.setService("Service");
        request.setShop("Shop");

        // Act and Assert
        assertThrows(ShopException.class, () -> shopImplementation.addService(request));
        verify(userUtil).getUser();
        verify(shopRepository).findById(Mockito.<String>any());
    }

    /**
     * Method under test:
     * {@link ShopImplementation#addService(AddShopServiceRequest)}
     */
    @Test
    void testAddService2() {
        // Arrange
        when(userUtil.getUser()).thenThrow(new ShopException("An error occurred"));

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

        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setCategory(SerchCategory.MECHANIC);
        shop.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setId("42");
        shop.setLatitude(10.0d);
        shop.setLongitude(10.0d);
        shop.setName("Name");
        shop.setPhoneNumber("6625550144");
        shop.setPlace("Place");
        shop.setRating(10.0d);
        shop.setServices(new ArrayList<>());
        shop.setStatus(ShopStatus.OPEN);
        shop.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setUser(user3);
        Optional<Shop> ofResult = Optional.of(shop);
        when(shopRepository.findById(Mockito.<String>any())).thenReturn(ofResult);

        AddShopServiceRequest request = new AddShopServiceRequest();
        request.setService("Service");
        request.setShop("Shop");

        // Act and Assert
        assertThrows(ShopException.class, () -> shopImplementation.addService(request));
        verify(userUtil).getUser();
        verify(shopRepository).findById(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ShopImplementation#changeStatus(String)}
     */
    @Test
    void testChangeStatus() {
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

        MFAFactor mfaFactor4 = new MFAFactor();
        mfaFactor4.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setId(UUID.randomUUID());
        mfaFactor4.setMfaChallenges(new ArrayList<>());
        mfaFactor4.setRecoveryCodes(new ArrayList<>());
        mfaFactor4.setSecret("Secret");
        mfaFactor4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor4.setUser(user8);

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
        user9.setMfaFactor(new MFAFactor());
        user9.setPassword("iloveyou");
        user9.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user9.setPasswordRecoveryToken("ABC123");
        user9.setProgram(new ReferralProgram());
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
        user10.setMfaFactor(mfaFactor4);
        user10.setPassword("iloveyou");
        user10.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user10.setPasswordRecoveryToken("ABC123");
        user10.setProgram(program4);
        user10.setRecoveryCodeEnabled(true);
        user10.setRole(Role.USER);
        user10.setSessions(new ArrayList<>());
        user10.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setCategory(SerchCategory.MECHANIC);
        shop.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setId("42");
        shop.setLatitude(10.0d);
        shop.setLongitude(10.0d);
        shop.setName("Name");
        shop.setPhoneNumber("6625550144");
        shop.setPlace("Place");
        shop.setRating(10.0d);
        shop.setServices(new ArrayList<>());
        shop.setStatus(ShopStatus.OPEN);
        shop.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setUser(user10);
        Optional<Shop> ofResult = Optional.of(shop);
        when(shopRepository.findById(Mockito.<String>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(ShopException.class, () -> shopImplementation.changeStatus("42"));
        verify(userUtil).getUser();
        verify(shopRepository).findById(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ShopImplementation#changeStatus(String)}
     */
    @Test
    void testChangeStatus2() {
        // Arrange
        when(userUtil.getUser()).thenThrow(new ShopException("An error occurred"));

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

        Shop shop = new Shop();
        shop.setAddress("42 Main St");
        shop.setCategory(SerchCategory.MECHANIC);
        shop.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setId("42");
        shop.setLatitude(10.0d);
        shop.setLongitude(10.0d);
        shop.setName("Name");
        shop.setPhoneNumber("6625550144");
        shop.setPlace("Place");
        shop.setRating(10.0d);
        shop.setServices(new ArrayList<>());
        shop.setStatus(ShopStatus.OPEN);
        shop.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        shop.setUser(user3);
        Optional<Shop> ofResult = Optional.of(shop);
        when(shopRepository.findById(Mockito.<String>any())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(ShopException.class, () -> shopImplementation.changeStatus("42"));
        verify(userUtil).getUser();
        verify(shopRepository).findById(Mockito.<String>any());
    }

    /**
     * Method under test: {@link ShopImplementation#markAllOpen()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testMarkAllOpen() {
        // TODO: Complete this test.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@71ef5fa4 testClass = com.serch.server.services.shop.services.DiffblueFakeClass703, locations = [], classes = [com.serch.server.ServerApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@7e57b685, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@3d4fa0d0, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@c74e0a2, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@7e9ed765, org.springframework.boot.test.context.SpringBootTestAnnotation@ae803bcd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
        //       at java.base/java.util.Optional.map(Optional.java:260)
        //   See https://diff.blue/R026 to resolve this issue.

        // Arrange and Act
        shopImplementation.markAllOpen();
    }

    /**
     * Method under test: {@link ShopImplementation#markAllClosed()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testMarkAllClosed() {
        // TODO: Complete this test.
        //   Reason: R026 Failed to create Spring context.
        //   Attempt to initialize test context failed with
        //   java.lang.IllegalStateException: ApplicationContext failure threshold (1) exceeded: skipping repeated attempt to load context for [WebMergedContextConfiguration@5d4ec5b1 testClass = com.serch.server.services.shop.services.DiffblueFakeClass698, locations = [], classes = [com.serch.server.ServerApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@7e57b685, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@3d4fa0d0, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@c74e0a2, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@7e9ed765, org.springframework.boot.test.context.SpringBootTestAnnotation@ae803bcd], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
        //       at org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate.loadContext(DefaultCacheAwareContextLoaderDelegate.java:145)
        //       at org.springframework.test.context.support.DefaultTestContext.getApplicationContext(DefaultTestContext.java:130)
        //       at java.base/java.util.Optional.map(Optional.java:260)
        //   See https://diff.blue/R026 to resolve this issue.

        // Arrange and Act
        shopImplementation.markAllClosed();
    }

    /**
     * Method under test:
     * {@link ShopImplementation#drive(String, String, Double, Double, Double)}
     */
    @Test
    void testDrive() {
        // Arrange
        when(shopRepository.findByServiceAndLocation(Mockito.<Double>any(), Mockito.<Double>any(), Mockito.<String>any(),
                Mockito.<Double>any(), Mockito.<String>any())).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<SearchShopResponse>> actualDriveResult = shopImplementation.drive("Query", "Category", 10.0d,
                10.0d, 10.0d);

        // Assert
        verify(shopRepository).findByServiceAndLocation(Mockito.<Double>any(), Mockito.<Double>any(), Mockito.<String>any(),
                Mockito.<Double>any(), Mockito.<String>any());
        assertEquals("Successful", actualDriveResult.getMessage());
        assertEquals(200, actualDriveResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualDriveResult.getStatus());
        assertTrue(actualDriveResult.getData().isEmpty());
    }

    /**
     * Method under test:
     * {@link ShopImplementation#drive(String, String, Double, Double, Double)}
     */
    @Test
    void testDrive2() {
        // Arrange
        when(shopRepository.findByCategoryAndLocation(Mockito.<Double>any(), Mockito.<Double>any(), Mockito.<String>any(),
                Mockito.<Double>any(), Mockito.<String>any())).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<SearchShopResponse>> actualDriveResult = shopImplementation.drive(null, "Category", 10.0d, 10.0d,
                10.0d);

        // Assert
        verify(shopRepository).findByCategoryAndLocation(Mockito.<Double>any(), Mockito.<Double>any(),
                Mockito.<String>any(), Mockito.<Double>any(), Mockito.<String>any());
        assertEquals("Successful", actualDriveResult.getMessage());
        assertEquals(200, actualDriveResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualDriveResult.getStatus());
        assertTrue(actualDriveResult.getData().isEmpty());
    }

    /**
     * Method under test:
     * {@link ShopImplementation#drive(String, String, Double, Double, Double)}
     */
    @Test
    void testDrive3() {
        // Arrange
        when(shopRepository.findByCategoryAndLocation(Mockito.<Double>any(), Mockito.<Double>any(), Mockito.<String>any(),
                Mockito.<Double>any(), Mockito.<String>any())).thenReturn(new ArrayList<>());

        // Act
        ApiResponse<List<SearchShopResponse>> actualDriveResult = shopImplementation.drive("", "Category", 10.0d, 10.0d,
                10.0d);

        // Assert
        verify(shopRepository).findByCategoryAndLocation(Mockito.<Double>any(), Mockito.<Double>any(),
                Mockito.<String>any(), Mockito.<Double>any(), Mockito.<String>any());
        assertEquals("Successful", actualDriveResult.getMessage());
        assertEquals(200, actualDriveResult.getCode().intValue());
        assertEquals(HttpStatus.OK, actualDriveResult.getStatus());
        assertTrue(actualDriveResult.getData().isEmpty());
    }

    /**
     * Method under test:
     * {@link ShopImplementation#drive(String, String, Double, Double, Double)}
     */
    @Test
    void testDrive4() {
        // Arrange
        when(shopRepository.findByCategoryAndLocation(Mockito.<Double>any(), Mockito.<Double>any(), Mockito.<String>any(),
                Mockito.<Double>any(), Mockito.<String>any())).thenThrow(new ShopException("An error occurred"));

        // Act and Assert
        assertThrows(ShopException.class, () -> shopImplementation.drive(null, "Category", 10.0d, 10.0d, 10.0d));
        verify(shopRepository).findByCategoryAndLocation(Mockito.<Double>any(), Mockito.<Double>any(),
                Mockito.<String>any(), Mockito.<Double>any(), Mockito.<String>any());
    }
}

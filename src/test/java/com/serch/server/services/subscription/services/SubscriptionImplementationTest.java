package com.serch.server.services.subscription.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.serch.server.enums.account.AccountStatus;
import com.serch.server.enums.auth.Role;
import com.serch.server.enums.referral.ReferralReward;
import com.serch.server.enums.subscription.PlanStatus;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.enums.subscription.SubPlanType;
import com.serch.server.models.auth.User;
import com.serch.server.models.auth.mfa.MFAFactor;
import com.serch.server.models.referral.ReferralProgram;
import com.serch.server.models.subscription.PlanChild;
import com.serch.server.models.subscription.PlanParent;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.models.subscription.SubscriptionAuth;
import com.serch.server.repositories.auth.UserRepository;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.utils.UserUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {SubscriptionImplementation.class})
@ExtendWith(SpringExtension.class)
class SubscriptionImplementationTest {
    @MockBean
    private InitSubscriptionService initSubscriptionService;

    @MockBean
    private PlanService planService;

    @Autowired
    private SubscriptionImplementation subscriptionImplementation;

    @MockBean
    private SubscriptionRepository subscriptionRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserUtil userUtil;

    @MockBean
    private VerifySubscriptionService verifySubscriptionService;

    /**
     * Method under test:
     * {@link SubscriptionImplementation#getAmountFromUserActivePlan(Subscription)}
     */
    @Test
    void testGetAmountFromUserActivePlan() {
        // Arrange
        SubscriptionAuth auth = new SubscriptionAuth();
        auth.setAccountName("Dr Jane Doe");
        auth.setBank("Bank");
        auth.setBin("Bin");
        auth.setCardType("Card Type");
        auth.setChannel("Channel");
        auth.setCode("Code");
        auth.setCountryCode("GB");
        auth.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        auth.setEmailAddress("42 Main St");
        auth.setExpMonth("Exp Month");
        auth.setExpYear("Exp Year");
        auth.setId(UUID.randomUUID());
        auth.setLast4("Last4");
        auth.setReusable(true);
        auth.setSignature("Signature");
        auth.setSubscription(new Subscription());
        auth.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanChild child = new PlanChild();
        child.setAmount("10");
        child.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        child.setDiscount("3");
        child.setId("42");
        child.setIsBusiness(true);
        child.setName("Name");
        child.setParent(new PlanParent());
        child.setTag("Tag");
        child.setType(SubPlanType.DAILY);
        child.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanParent plan = new PlanParent();
        plan.setAmount("10");
        plan.setBenefits(new ArrayList<>());
        plan.setChildren(new ArrayList<>());
        plan.setColor("Color");
        plan.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        plan.setDescription("The characteristics of someone or something");
        plan.setDuration("Duration");
        plan.setId("42");
        plan.setImage("Image");
        plan.setType(PlanType.FREE);
        plan.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

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

        Subscription subscription = new Subscription();
        subscription.setAuth(auth);
        subscription.setChild(child);
        subscription.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        subscription.setFreePlanStatus(PlanStatus.ACTIVE);
        subscription.setId("42");
        subscription.setInvoices(new ArrayList<>());
        subscription.setPlan(plan);
        subscription.setPlanStatus(PlanStatus.ACTIVE);
        subscription.setRetries(1);
        subscription.setSubscribedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        subscription.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        subscription.setUser(user);

        SubscriptionAuth auth2 = new SubscriptionAuth();
        auth2.setAccountName("Dr Jane Doe");
        auth2.setBank("Bank");
        auth2.setBin("Bin");
        auth2.setCardType("Card Type");
        auth2.setChannel("Channel");
        auth2.setCode("Code");
        auth2.setCountryCode("GB");
        auth2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        auth2.setEmailAddress("42 Main St");
        auth2.setExpMonth("Exp Month");
        auth2.setExpYear("Exp Year");
        auth2.setId(UUID.randomUUID());
        auth2.setLast4("Last4");
        auth2.setReusable(true);
        auth2.setSignature("Signature");
        auth2.setSubscription(subscription);
        auth2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanParent parent = new PlanParent();
        parent.setAmount("10");
        parent.setBenefits(new ArrayList<>());
        parent.setChildren(new ArrayList<>());
        parent.setColor("Color");
        parent.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        parent.setDescription("The characteristics of someone or something");
        parent.setDuration("Duration");
        parent.setId("42");
        parent.setImage("Image");
        parent.setType(PlanType.FREE);
        parent.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanChild child2 = new PlanChild();
        child2.setAmount("10");
        child2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        child2.setDiscount("3");
        child2.setId("42");
        child2.setIsBusiness(true);
        child2.setName("Name");
        child2.setParent(parent);
        child2.setTag("Tag");
        child2.setType(SubPlanType.DAILY);
        child2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        PlanParent plan2 = new PlanParent();
        plan2.setAmount("10");
        plan2.setBenefits(new ArrayList<>());
        plan2.setChildren(new ArrayList<>());
        plan2.setColor("Color");
        plan2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        plan2.setDescription("The characteristics of someone or something");
        plan2.setDuration("Duration");
        plan2.setId("42");
        plan2.setImage("Image");
        plan2.setType(PlanType.FREE);
        plan2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

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

        MFAFactor mfaFactor = new MFAFactor();
        mfaFactor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setId(UUID.randomUUID());
        mfaFactor.setMfaChallenges(new ArrayList<>());
        mfaFactor.setRecoveryCodes(new ArrayList<>());
        mfaFactor.setSecret("Secret");
        mfaFactor.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        mfaFactor.setUser(user2);

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
        user3.setMfaFactor(new MFAFactor());
        user3.setPassword("iloveyou");
        user3.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user3.setPasswordRecoveryToken("ABC123");
        user3.setProgram(new ReferralProgram());
        user3.setRecoveryCodeEnabled(true);
        user3.setRole(Role.USER);
        user3.setSessions(new ArrayList<>());
        user3.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        ReferralProgram program = new ReferralProgram();
        program.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setCredits(new BigDecimal("2.3"));
        program.setId(UUID.randomUUID());
        program.setMilestone(1);
        program.setReferLink("Refer Link");
        program.setReferralCode("Referral Code");
        program.setReward(ReferralReward.REFER_TIERED);
        program.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        program.setUser(user3);

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
        user4.setMfaFactor(mfaFactor);
        user4.setPassword("iloveyou");
        user4.setPasswordRecoveryConfirmedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setPasswordRecoveryExpiresAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        user4.setPasswordRecoveryToken("ABC123");
        user4.setProgram(program);
        user4.setRecoveryCodeEnabled(true);
        user4.setRole(Role.USER);
        user4.setSessions(new ArrayList<>());
        user4.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        Subscription subscription2 = new Subscription();
        subscription2.setAuth(auth2);
        subscription2.setChild(child2);
        subscription2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        subscription2.setFreePlanStatus(PlanStatus.ACTIVE);
        subscription2.setId("42");
        subscription2.setInvoices(new ArrayList<>());
        subscription2.setPlan(plan2);
        subscription2.setPlanStatus(PlanStatus.ACTIVE);
        subscription2.setRetries(1);
        subscription2.setSubscribedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        subscription2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        subscription2.setUser(user4);

        // Act and Assert
        assertEquals("10", subscriptionImplementation.getAmountFromUserActivePlan(subscription2));
    }
}

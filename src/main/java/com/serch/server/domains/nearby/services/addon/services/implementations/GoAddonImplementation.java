package com.serch.server.domains.nearby.services.addon.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.core.payment.requests.InitializePaymentRequest;
import com.serch.server.core.payment.requests.PaymentChargeRequest;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.core.payment.responses.PaymentAuthorization;
import com.serch.server.core.payment.responses.PaymentVerificationData;
import com.serch.server.core.payment.services.PaymentService;
import com.serch.server.domains.nearby.mappers.GoMapper;
import com.serch.server.domains.nearby.models.go.GoPaymentAuthorization;
import com.serch.server.domains.nearby.models.go.addon.GoAddon;
import com.serch.server.domains.nearby.models.go.addon.GoAddonPlan;
import com.serch.server.domains.nearby.models.go.addon.GoAddonTransaction;
import com.serch.server.domains.nearby.models.go.user.GoUser;
import com.serch.server.domains.nearby.models.go.user.GoUserAddon;
import com.serch.server.domains.nearby.models.go.user.GoUserAddonChange;
import com.serch.server.domains.nearby.repositories.go.*;
import com.serch.server.domains.nearby.services.addon.responses.GoAddonResponse;
import com.serch.server.domains.nearby.services.addon.responses.GoAddonVerificationResponse;
import com.serch.server.domains.nearby.services.addon.responses.GoUserAddonResponse;
import com.serch.server.domains.nearby.services.addon.services.GoAddonService;
import com.serch.server.enums.nearby.GoAddonPlanInterval;
import com.serch.server.enums.nearby.GoUserAddonStatus;
import com.serch.server.enums.transaction.TransactionStatus;
import com.serch.server.exceptions.others.SerchException;
import com.serch.server.exceptions.transaction.WalletException;
import com.serch.server.utils.AuthUtil;
import com.serch.server.utils.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.serch.server.enums.transaction.TransactionStatus.FAILED;
import static com.serch.server.enums.transaction.TransactionStatus.SUCCESSFUL;

@Service
@RequiredArgsConstructor
public class GoAddonImplementation implements GoAddonService {
    private final PaymentService pay;
    private final GoAddonRepository goAddonRepository;
    private final AuthUtil authUtil;
    private final GoAddonPlanRepository goAddonPlanRepository;
    private final GoAddonTransactionRepository goAddonTransactionRepository;
    private final GoUserRepository goUserRepository;
    private final GoUserAddonRepository goUserAddonRepository;
    private final GoUserAddonChangeRepository goUserAddonChangeRepository;
    private final GoPaymentAuthorizationRepository goPaymentAuthorizationRepository;

    @Override
    public ApiResponse<List<GoAddonResponse>> get() {
        List<GoAddonPlan> plans = goUserAddonRepository.findSubscribed(authUtil.getGoUser().getId())
                .stream().map(GoUserAddon::getPlan).toList();
        List<GoAddon> addons = goAddonRepository.findAll();

        if(addons.isEmpty()) {
            return new ApiResponse<>(new ArrayList<>());
        } else {
            List<GoAddonResponse> filteredAddons = addons.stream()
                    .filter(addon -> addon.getPlans().stream().noneMatch(plans::contains))
                    .map(addon -> addon(addon, null))
                    .filter(response -> !response.getPlans().isEmpty())
                    .toList();

            return new ApiResponse<>(filteredAddons);
        }
    }

    private GoAddonResponse addon(GoAddon addon, GoAddonPlan excluding) {
        GoAddonResponse response = GoMapper.instance.addon(addon);

        if(addon.getPlans().isEmpty()) {
            response.setPlans(new ArrayList<>());
        } else {
            if(excluding == null) {
                response.setPlans(addon.getPlans().stream().map(GoMapper.instance::plan).toList());
            } else {
                response.setPlans(addon.getPlans().stream()
                        .filter(plan -> !excluding.equals(plan))
                        .map(GoMapper.instance::plan).toList());
            }
        }

        return response;
    }

    @Override
    public ApiResponse<InitializePaymentData> init(String id) {
        GoUser user = authUtil.getGoUser();
        GoAddonPlan plan = goAddonPlanRepository.findById(id).orElseThrow(() -> new SerchException("Plan not found"));
        Optional<GoUserAddon> addon = goUserAddonRepository.findExisting(plan.getAddon().getId(), user.getId());

        if(addon.isPresent() && addon.get().isActive()) {
            if(addon.get().hasMultiple()) {
                throw new SerchException(String.format("You already have an active subscription for %s addon. You can, however, switch your current plan to a new one.", plan.getAddon().getName()));
            } else {
                throw new SerchException(String.format("You already have an active subscription for %s addon.", plan.getAddon().getName()));
            }
        } else {
            InitializePaymentData data = pay.initialize(request(user, plan));
            save(user, plan, data);

            return new ApiResponse<>(data);
        }
    }

    private InitializePaymentRequest request(GoUser user, GoAddonPlan plan) {
        InitializePaymentRequest payment = new InitializePaymentRequest();
        payment.setAmount(plan.getAmt());
        payment.setEmail(user.getEmailAddress());
        payment.setCallbackUrl("https://nearby.serchservice.com/tx-verify");

        return payment;
    }

    private void save(GoUser user, GoAddonPlan plan, InitializePaymentData data) {
        GoAddonTransaction transaction = new GoAddonTransaction();

        transaction.setVerified(false);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setUser(user);
        transaction.setPlan(plan);
        transaction.setReference(data.getReference());
        goAddonTransactionRepository.save(transaction);
    }

    @Override
    public ApiResponse<GoAddonVerificationResponse> see(String reference) {
        GoAddonTransaction transaction = goAddonTransactionRepository.findByReference(reference)
                .orElseThrow(() -> new SerchException("Transaction not found"));

        GoAddonVerificationResponse response = GoMapper.instance.verification(transaction.getPlan().getAddon());
        if(transaction.getPlan().getAddon().getPlans().isEmpty()) {
            response.setPlans(new ArrayList<>());
        } else {
            response.setPlans(transaction.getPlan().getAddon().getPlans().stream().map(GoMapper.instance::plan).toList());
        }
        response.setActivator(GoMapper.instance.plan(transaction.getPlan()));

        return new ApiResponse<>(response);
    }

    @Override
    @Transactional
    public ApiResponse<List<GoUserAddonResponse>> add(String reference) {
        GoUser user = authUtil.getGoUser();
        GoAddonTransaction transaction = goAddonTransactionRepository.findByReference(reference)
                .orElseThrow(() -> new SerchException("Transaction not found"));

        if(user.getId().equals(transaction.getUser().getId())) {
            if(transaction.getStatus() == SUCCESSFUL) {
                return new ApiResponse<>(
                        "A %s addon is now added to your list of addons".formatted(transaction.getPlan().getName()),
                        addons(user),
                        HttpStatus.OK
                );
            } else {
                try {
                    var data = pay.verify(transaction.getReference());
                    transaction.setStatus(SUCCESSFUL);
                    transaction.setVerified(true);
                    transaction.setUpdatedAt(TimeUtil.now());
                    goAddonTransactionRepository.save(transaction);

                    user.setCustomerCode(data.getCustomer().getCode());
                    user.setUpdatedAt(TimeUtil.now());
                    goUserRepository.save(user);

                    create(user, transaction.getPlan(), data.getAuthorization());

                    return new ApiResponse<>(
                            "A %s addon is now added to your list of addons".formatted(transaction.getPlan().getName()),
                            addons(user),
                            HttpStatus.OK
                    );
                } catch (Exception e) {
                    transaction.setStatus(FAILED);
                    transaction.setVerified(false);
                    transaction.setUpdatedAt(TimeUtil.now());
                    goAddonTransactionRepository.save(transaction);

                    throw new WalletException(e.getMessage());
                }
            }
        } else {
            throw new SerchException("Access denied. Error while verifying identity.");
        }
    }

    @Override
    @Transactional
    public void create(GoUser user, GoAddonPlan plan, PaymentAuthorization authorization) {
        goUserAddonRepository.deleteByPlanAndUser(plan.getAddon().getId(),  user.getId());
        GoPaymentAuthorization auth = goPaymentAuthorizationRepository.save(GoMapper.instance.auth(authorization));

        GoUserAddon addon = new GoUserAddon();
        addon.setUser(user);
        addon.setPlan(plan);
        addon.setIsRecurring(plan.isRecurring());
        addon.setAuthorization(auth);
        addon.setSubscriptionDate(LocalDate.now());
        addon.setNextBillingDate(getNextBillingDate(plan.getInterval()));
        addon = goUserAddonRepository.save(addon);

        auth.setAddon(addon);
    }

    private LocalDate getNextBillingDate(GoAddonPlanInterval interval) {
        LocalDate date = LocalDate.now();

        return switch (interval) {
            case DAILY -> date.plusDays(1);
            case WEEKLY -> date.plusWeeks(1);
            case MONTHLY -> date.plusMonths(1);
            case QUARTERLY -> date.plusMonths(3);
            case YEARLY -> date.plusYears(1);
            default -> date;
        };
    }

    @Override
    @Transactional
    public ApiResponse<Object> change(String id, Boolean useExistingAuthorization) {
        GoUser user = authUtil.getGoUser();
        GoAddonPlan plan = goAddonPlanRepository.findById(id).orElseThrow(() -> new SerchException("Plan not found"));
        Optional<GoUserAddon> addon = goUserAddonRepository.findExisting(plan.getAddon().getId(), user.getId());

        if(addon.isPresent()) {
            if(addon.get().hasSwitch()) {
                goUserAddonChangeRepository.delete(addon.get().getChange());
            }

            if(addon.get().isActive()) {

                GoUserAddonChange change = new GoUserAddonChange();
                change.setAddon(addon.get());
                change.setPlan(plan);
                change.setUseExistingAuthorization(useExistingAuthorization);
                goUserAddonChangeRepository.save(change);

                return new ApiResponse<>("Your new plan will take effect when your current plan expires.", addons(user), HttpStatus.OK);
            } else if(useExistingAuthorization) {
                if(addon.get().hasAuthorization()) {
                    var data = pay.charge(PaymentChargeRequest.build(
                            addon.get().getUser().getEmailAddress(),
                            plan.getAmt(),
                            addon.get().getAuthorization().getAuthorizationCode()
                    ));

                    buildTransaction(user, plan, data);
                    addon.get().setPlan(plan);
                    addon.get().setNextBillingDate(getNextBillingDate(plan.getInterval()));
                    addon.get().setStatus(GoUserAddonStatus.ACTIVE);
                    addon.get().setSubscriptionDate(LocalDate.now());
                    addon.get().setUpdatedAt(TimeUtil.now());

                    goUserAddonRepository.save(addon.get());

                    return new ApiResponse<>("You've successfully changed your plan for %s addon.".formatted(plan.getAddon().getName()), addons(user), HttpStatus.OK);
                } else {
                    throw new SerchException("You do not have any auth set, so disable use of existing auth.");
                }
            } else {
                InitializePaymentData data = pay.initialize(request(user, plan));
                save(user, plan, data);

                return new ApiResponse<>(data);
            }
        } else {
            throw new SerchException("You have not activated a plan for %s addon".formatted(plan.getAddon().getName()));
        }
    }

    private void buildTransaction(GoUser user, GoAddonPlan plan, PaymentVerificationData data) {
        GoAddonTransaction transaction = new GoAddonTransaction();

        transaction.setVerified(true);
        transaction.setStatus(TransactionStatus.SUCCESSFUL);
        transaction.setUser(user);
        transaction.setPlan(plan);
        transaction.setReference(data.getReference());
        goAddonTransactionRepository.save(transaction);
    }

    @Override
    public ApiResponse<List<GoUserAddonResponse>> cancel(Long id) {
        GoUser user = authUtil.getGoUser();
        GoUserAddon addon = goUserAddonRepository.findById(id).orElseThrow(() -> new SerchException("Plan not found"));

        if(user.getId().equals(addon.getUser().getId())) {
            if(addon.isActive()) {
                addon.setStatus(GoUserAddonStatus.CANCELLED);
                addon.setUpdatedAt(TimeUtil.now());
                goUserAddonRepository.save(addon);

                return new ApiResponse<>(addons(user));
            } else {
                throw new SerchException("You cannot cancel an inactive plan");
            }
        } else {
            throw new SerchException("Access denied. You are not authorized to perform this action");
        }
    }

    @Override
    @Transactional
    public ApiResponse<List<GoUserAddonResponse>> cancelSwitch(Long id) {
        GoUser user = authUtil.getGoUser();
        GoUserAddon addon = goUserAddonRepository.findById(id).orElseThrow(() -> new SerchException("Plan not found"));

        if(user.getId().equals(addon.getUser().getId())) {
            if(addon.hasSwitch()) {
                goUserAddonChangeRepository.delete(addon.getChange());

                addon.setUpdatedAt(TimeUtil.now());
                goUserAddonRepository.save(addon);

                return new ApiResponse<>("Switch cancelled", addons(user), HttpStatus.OK);
            } else {
                throw new SerchException("You do not have any plan you want to switch to");
            }
        } else {
            throw new SerchException("Access denied. You are not authorized to perform this action");
        }
    }

    @Override
    public ApiResponse<List<GoUserAddonResponse>> activate(Long id) {
        GoUser user = authUtil.getGoUser();
        GoUserAddon addon = goUserAddonRepository.findById(id).orElseThrow(() -> new SerchException("Plan not found"));

        if(user.getId().equals(addon.getUser().getId())) {
            if(addon.isDeactivated()) {
                addon.setStatus(GoUserAddonStatus.ACTIVE);
                addon.setUpdatedAt(TimeUtil.now());
                goUserAddonRepository.save(addon);

                return new ApiResponse<>(addons(user));
            } else {
                throw new SerchException("Your %s plan has expired. You can, however, renew the plan if you want to".formatted(addon.getPlan().getName()));
            }
        } else {
            throw new SerchException("Access denied. You are not authorized to perform this action");
        }
    }

    @Override
    public ApiResponse<Object> renew(Long id, Boolean useExistingAuthorization) {
        GoUser user = authUtil.getGoUser();
        GoUserAddon addon = goUserAddonRepository.findById(id).orElseThrow(() -> new SerchException("Plan not found"));

        if(user.getId().equals(addon.getUser().getId())) {
            if(addon.isExpired()) {
                if(useExistingAuthorization) {
                    if(addon.hasAuthorization()) {
                        var data = pay.charge(PaymentChargeRequest.build(
                                addon.getUser().getEmailAddress(),
                                addon.getPlan().getAmt(),
                                addon.getAuthorization().getAuthorizationCode()
                        ));

                        buildTransaction(user, addon.getPlan(), data);
                        addon.setPlan(addon.getPlan());
                        addon.setNextBillingDate(getNextBillingDate(addon.getPlan().getInterval()));
                        addon.setStatus(GoUserAddonStatus.ACTIVE);
                        addon.setSubscriptionDate(LocalDate.now());
                        addon.setUpdatedAt(TimeUtil.now());

                        goUserAddonRepository.save(addon);

                        return new ApiResponse<>("You've successfully renewed your plan for %s addon.".formatted(addon.getPlan().getAddon().getName()), addons(user), HttpStatus.OK);
                    } else {
                        throw new SerchException("You do not have any auth set, so disable use of existing auth.");
                    }
                } else {
                    InitializePaymentData data = pay.initialize(request(user, addon.getPlan()));
                    save(user, addon.getPlan(), data);

                    return new ApiResponse<>(data);
                }
            } else {
                throw new SerchException("Your %s plan %s. You cannot renew it for now until it expires.".formatted(addon.getPlan().getName(), addon.getStatus().getSentence()));
            }
        } else {
            throw new SerchException("Access denied. You are not authorized to perform this action");
        }
    }

    @Override
    public ApiResponse<List<GoUserAddonResponse>> getAll() {
        return new ApiResponse<>(addons(authUtil.getGoUser()));
    }

    private List<GoUserAddonResponse> addons(GoUser user) {
        List<GoUserAddon> addons = goUserAddonRepository.findByUser_Id(user.getId());

        if(addons == null || addons.isEmpty()) {
            return new ArrayList<>();
        }

        return addons.stream().map(this::response).toList();
    }

    private GoUserAddonResponse response(GoUserAddon addon) {
        GoUserAddonResponse response = GoMapper.instance.response(addon);
        response.setCard(GoMapper.instance.card(addon.getAuthorization()));
        response.setAddon(addon(addon.getPlan().getAddon(), addon.getPlan()));
        response.setTimeline(prepareTimeline(addon));
        response.setConstraint(prepareConstraint(addon));

        if(addon.hasSwitch()) {
            response.setSwitching(prepareSwitchInformation(addon));
        }

        return response;
    }

    private GoUserAddonResponse.Switch prepareSwitchInformation(GoUserAddon addon) {
        GoUserAddonResponse.Switch switching = GoMapper.instance.switching(addon.getChange().getPlan());
        switching.setCanCancel(addon.hasSwitch());
        switching.setStartsWhen(TimeUtil.formatFutureTime(addon.getChange().getCreatedAt(), addon.getUser().getTimezone()));

        return switching;
    }

    private GoUserAddonResponse.Timeline prepareTimeline(GoUserAddon addon) {
        GoUserAddonResponse.Timeline timeline = new GoUserAddonResponse.Timeline();
        timeline.setSubscribedAt(TimeUtil.formatDate(addon.getUser().getTimezone(), addon.getSubscriptionDate()));

        if(addon.getNextBillingDate() != null) {
            timeline.setNextBillingDate(TimeUtil.formatDate(addon.getUser().getTimezone(), addon.getNextBillingDate()));
        } else {
            timeline.setNextBillingDate("Forever");
        }

        return timeline;
    }

    private GoUserAddonResponse.Constraint prepareConstraint(GoUserAddon addon) {
        GoUserAddonResponse.Constraint constraint = new GoUserAddonResponse.Constraint();
        constraint.setCanActivate(addon.isDeactivated());
        constraint.setCanCancel(addon.isActive());
        constraint.setCanRenew(addon.isExpired());
        constraint.setCanSwitch(addon.isActive() && !addon.hasSwitch());

        return constraint;
    }
}
package com.serch.server.services.subscription.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.exceptions.subscription.PlanException;
import com.serch.server.mappers.SubscriptionMapper;
import com.serch.server.models.subscription.PlanBenefit;
import com.serch.server.models.subscription.PlanParent;
import com.serch.server.models.subscription.Subscription;
import com.serch.server.repositories.subscription.PlanParentRepository;
import com.serch.server.repositories.subscription.SubscriptionRepository;
import com.serch.server.services.subscription.responses.PlanChildResponse;
import com.serch.server.services.subscription.responses.PlanParentResponse;
import com.serch.server.services.subscription.services.PlanService;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the class that holds the implementation and logic of its wrapper class.
 *
 * @see PlanService
 */
@Service
@RequiredArgsConstructor
public class PlanImplementation implements PlanService {
    private final UserUtil userUtil;
    private final PlanParentRepository planParentRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public ApiResponse<List<PlanParentResponse>> plans() {
        Subscription subscription = subscriptionRepository.findByUser_Id(userUtil.getUser().getId())
                .orElse(new Subscription());
        List<PlanParent> parents = planParentRepository.findAll();

        if(parents.isEmpty()) {
            throw new PlanException("Plan is empty");
        } else {
            if(subscription.canUseFreePlan()) {
                List<PlanParentResponse> planList = parents.stream().map(parent -> {
                    PlanParentResponse response = SubscriptionMapper.INSTANCE.response(parent);
                    response.setType(parent.getType().getType());
                    updateChildren(parent, response);
                    return response;
                }).toList();
                return new ApiResponse<>(planList);
            }
            List<PlanParentResponse> planList = parents.stream()
                    .filter(res -> res.getType() != PlanType.FREE)
                    .map(parent -> {
                        PlanParentResponse response = SubscriptionMapper.INSTANCE.response(parent);
                        response.setType(parent.getType().getType());
                        updateChildren(parent, response);
                        return response;
                    }).collect(Collectors.toList());
            return new ApiResponse<>(planList);
        }
    }

    private void updateChildren(PlanParent parent, PlanParentResponse response) {
        if(parent.getBenefits() != null && !parent.getBenefits().isEmpty()) {
            response.setBenefits(new ArrayList<>(parent.getBenefits().stream().map(PlanBenefit::getBenefit).toList())
            );
        }
        if(parent.getChildren() != null && !parent.getChildren().isEmpty()) {
            response.setChildren(new ArrayList<>(parent.getChildren().stream().map(plan -> {
                PlanChildResponse child = SubscriptionMapper.INSTANCE.response(plan);
                child.setAmount(MoneyUtil.formatToNaira(BigDecimal.valueOf(Double.parseDouble(plan.getAmount()))));
                return child;
            }).toList()));
        }
    }
}
package com.serch.server.services.subscription.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.subscription.PlanType;
import com.serch.server.exceptions.subscription.PlanException;
import com.serch.server.mappers.SubscriptionMapper;
import com.serch.server.models.subscription.PlanBenefit;
import com.serch.server.models.subscription.PlanParent;
import com.serch.server.repositories.subscription.PlanParentRepository;
import com.serch.server.services.subscription.responses.PlanParentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final PlanParentRepository planParentRepository;

    @Override
    public ApiResponse<List<PlanParentResponse>> getPlans() {
        List<PlanParent> parents = planParentRepository.findAll();

        if(parents.isEmpty()) {
            throw new PlanException("Plan is empty");
        } else {
            List<PlanParentResponse> planList = parents.stream()
                    .map(parent -> {
                        PlanParentResponse response = SubscriptionMapper.INSTANCE.response(parent);
                        updateChildren(parent, response);
                        return response;
                    }).collect(Collectors.toList());
            return new ApiResponse<>(planList);
        }
    }

    @Override
    public ApiResponse<PlanParentResponse> getPlan(PlanType type) {
        PlanParent parent = planParentRepository.findByType(type)
                .orElseThrow(() -> new PlanException("Plan not found"));
        PlanParentResponse response = SubscriptionMapper.INSTANCE.response(parent);
        updateChildren(parent, response);
        return new ApiResponse<>(response);
    }

    @Override
    public void updateChildren(PlanParent parent, PlanParentResponse response) {
        if(parent.getBenefits() != null && !parent.getBenefits().isEmpty()) {
            response.setBenefits(new ArrayList<>(parent.getBenefits()
                    .stream()
                    .map(PlanBenefit::getBenefit)
                    .collect(Collectors.toList())));
        }
        if(parent.getChildren() != null && !parent.getChildren().isEmpty()) {
            response.setChildren(new ArrayList<>(parent.getChildren().stream()
                    .map(SubscriptionMapper.INSTANCE::response)
                    .collect(Collectors.toList())
            ));
        }
    }
}

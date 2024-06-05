package com.serch.server.mappers;

import com.serch.server.models.subscription.*;
import com.serch.server.services.payment.responses.PaymentAuthorization;
import com.serch.server.services.subscription.responses.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mappings({
            @Mapping(target = "benefits", source = "benefits", ignore = true),
            @Mapping(target = "children", source = "children", ignore = true),
            @Mapping(target = "type", source = "type", ignore = true),
            @Mapping(target = "amount", source = "amount")
    })
    PlanParentResponse response(PlanParent plan);
    PlanChildResponse response(PlanChild plan);
    @Mappings({
            @Mapping(target = "benefits", source = "benefits", ignore = true),
            @Mapping(target = "duration", source = "duration", ignore = true),
            @Mapping(target = "amount", source = "amount", ignore = true),
            @Mapping(target = "id", source = "id", ignore = true)
    })
    CurrentSubscriptionResponse subscription(PlanParent plan);
    @Mapping(target = "code", source = "authorizationCode")
    SubscriptionAuth auth(PaymentAuthorization authorization);
    SubscriptionCardResponse response(SubscriptionAuth auth);
    Subscription subscription(SubscriptionRequest request);
    @Mappings({
            @Mapping(target = "amount", source = "amount", ignore = true),
            @Mapping(target = "plan", source = "plan", ignore = true),
            @Mapping(target = "id", source = "id", ignore = true)
    })
    SubscriptionInvoice invoice(Subscription subscription);
    @Mapping(target = "subscription", source = "subscription", ignore = true)
    SubscriptionInvoiceResponse response(SubscriptionInvoice invoice);
}
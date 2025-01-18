package com.serch.server.mappers;

import com.serch.server.models.account.Profile;
import com.serch.server.models.trip.*;
import com.serch.server.domains.trip.requests.OnlineRequest;
import com.serch.server.domains.trip.requests.ShoppingItemRequest;
import com.serch.server.domains.trip.requests.TripInviteRequest;
import com.serch.server.domains.trip.requests.TripShareRequest;
import com.serch.server.domains.trip.responses.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TripMapper {
    TripMapper INSTANCE = Mappers.getMapper(TripMapper.class);

    Active active(OnlineRequest request);
    ActiveResponse response(Profile profile);
    OnlineRequest request(Active active);
    OnlineRequest request(Trip trip);
    ShoppingLocation shopping(OnlineRequest request);

    @Mapping(target = "amount", source = "amount", ignore = true)
    ShoppingItem shopping(ShoppingItemRequest request);

    ShoppingItemResponse response(ShoppingItem item);
    QuotationResponse response(TripInviteQuotation quotation);

    @Mapping(target = "trip", source = "trip", ignore = true)
    TripShare share(TripShareRequest request);

    Trip trip(TripInvite request);

    @Mapping(target = "audio", source = "audio", ignore = true)
    TripInvite request(TripInviteRequest request);

    @Mappings({
            @Mapping(target = "id", source = "id", ignore = true),
            @Mapping(target = "status", source = "status", ignore = true),
            @Mapping(target = "createdAt", source = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", source = "updatedAt", ignore = true),
            @Mapping(target = "isActive", source = "isActive", ignore = true),
            @Mapping(target = "access", source = "access", ignore = true),
            @Mapping(target = "serviceFee", source = "serviceFee", ignore = true),
            @Mapping(target = "userShare", source = "userShare", ignore = true),
            @Mapping(target = "isServiceFeePaid", source = "isServiceFeePaid", ignore = true),
            @Mapping(target = "cancelReason", source = "cancelReason", ignore = true),
            @Mapping(target = "timelines", source = "timelines", ignore = true),
            @Mapping(target = "shoppingItems", source = "shoppingItems", ignore = true),
            @Mapping(target = "invited", source = "invited", ignore = true),
            @Mapping(target = "location", source = "location", ignore = true),
            @Mapping(target = "payment", source = "payment", ignore = true),
            @Mapping(target = "shared", source = "shared", ignore = true),
    })
    Trip trip(Trip trip);

    @Mappings({
            @Mapping(target = "audio", source = "audio", ignore = true),
            @Mapping(target = "provider", source = "provider", ignore = true),
    })
    Trip trip(TripInviteRequest request);

    @Mappings({
            @Mapping(target = "shoppingLocation", source = "shoppingLocation"),
            @Mapping(target = "mode", source = "mode", ignore = true),
            @Mapping(target = "category", source = "category", ignore = true)
    })
    TripResponse response(TripInvite invite);

    @Mappings({
            @Mapping(target = "shoppingLocation", source = "location"),
            @Mapping(target = "authentication", source = "authentication", ignore = true),
            @Mapping(target = "mode", source = "mode", ignore = true),
            @Mapping(target = "category", source = "category", ignore = true),
            @Mapping(target = "provider", source = "provider", ignore = true)
    })
    TripResponse response(Trip trip);

    TripTimelineResponse response(TripTimeline timeline);

    @Mapping(target = "authentication", source = "authentication", ignore = true)
    TripShareResponse share(TripShare share);

    @Mapping(target = "place", source = "address")
    MapViewResponse view(Active active);

    MapViewResponse view(MapView view);

    MapView view(MapViewResponse view);
}

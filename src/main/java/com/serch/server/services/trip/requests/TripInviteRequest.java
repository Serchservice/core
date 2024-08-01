package com.serch.server.services.trip.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.core.storage.requests.FileUploadRequest;
import com.serch.server.enums.account.SerchCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class TripInviteRequest extends OnlineRequest {
    private UUID provider;
    private SerchCategory category;
    private FileUploadRequest audio;
    private String problem;
    private String car;
    private Integer amount;

    @JsonProperty("shopping_location")
    private OnlineRequest shoppingLocation;

    @JsonProperty("shopping_items")
    private List<ShoppingItemRequest> shoppingItems;
}
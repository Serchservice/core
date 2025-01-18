package com.serch.server.domains.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.serch.server.core.payment.responses.InitializePaymentData;
import com.serch.server.enums.trip.TripStatus;
import com.serch.server.enums.trip.TripType;
import com.serch.server.domains.trip.requests.OnlineRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class TripResponse extends TripActionResponse {
    private String id;
    private String mode = "";
    private String snt = "";
    private String skill = "";
    private String car = "";
    private String category = "";
    private String image = "";
    private String audio = "";
    private String problem = "";
    private String amount = "";
    private String address = "";
    private String placeId = "";
    private Double latitude;
    private Double longitude;
    private String label = "";
    private TripStatus status;
    private TripType type;
    private UserResponse provider;
    private UserResponse user;
    private TripShareResponse shared;
    private List<TripTimelineResponse> timelines;
    private List<QuotationResponse> quotations;
    private String authentication;
    private MapViewResponse location;

    @JsonProperty("requested_id")
    private String requestedId;

    @JsonProperty("is_provider")
    private Boolean isProvider = false;

    @JsonProperty("waiting_for_quotation_response")
    private Boolean waitingForQuotationResponse;

    @JsonProperty("try_service_payment_again")
    private Boolean tryServicePaymentAgain = false;

    @JsonProperty("service_fee")
    private String serviceFee;

    @JsonProperty("user_share_fee")
    private String userShareFee;

    @JsonProperty("pending_payment_data")
    private InitializePaymentData pendingPaymentData;

    @JsonProperty("try_payment_again")
    private Boolean tryPaymentAgain = false;

    @JsonProperty("shopping_location")
    private OnlineRequest shoppingLocation;

    @JsonProperty("shopping_items")
    private List<ShoppingItemResponse> shoppingItems;

    @JsonProperty("total_amount")
    private Integer totalAmount;

    @JsonProperty("total_shopping_amount")
    private String totalShoppingAmount;

    @JsonProperty("total_amount_spent_in_shopping")
    private String totalAmountSpentInShopping;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
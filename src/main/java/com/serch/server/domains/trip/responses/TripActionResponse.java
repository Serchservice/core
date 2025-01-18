package com.serch.server.domains.trip.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TripActionResponse {
    @JsonProperty("show_cancel")
    private Boolean showCancel;

    @JsonProperty("show_auth")
    private Boolean showAuth;

    @JsonProperty("show_end")
    private Boolean showEnd;

    @JsonProperty("show_share")
    private Boolean showShare;

    @JsonProperty("show_grant")
    private Boolean showGrant;

    @JsonProperty("show_deny")
    private Boolean showDeny;

    @JsonProperty("show_leave")
    private Boolean showLeave;

    @JsonProperty("show_notify_on_my_way")
    private Boolean showNotifyOnMyWay;

    @JsonProperty("show_notify_arrival")
    private Boolean showNotifyArrival;
}

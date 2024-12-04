package com.serch.server.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeatureActivity {
    TIP2FIX("Tip2Fix"),
    TRIP("Trip"),
    SCHEDULE("Schedule"),
    CHATROOM("Chatroom"),
    CHAT("Chat"),
    SHARED_LINK("Shared Link"),
    SHARED_INVITE("Shared Invite"),
    TRANSACTION("Transaction"),
    VOICE_CALL("Voice Call");

    private final String value;
}
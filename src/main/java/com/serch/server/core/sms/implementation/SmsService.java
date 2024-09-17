package com.serch.server.core.sms.implementation;

public interface SmsService {
    void sendTripAuth(String phone, String content);
}

package com.serch.server.core.sms.implementation;

import com.serch.server.core.sms.SmsConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsImplementation implements SmsService {
    private final SmsConfig config;

    @Override
    public void sendTripAuth(String phone, String content) {
        Message message = Message.creator(new PhoneNumber(phone), new PhoneNumber(config.getPhoneNumber()), content).create();
        log.info(String.format("SERCH::: (Twilio Sms Sender) SID %s", message.getSid()));
        log.info(String.format("SERCH::: (Twilio Sms Sender) BODY %s", message.getBody()));
    }
}

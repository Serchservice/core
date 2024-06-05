package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.enums.call.CallType;
import com.serch.server.exceptions.conversation.CallException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.conversation.Call;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.CallInformation;
import com.serch.server.services.conversation.responses.CallMemberData;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.responses.StartCallResponse;
import com.serch.server.services.notification.core.NotificationService;
import com.serch.server.services.notification.requests.NotificationRequest;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.*;
import io.agora.media.RtcTokenBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallImplementation implements CallService {
    private final UserUtil userUtil;
    private final WalletService walletService;
    private final NotificationService notificationService;
    private final CallRepository callRepository;
    private final ProfileRepository profileRepository;

    @Value("${application.call.api-key}")
    private String AGORA_APP_ID;
    @Value("${application.call.api-certificate}")
    private String AGORA_APP_CERTIFICATE;
    @Value("${application.call.token.expiration}")
    private Integer AGORA_TOKEN_EXPIRATION_TIME;
    @Value("${application.call.tip2fix.amount}")
    private Integer TIP2FIX_AMOUNT;

    private boolean userIsOnCall(UUID id) {
        return callRepository.findByUserId(id).stream().anyMatch(call -> call.getStatus() == CallStatus.ON_CALL);
    }

    private String authenticate(String channel, String user, boolean isUid) {
        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int) (System.currentTimeMillis() / 1000 + AGORA_TOKEN_EXPIRATION_TIME);
        if(isUid) {
            return token.buildTokenWithUid(
                    AGORA_APP_ID, AGORA_APP_CERTIFICATE, channel,
                    Integer.parseInt(user), RtcTokenBuilder.Role.Role_Publisher, timestamp
            );
        } else {
            return token.buildTokenWithUserAccount(
                    AGORA_APP_ID, AGORA_APP_CERTIFICATE, channel,
                    user, RtcTokenBuilder.Role.Role_Publisher, timestamp
            );
        }
    }

    @Override
    public ApiResponse<StartCallResponse> start(StartCallRequest request) {
        Profile caller = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new CallException("User not found"));
        Profile called = profileRepository.findById(request.getUser())
                .orElseThrow(() -> new CallException("User not found"));

        if(request.getType() == CallType.T2F && caller.getCategory() != SerchCategory.USER) {
            throw new CallException("Only Serch Users can start Tip2Fix calls");
        } else {
            if(userIsOnCall(caller.getId())) {
                throw new CallException("Call cannot be placed when you are on another");
            } else if(userIsOnCall(request.getUser())) {
                throw new CallException("%s is on another call. Wait a moment and try again".formatted(called.getFullName()));
            } else {
                if (request.getType() == CallType.T2F) {
                   walletService.checkBalanceForTip2Fix(caller.getId());
                }

                Call call = new Call();
                call.setCalled(called);
                call.setType(request.getType());
                call.setStatus(CallStatus.RINGING);
                call.setCaller(caller);
                if(request.getType() == CallType.T2F) {
                    call.setAmount(BigDecimal.valueOf(TIP2FIX_AMOUNT));
                }
                Call update = callRepository.save(call);
                String auth = authenticate(update.getChannel(), String.valueOf(called.getId()), false);

                CompletableFuture.runAsync(() -> {
                    NotificationRequest notificationRequest = NotificationRequest.builder()
                            .token(caller.getFcmToken())
                            .message("Hello")
                            .title("Hello")
                            .build();
                    notificationService.send(notificationRequest);
                });
                return new ApiResponse<>(StartCallResponse.builder()
                        .status(CallStatus.RINGING)
                        .app(AGORA_APP_ID)
                        .channel(update.getChannel())
                        .auth(auth)
                        .build()
                );
            }
        }
    }

    @Override
    public Map<String, String> authenticate(String channel, Integer uid) {
        Call call = callRepository.findById(channel).orElseThrow(() -> new CallException("Call not found"));
        String auth = authenticate(call.getChannel(), String.valueOf(uid), true);
        Map<String, String> data = new HashMap<>();
        data.put("rtcToken", auth);
        return data;
    }

    @Override
    public ApiResponse<StartCallResponse> verify(String channel) {
        Call call = callRepository.findByChannelAndCalled_Id(channel, userUtil.getUser().getId())
                .orElseThrow(() -> new CallException("Call not found"));
        return getStartCallResponse(call);
    }

    @Override
    public ApiResponse<StartCallResponse> update(String channel, CallStatus status) {
        Call call = callRepository.findById(channel).orElseThrow(() -> new CallException("Call not found"));
        call.checkIfActive();
        return getCallResponse(call, status);
    }

    @Override
    public ApiResponse<StartCallResponse> answer(String channel) {
        Call call = callRepository.findByChannelAndCalled_Id(channel, userUtil.getUser().getId())
                .orElseThrow(() -> new CallException("Call not found"));

        return getStartCallResponse(call);
    }

    private ApiResponse<StartCallResponse> getStartCallResponse(Call call) {
        call.checkIfActive();
        if(userIsOnCall(userUtil.getUser().getId())) {
            throw new CallException("You cannot pick a call when you are on another.");
        } else {
            if(call.getType() == CallType.T2F) {
                walletService.processTip2FixCallPayment(call.getChannel(), call.getCaller().getId(), call.getCalled());
            }
            return getCallResponse(call, CallStatus.ON_CALL);
        }
    }

    private ApiResponse<StartCallResponse> getCallResponse(Call call, CallStatus status) {
        String auth = authenticate(call.getChannel(), String.valueOf(userUtil.getUser().getId()), false);
        call.setStatus(status);
        call.setUpdatedAt(LocalDateTime.now());
        callRepository.save(call);
        return new ApiResponse<>(StartCallResponse.builder()
                .status(status)
                .app(AGORA_APP_ID)
                .channel(call.getChannel())
                .auth(auth)
                .build()
        );
    }

    @Override
    public ApiResponse<StartCallResponse> decline(String channel) {
        Call call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException("Call not found"));

        call.checkIfActive();
        if(call.getCalled().isSameAs(userUtil.getUser().getId())) {
            return getCallResponse(call, CallStatus.DECLINED);
        } else {
            throw new CallException("You cannot decline calls that is not made to you");
        }
    }

    @Override
    public ApiResponse<StartCallResponse> end(String channel) {
        Call call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException("Call not found"));

        call.checkIfActive();
        if(call.getCaller().isSameAs(userUtil.getUser().getId()) || call.getCalled().isSameAs(userUtil.getUser().getId())) {
            call.setStatus(CallStatus.CLOSED);
            call.setDuration(CallUtil.formatCallTimeFromDate(call.getUpdatedAt()));
            call.setUpdatedAt(LocalDateTime.now());
            callRepository.save(call);

            return new ApiResponse<>(StartCallResponse.builder().status(CallStatus.CLOSED).build());
        } else {
            throw new CallException("You cannot cancel calls you didn't start");
        }
    }

    @Override
    public ApiResponse<List<CallResponse>> logs() {
        List<CallResponse> list = new ArrayList<>();

        callRepository.findByUserId(userUtil.getUser().getId())
                .stream()
                .collect(Collectors.groupingBy(call -> call.getCalled().getId()))
                .forEach((user, calls) -> {
                    CallResponse response = new CallResponse();

                    Call recent = calls.get(0);
                    response.setRecent(createCallInformation(recent));
                    response.setMember(createCallMemberData(recent.getCalled()));

                    List<CallInformation> history = new ArrayList<>();
                    history.add(response.getRecent());

                    List<CallInformation> previous = calls.stream()
                            .skip(1) // Skip the most recent call
                            .map(this::createCallInformation)
                            .toList();
                    history.addAll(previous);
                    response.setHistory(history);

                    list.add(response);
                });
        return new ApiResponse<>(list);
    }

    private CallInformation createCallInformation(Call call) {
        CallInformation info = new CallInformation();
        info.setChannel(call.getChannel());
        info.setLabel(TimeUtil.formatDay(call.getCreatedAt()));
        info.setDuration(call.getDuration());
        info.setOutgoing(call.getCaller().isSameAs(userUtil.getUser().getId())); // Check if the user is the caller
        info.setType(call.getType());
        info.setStatus(call.getStatus());
        return info;
    }

    private CallMemberData createCallMemberData(Profile profile) {
        CallMemberData data = new CallMemberData();
        data.setMember(profile.getId());
        data.setName(profile.getFullName());
        data.setAvatar(profile.getAvatar());
        data.setCategory(profile.getCategory().getType());
        data.setImage(profile.getCategory().getImage());
        return data;
    }

    @Override
    public ApiResponse<String> checkSession(Integer duration, String channel) {
        Call call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException("Call not found"));

        if(CallUtil.getHours(duration) == 1) {
            try {
                walletService.checkBalanceForTip2Fix(call.getCaller().getId());
                walletService.processTip2FixCallPayment(call.getChannel(), call.getCaller().getId(), call.getCalled());
                call.setSessionCount(call.getSessionCount() + 1);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);
                return new ApiResponse<>("Continue with Tip2Fix", HttpStatus.OK);
            } catch (Exception e) {
                if(call.getRetries() == 3) {
                    call.setStatus(CallStatus.CLOSED);
                } else {
                    call.setRetries(call.getRetries() + 1);
                }
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);
                throw new CallException(e.getMessage());
            }
        } else {
            return new ApiResponse<>("Continue with Tip2Fix", HttpStatus.OK);
        }
    }

    @Override
    public void closeRingingCalls() {
        List<Call> calls  = callRepository.findAllRinging();
        if(calls != null && !calls.isEmpty()) {
            calls.stream().filter(call -> {
                LocalDateTime now = LocalDateTime.now();
                Duration duration = Duration.between(call.getCreatedAt(), now);
                return duration.getSeconds() > 65;
            }).forEach(call -> {
                call.setStatus(CallStatus.MISSED);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);
            });
        }
    }
}

package com.serch.server.services.conversation.services;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.enums.call.CallType;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.conversation.CallException;
import com.serch.server.exceptions.transaction.WalletException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.conversation.Call;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.CallInformation;
import com.serch.server.services.conversation.responses.CallMemberData;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.responses.ActiveCallResponse;
import com.serch.server.core.notification.core.NotificationService;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.*;
import io.getstream.chat.java.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallImplementation implements CallService {
    private final UserUtil userUtil;
    private final SimpMessagingTemplate template;
    private final WalletService walletService;
    private final NotificationService notificationService;
    private final CallRepository callRepository;
    private final ProfileRepository profileRepository;

    @Value("${application.call.api-key}")
    private String CALL_APP_KEY;

    @Value("${application.call.token.expiration}")
    private Integer CALL_TOKEN_EXPIRATION_TIME;

    @Value("${application.call.tip2fix.amount}")
    private Integer TIP2FIX_AMOUNT;

    private boolean userIsOnCall(UUID id) {
        return callRepository.findByUserId(id).stream().anyMatch(call -> call.getStatus() == CallStatus.ON_CALL);
    }

    @Override
    @Transactional
    public ApiResponse<ActiveCallResponse> start(StartCallRequest request) {
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
                    walletService.checkBalanceForTip2Fix(userUtil.getUser().getId());
                }

                Call call = new Call();
                call.setCalled(called);
                call.setType(request.getType());
                call.setStatus(CallStatus.RINGING);
                call.setCaller(caller);
                call.setDuration("");
                if(request.getType() == CallType.T2F) {
                    call.setAmount(BigDecimal.valueOf(TIP2FIX_AMOUNT));
                    call.setSession(1);
                }
                Call update = callRepository.save(call);

                notificationService.send(request.getUser(), prepareResponse(update));
                return new ApiResponse<>(prepareResponse(update));
            }
        }
    }

    private ActiveCallResponse prepareResponse(Call call) {
        Profile profile = call.getCalled().isSameAs(userUtil.getUser().getId())
                ? call.getCaller()
                : call.getCalled();

        return ActiveCallResponse.builder()
                .status(call.getStatus())
                .app(CALL_APP_KEY)
                .channel(call.getChannel())
                .name(profile.getFullName())
                .type(call.getType())
                .user(profile.getId())
                .isCaller(call.getCaller().isSameAs(userUtil.getUser().getId()))
                .avatar(profile.getAvatar())
                .image(profile.getCategory().getImage())
                .category(profile.getCategory().getType())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<String> auth() {
        String token = User.createToken(String.valueOf(userUtil.getUser().getId()), null, null);

        return new ApiResponse<>("Authentication successful", token, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponse<String> auth(String channel) {
        Call call = callRepository.findById(channel).orElseThrow(() -> new CallException("Call not found"));

        String token;
        if(call.isVoice()) {
            token = User.createToken(String.valueOf(userUtil.getUser().getId()), null, null);
        } else {
            var calendar = new GregorianCalendar();
            calendar.add(Calendar.MINUTE, CALL_TOKEN_EXPIRATION_TIME);

            token = User.createToken(String.valueOf(userUtil.getUser().getId()), calendar.getTime(), null);
        }

        return new ApiResponse<>("Authentication successful", token, HttpStatus.OK);
    }

    @Override
    @Transactional
    public void answer(String channel) {
        Call call = callRepository.findByChannelAndCalled_Id(channel, userUtil.getUser().getId()).orElse(null);
        if(call == null) {
            sendError("Call not found", null);
        } else {
            try {
                call.checkIfActive();
            } catch (Exception e) {
                sendError(e.getMessage(), call.getChannel());
            }
            if(userIsOnCall(userUtil.getUser().getId())) {
                sendError("You cannot pick a call when you are on another.", call.getChannel());
            } else {
                if(call.getType() == CallType.T2F) {
                    try {
                        walletService.processTip2FixCallPayment(call.getChannel(), call.getCaller().getId(), call.getCalled());
                    } catch (WalletException e) {
                        sendError(e.getMessage(), call.getChannel());
                        return;
                    }
                }

                call.setStatus(CallStatus.ON_CALL);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);
                sendToChannelMembers(call);
            }
        }
    }

    private void sendError(String error, String channel) {
        ActiveCallResponse response = ActiveCallResponse.builder()
                .error(error)
                .errorCode(ExceptionCodes.CALL_ERROR)
                .build();

        if(channel != null) {
            template.convertAndSend(
                    "/platform/%s/%s".formatted(channel, String.valueOf(userUtil.getUser().getId())),
                    response
            );
        } else {
            template.convertAndSend("/platform/%s".formatted(String.valueOf(userUtil.getUser().getId())), error);
        }
    }

    private void sendToChannelMembers(Call call) {
        ActiveCallResponse response = getCallResponse(call, call.getCaller());
        template.convertAndSend("/platform/%s/%s".formatted(call.getChannel(), String.valueOf(call.getCalled().getId())), response);

        response = getCallResponse(call, call.getCalled());
        template.convertAndSend("/platform/%s/%s".formatted(call.getChannel(), String.valueOf(call.getCaller().getId())), response);
    }

    private ActiveCallResponse getCallResponse(Call call, Profile profile) {
        return ActiveCallResponse.builder()
                .status(call.getStatus())
                .app(CALL_APP_KEY)
                .channel(call.getChannel())
                .name(profile.getFullName())
                .type(call.getType())
                .user(profile.getId())
                .avatar(profile.getAvatar())
                .isCaller(profile.isSameAs(call.getCaller().getId()))
                .image(profile.getCategory().getImage())
                .session(call.getSession())
                .category(profile.getCategory().getType())
                .build();
    }

    @Override
    @Transactional
    public void update(String channel, CallStatus status) {
        Call call = callRepository.findById(channel).orElse(null);
        if(call == null) {
            sendError("Call not found", null);
        } else {
            try {
                call.checkIfActive();
            } catch (Exception e) {
                sendError(e.getMessage(), call.getChannel());
            }

            call.setStatus(status);
            call.setUpdatedAt(LocalDateTime.now());
            callRepository.save(call);

            sendToChannelMembers(call);
        }
    }

    @Override
    @Transactional
    public void decline(String channel) {
        Call call = callRepository.findById(channel).orElse(null);
        if(call == null) {
            sendError("Call not found", null);
        } else {
            try {
                call.checkIfActive();
            } catch (Exception e) {
                sendError(e.getMessage(), call.getChannel());
            }
            if(call.getCalled().isSameAs(userUtil.getUser().getId())) {
                update(channel, CallStatus.DECLINED);
            } else {
                sendError("You cannot decline calls that is not made to you", call.getChannel());
            }
        }
    }

    @Override
    @Transactional
    public void end(String channel, String time) {
        Call call = callRepository.findById(channel).orElse(null);
        if(call == null) {
            sendError("Call not found", null);
        } else {
            try {
                call.checkIfActive();
            } catch (Exception e) {
                sendError(e.getMessage(), call.getChannel());
            }
            if(call.getCaller().isSameAs(userUtil.getUser().getId()) || call.getCalled().isSameAs(userUtil.getUser().getId())) {
                call.setStatus(CallStatus.CLOSED);
                call.setDuration(time);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);

                sendToChannelMembers(call);
            } else {
                sendError("You cannot cancel calls you didn't start", call.getChannel());
            }
        }
    }

    @Override
    @Transactional
    public void checkSession(Integer duration, String channel) {
        Call call = callRepository.findById(channel).orElse(null);
        if(call == null) {
            sendError("Call not found", null);
        } else {
            if (CallUtil.isSession(duration)) {
                try {
                    walletService.checkBalanceForTip2Fix(call.getCaller().getId());
                    walletService.processTip2FixCallPayment(call.getChannel(), call.getCaller().getId(), call.getCalled());
                    call.setSession(call.getSession() + 1);
                    call.setUpdatedAt(LocalDateTime.now());
                    callRepository.save(call);

                    sendToChannelMembers(call);
                } catch (Exception e) {
                    if(call.getRetries() == 3) {
                        call.setStatus(CallStatus.CLOSED);
                    } else {
                        call.setRetries(call.getRetries() + 1);
                    }
                    call.setUpdatedAt(LocalDateTime.now());
                    callRepository.save(call);

                    if(call.getRetries() == 3) {
                        sendError(
                                "This call will automatically end due to insufficient funds to update call session.",
                                call.getChannel()
                        );
                    } else {
                        sendError(
                                String.format(
                                        "Couldn't process call session update due to insufficient funds. %s try.",
                                        call.getRetries() == 1 ? "First" : "Second"
                                ),
                                call.getChannel()
                        );
                    }

                    if(call.getStatus() == CallStatus.CLOSED) {
                        sendToChannelMembers(call);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public ApiResponse<List<CallResponse>> logs() {
        List<CallResponse> list = new ArrayList<>();

        callRepository.findByUserId(userUtil.getUser().getId())
                .stream()
                .collect(Collectors.groupingBy(call -> call.getCalled().getId()))
                .forEach((user, calls) -> {
                    CallResponse response = new CallResponse();

                    Call recent = calls.get(0);
                    response.setRecent(createCallInformation(recent));
                    response.setMember(recent.getCalled().isSameAs(userUtil.getUser().getId())
                            ? createCallMemberData(recent.getCaller())
                            : createCallMemberData(recent.getCalled())
                    );

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
    @Transactional
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

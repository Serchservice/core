package com.serch.server.services.conversation.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.enums.call.CallType;
import com.serch.server.enums.transaction.TransactionType;
import com.serch.server.exceptions.conversation.CallException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.auth.User;
import com.serch.server.models.conversation.Call;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.call.CallRepository;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.CallInformation;
import com.serch.server.services.conversation.responses.CallMemberData;
import com.serch.server.services.conversation.responses.CallResponse;
import com.serch.server.services.conversation.responses.StartCallResponse;
import com.serch.server.services.conversation.services.CallService;
import com.serch.server.services.transaction.requests.BalanceUpdateRequest;
import com.serch.server.services.transaction.requests.PayRequest;
import com.serch.server.services.transaction.services.WalletService;
import com.serch.server.utils.CallUtil;
import com.serch.server.utils.UserUtil;
import com.serch.server.utils.WalletUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallImplementation implements CallService {
    private final UserUtil userUtil;
    private final WalletUtil walletUtil;
    private final WalletService walletService;
    private final CallRepository callRepository;
    private final ProfileRepository profileRepository;

    @Value("${serch.agora.app-id}")
    private String AGORA_APP_ID;
    @Value("${serch.tip2fix.call-amount}")
    private Integer TIP2FIX_CALL_AMOUNT;

    private boolean userIsOnCall(UUID id) {
        return callRepository.findBySerchId(id).stream().anyMatch(call ->
                call.getStatus() == CallStatus.CALLING || call.getStatus() == CallStatus.ON_CALL
                || call.getStatus() == CallStatus.RINGING
        );
    }

    @Override
    public ApiResponse<StartCallResponse> start(StartCallRequest request) {
        Profile caller = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new CallException("User not found"));
        Profile called = profileRepository.findById(request.getUser())
                .orElseThrow(() -> new CallException("User not found"));

        if(userIsOnCall(caller.getSerchId())) {
            throw new CallException("Call cannot be placed when you are on another");
        } else if(userIsOnCall(request.getUser())) {
            throw new CallException(
                    "%s is on another call. Wait a moment and try again".formatted(called.getFullName())
            );
        } else {
            if(request.getType() == CallType.T2F && caller.getCategory() != SerchCategory.USER) {
                throw new CallException("Only Serch Users can start Tip2Fix calls");
            } else if(request.getType() == CallType.T2F && !walletUtil.isBalanceSufficient(
                    BalanceUpdateRequest.builder()
                            .amount(BigDecimal.valueOf(TIP2FIX_CALL_AMOUNT))
                            .user(caller.getSerchId())
                            .type(TransactionType.T2F)
                            .build()
            )) {
                throw new CallException(
                        "Insufficient balance to start tip2fix. Tip2Fix is charged at ₦%s".formatted(TIP2FIX_CALL_AMOUNT)
                );
            } else {
                Call call = new Call();
                call.setCalled(called);
                call.setType(request.getType());
                call.setStatus(CallStatus.RINGING);
                call.setCaller(caller);
                Call update = callRepository.save(call);

                return new ApiResponse<>(StartCallResponse.builder()
                        .status(CallStatus.RINGING)
                        .app(AGORA_APP_ID)
                        .channel(update.getChannel())
                        .build()
                );
            }
        }
    }

    @Override
    public ApiResponse<StartCallResponse> answer(String channel) {
        User user = userUtil.getUser();
        Call call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException("Call not found"));

        call.checkIfActive();
        if(call.getCalled().isSameAs(user.getId())) {
            if(userIsOnCall(user.getId())) {
                throw new CallException("You cannot pick a call when you are on another.");
            } else {
                if(call.getType() == CallType.T2F) {
                    ApiResponse<String> response = processPayment(call);
                    if(response.getStatus().is2xxSuccessful()) {
                        return getCallResponse(call, CallStatus.ON_CALL);
                    } else {
                        throw new CallException(response.getMessage());
                    }
                } else {
                    return getCallResponse(call, CallStatus.ON_CALL);
                }
            }
        } else {
            throw new CallException("You cannot answer calls that is not made for you");
        }
    }

    private ApiResponse<String> processPayment(Call call) {
        PayRequest payRequest = new PayRequest();
        payRequest.setReceiver(call.getCalled().getSerchId());
        payRequest.setSender(call.getCaller().getSerchId());
        payRequest.setEvent(call.getChannel());
        payRequest.setType(TransactionType.T2F);
        return walletService.pay(payRequest);
    }

    private ApiResponse<StartCallResponse> getCallResponse(Call call, CallStatus status) {
        call.setStatus(status);
        call.setUpdatedAt(LocalDateTime.now());
        callRepository.save(call);
        return new ApiResponse<>(StartCallResponse.builder().status(status).build());
    }

    @Override
    public ApiResponse<StartCallResponse> cancel(String channel) {
        Call call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException("Call not found"));

        call.checkIfActive();
        if(call.getCaller().isSameAs(userUtil.getUser().getId())) {
            return getCallResponse(call, CallStatus.MISSED);
        } else {
            throw new CallException("You cannot cancel calls you didn't start");
        }
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

        callRepository.findBySerchId(userUtil.getUser().getId())
                .stream()
                .collect(Collectors.groupingBy(call -> call.getCalled().getSerchId()))
                .forEach((user, calls) -> {
                    CallResponse response = new CallResponse();

                    Call recent = calls.get(0);
                    response.setRecent(createCallInformation(recent));
                    response.setMember(createCallMemberData(recent.getCalled()));
                    response.setHistory(Collections.singletonList(response.getRecent()));

                    List<CallInformation> previous = calls.stream()
                            .skip(1) // Skip the most recent call
                            .map(this::createCallInformation)
                            .toList();
                    response.getHistory().addAll(previous);
                    list.add(response);
                });
        return new ApiResponse<>(list);
    }

    private CallInformation createCallInformation(Call call) {
        CallInformation info = new CallInformation();
        info.setLabel(call.getChannel());
        info.setDuration(call.getDuration());
        info.setOutgoing(call.getCaller().isSameAs(userUtil.getUser().getId())); // Check if the user is the caller
        info.setType(call.getType());
        info.setStatus(call.getStatus());
        return info;
    }

    private CallMemberData createCallMemberData(Profile profile) {
        CallMemberData memberData = new CallMemberData();
        memberData.setMember(profile.getSerchId());
        memberData.setName(profile.getFullName());
        memberData.setAvatar(profile.getAvatar());
        memberData.setCategory(profile.getCategory());
        return memberData;
    }

    @Override
    public ApiResponse<String> checkSession(Integer duration, String channel) {
        Call call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException("Call not found"));

        if(CallUtil.getHours(duration) == 1) {
            if(walletUtil.isBalanceSufficient(BalanceUpdateRequest.builder()
                    .amount(BigDecimal.valueOf(TIP2FIX_CALL_AMOUNT))
                    .user(call.getCaller().getSerchId())
                    .type(TransactionType.T2F)
                    .build()
            )) {
                ApiResponse<String> response = processPayment(call);
                if(response.getStatus().is2xxSuccessful()) {
                    call.setSessionCount(call.getSessionCount() + 1);
                    call.setUpdatedAt(LocalDateTime.now());
                    callRepository.save(call);
                    return new ApiResponse<>("Continue with Tip2Fix", HttpStatus.OK);
                } else {
                    call.setStatus(CallStatus.CLOSED);
                    call.setUpdatedAt(LocalDateTime.now());
                    callRepository.save(call);
                    throw new CallException(response.getMessage());
                }
            } else {
                call.setStatus(CallStatus.CLOSED);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);
                throw new CallException("Your balance is too low to continue with call");
            }
        } else {
            return new ApiResponse<>("Continue with Tip2Fix", HttpStatus.OK);
        }
    }
}

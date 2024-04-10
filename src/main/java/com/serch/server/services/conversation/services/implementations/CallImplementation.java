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
import com.serch.server.services.conversation.requests.CheckTip2FixSessionRequest;
import com.serch.server.services.conversation.requests.StartCallRequest;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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
                    PayRequest request = new PayRequest();
                    request.setReceiver(call.getCalled().getSerchId());
                    request.setSender(call.getCaller().getSerchId());
                    request.setEvent(call.getChannel());
                    walletService.payTip2Fix(request);
                }

                call.setStatus(CallStatus.ON_CALL);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);
                return new ApiResponse<>(StartCallResponse.builder().status(CallStatus.ON_CALL).build());
            }
        } else {
            throw new CallException("You cannot answer calls that is not made for you");
        }
    }

    @Override
    public ApiResponse<StartCallResponse> cancel(String channel) {
        Call call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException("Call not found"));

        call.checkIfActive();
        if(call.getCaller().isSameAs(userUtil.getUser().getId())) {
            call.setStatus(CallStatus.MISSED);
            call.setUpdatedAt(LocalDateTime.now());
            callRepository.save(call);

            return new ApiResponse<>(StartCallResponse.builder().status(CallStatus.MISSED).build());
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
            call.setStatus(CallStatus.DECLINED);
            call.setUpdatedAt(LocalDateTime.now());
            callRepository.save(call);

            return new ApiResponse<>(StartCallResponse.builder().status(CallStatus.DECLINED).build());
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
    public ApiResponse<List<CallHistoryResponse>> calls() {
        List<CallHistoryResponse> list = new ArrayList<>();

        callRepository.findBySerchId(userUtil.loggedInUserId()).forEach(call ->
                extractedCallHistoryResponse(call, list)
        );
        if(!list.isEmpty()) {
            list.sort(Comparator.comparing(CallHistoryResponse::getCalledAt));
        }
        return new ApiResponse<>(list);
    }

    private void extractedCallHistoryResponse(Call call, List<CallHistoryResponse> list) {
        UUID otherUserId;

        if (isCurrentUserTheCaller(call)) {
            otherUserId = call.getCalled().getSerchId();
        } else {
            otherUserId = call.getCaller().getSerchId();
        }
        Call recent = callRepository.findMostRecentCall(userUtil.loggedInUserId(), otherUserId);
        if(recent != null) {
            getCallHistoryResponse(recent, otherUserId, list);
        }
    }

    private void getCallHistoryResponse(Call recent, UUID otherUserId, List<CallHistoryResponse> list) {
        CallHistoryResponse response = new CallHistoryResponse();
        response.setChannel(recent.getChannel());
        response.setType(recent.getType());
        response.setDuration(recent.getDuration());
        response.setStatus(recent.getStatus());
        response.setStartTime(TimeUtil.formatTime(recent.getCreatedAt()));
        response.setEndTime(TimeUtil.formatTime(recent.getUpdatedAt()));
        response.setIsOutgoing(isCurrentUserTheCaller(recent));
        response.setCallMateId(otherUserId);
        response.setCallMateName(
                isCurrentUserTheCaller(recent)
                        ? recent.getCalled().getFullName() : recent.getCaller().getFullName()
        );
        response.setCallMateAvatar(
                isCurrentUserTheCaller(recent)
                        ? recent.getCalled().getAvatar() : recent.getCaller().getAvatar()
        );
        response.setCallMateCategory(
                isCurrentUserTheCaller(recent)
                        ? recent.getCalled().getCategory() : recent.getCaller().getCategory()
        );
        response.setCalledAt(recent.getCreatedAt());
        list.add(response);
    }

    private boolean isCurrentUserTheCaller(Call call) {
        return call.getCaller().getSerchId() == userUtil.loggedInUserId();
    }

    @Override
    public ApiResponse<List<CallResponse>> logs(UUID userId) {
        List<CallResponse> list = new ArrayList<>();

        callRepository.findAllBySerchId(userUtil.loggedInUserId(), userId).forEach(call ->
                prepareCallResponse(call, list)
        );
        if(!list.isEmpty()) {
            list.sort(Comparator.comparing(CallResponse::getCalledAt));
        }
        return new ApiResponse<>(list);
    }

    @Override
    public ApiResponse<String> checkSession(CheckTip2FixSessionRequest request) {
        Call call = callRepository.findById(request.getChannel())
                .orElseThrow(() -> new CallException("Call not found"));

        if(CallUtil.getHours(request.getDuration()) == 1) {
            if(walletUtil.isBalanceSufficient(BalanceUpdateRequest.builder()
                    .amount(BigDecimal.valueOf(TIP2FIX_CALL_AMOUNT))
                    .user(call.getCaller().getSerchId())
                    .type(TransactionType.T2F)
                    .build()
            )) {
                pay(call.getCaller().getSerchId(), call.getCalled().getSerchId());
                call.setSessionCount(call.getSessionCount() + 1);
                call.setUpdatedAt(LocalDateTime.now());
                callRepository.save(call);

                return new ApiResponse<>("Continue with Tip2Fix", HttpStatus.OK);
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

    private void prepareCallResponse(Call call, List<CallResponse> list) {
        CallResponse response = new CallResponse();
        response.setCalledAt(call.getCreatedAt());
        response.setLabel(TimeUtil.formatDay(call.getCreatedAt()));
        response.setTime(call.getDuration());
        response.setType(call.getType());
        response.setDuration(call.getDuration());
        response.setStatus(call.getStatus());
        response.setIsOutgoing(isCurrentUserTheCaller(call));
        response.setCalledAt(call.getCreatedAt());
        list.add(response);
    }
}

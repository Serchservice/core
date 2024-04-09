package com.serch.server.services.conversation.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.enums.call.CallType;
import com.serch.server.exceptions.conversation.CallException;
import com.serch.server.models.account.Profile;
import com.serch.server.models.conversation.Call;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.call.CallRepository;
import com.serch.server.services.conversation.requests.StartCallRequest;
import com.serch.server.services.conversation.responses.StartCallResponse;
import com.serch.server.services.conversation.services.CallService;
import com.serch.server.services.conversation.services.Tip2FixService;
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
    private final Tip2FixService tip2FixService;
    private final CallRepository callRepository;

    @Value("${serch.agora.app-id}")
    private String AGORA_APP_ID;
    @Value("${serch.tip2fix.call-limit}")
    private Integer CALL_LIMIT;
    private final ProfileRepository profileRepository;

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
            } else if(request.getType() == CallType.T2F &&
                    !walletUtil.isBalanceSufficient(BigDecimal.valueOf(CALL_LIMIT), caller.getSerchId())
            ) {
                throw new CallException(
                        "Insufficient balance to start tip2fix. Tip2Fix is charged at ₦%s".formatted(CALL_LIMIT)
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
    public ApiResponse<StartCallResponse> accept(String channel) {
        Profile called = profileRepository.findById(userUtil.getUser().getId())
                .orElseThrow(() -> new CallException("User not found"));
        Call call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException("Call not found"));

        if(userIsOnCall(called.getSerchId())) {
            throw new CallException("You cannot pick a call when you are on another.");
        } else {
            if(call.getType() == CallType.T2F) {
                tip2FixService.pay(call.getCaller().getSerchId(), call.getCalled().getSerchId());
            }

            call.setStatus(CallStatus.ON_CALL);
            call.setUpdatedAt(LocalDateTime.now());
            callRepository.save(call);
            return new ApiResponse<>(StartCallResponse.builder().status(CallStatus.ON_CALL).build());
        }
    }

    @Override
    public ApiResponse<StartCallResponse> leave(String channel) {
        var call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException(
                        "An error occurred while locating call. Contact support if this continues."
                ));
        if(isCurrentUserTheCaller(call)) {
            call.setStatus(CallStatus.MISSED);
            call.setUpdatedAt(LocalDateTime.now());
            callRepository.save(call);

            return new ApiResponse<>(CallStatus.MISSED);
        } else {
            throw new CallException("You cannot leave a call you didn't start");
        }
    }

    @Override
    public ApiResponse<StartCallResponse> decline(String channel) {
        var call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException(
                        "An error occurred while locating call. Contact support if this continues."
                ));
        if(call.getCalled().getSerchId() == userUtil.loggedInUserId()) {
            call.setStatus(CallStatus.DECLINED);
            call.setUpdatedAt(LocalDateTime.now());
            call.setEndedBy(call.getCalled());
            callRepository.save(call);

            return new ApiResponse<>(CallStatus.DECLINED);
        } else {
            throw new CallException("You cannot decline a call you were not invited for");
        }
    }

    @Override
    public ApiResponse<String> end(String channel) {
        var call = callRepository.findById(channel)
                .orElseThrow(() -> new CallException(
                        "An error occurred while locating call. Contact support if this continues."
                ));
        if(call.getCalled().getSerchId() == userUtil.loggedInUserId()) {
            call.setStatus(CallStatus.CLOSED);
            call.setEndedBy(call.getCalled());
            call.setDuration(CallUtil.formatCallTimeFromDate(call.getUpdatedAt()));
            call.setUpdatedAt(LocalDateTime.now());
            callRepository.save(call);

            return new ApiResponse<>("Call ended", HttpStatus.OK);
        } else if(isCurrentUserTheCaller(call)) {
            call.setStatus(CallStatus.CLOSED);
            call.setDuration(CallUtil.formatCallTimeFromDate(call.getUpdatedAt()));
            call.setUpdatedAt(LocalDateTime.now());
            call.setEndedBy(call.getCaller());
            callRepository.save(call);

            return new ApiResponse<>("Call ended", HttpStatus.OK);
        } else {
            throw new CallException("You cannot end a call you were not invited for");
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
    public ApiResponse<List<CallResponse>> view(UUID userId) {
        List<CallResponse> list = new ArrayList<>();

        callRepository.findAllBySerchId(userUtil.loggedInUserId(), userId).forEach(call ->
                prepareCallResponse(call, list)
        );
        if(!list.isEmpty()) {
            list.sort(Comparator.comparing(CallResponse::getCalledAt));
        }
        return new ApiResponse<>(list);
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

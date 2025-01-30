package com.serch.server.domains.conversation.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.enums.account.SerchCategory;
import com.serch.server.enums.call.CallStatus;
import com.serch.server.enums.call.CallType;
import com.serch.server.exceptions.ExceptionCodes;
import com.serch.server.exceptions.conversation.CallException;
import com.serch.server.mappers.ConversationMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.conversation.Call;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.domains.conversation.requests.StartCallRequest;
import com.serch.server.domains.conversation.requests.UpdateCallRequest;
import com.serch.server.domains.conversation.responses.*;
import com.serch.server.domains.conversation.services.CallService;
import com.serch.server.domains.transaction.services.WalletService;
import com.serch.server.utils.CallUtil;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import io.getstream.chat.java.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallImplementation implements CallService {
    private final AuthUtil authUtil;
    private final WalletService walletService;
    private final CallRepository callRepository;
    private final ProfileRepository profileRepository;

    @Value("${application.call.api-key}")
    private String CALL_APP_KEY;

    @Value("${application.call.token.expiration}")
    private Integer CALL_TOKEN_EXPIRATION_TIME;

    @Value("${application.call.tip2fix.amount}")
    private Integer TIP2FIX_AMOUNT;

    @Value("${application.call.tip2fix.session}")
    private Integer TIP2FIX_SESSION;

    private boolean userIsOnCall(UUID id) {
        CallPeriodResponse period = CallUtil.getPeriod(authUtil.getUser().getTimezone());

        return callRepository.findByUserId(id, period.getStart(), period.getEnd())
                .stream().anyMatch(call -> call.getStatus() == CallStatus.ON_CALL);
    }

    @Override
    @Transactional
    public ApiResponse<ActiveCallResponse> start(StartCallRequest request) {
        Profile caller = profileRepository.findById(authUtil.getUser().getId())
                .orElseThrow(() -> new CallException("User not found", false));
        Profile called = profileRepository.findById(request.getUser())
                .orElseThrow(() -> new CallException("User not found", false));

        if(request.getType() == CallType.T2F && caller.getCategory() != SerchCategory.USER) {
            throw new CallException("Only Serch Users can start Tip2Fix calls", false);
        } else {
            if(userIsOnCall(caller.getId())) {
                throw new CallException("Call cannot be placed when you are on another", false);
            } else if(userIsOnCall(request.getUser())) {
                throw new CallException("%s is on another call. Wait a moment and try again".formatted(called.getFullName()), false);
            } else {
                if (request.getType() == CallType.T2F) {
                    walletService.checkBalanceForTip2Fix(authUtil.getUser().getId());
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

                return new ApiResponse<>(prepareResponse(callRepository.save(call)));
            }
        }
    }

    private ActiveCallResponse prepareResponse(Call call) {
        Profile profile = call.getCalled().isSameAs(authUtil.getUser().getId())
                ? call.getCaller()
                : call.getCalled();

        return ActiveCallResponse.builder()
                .status(call.getStatus())
                .app(CALL_APP_KEY)
                .channel(call.getChannel())
                .name(profile.getFullName())
                .type(call.getType())
                .user(profile.getId())
                .isCaller(call.getCaller().isSameAs(authUtil.getUser().getId()))
                .avatar(profile.getAvatar())
                .image(profile.getCategory().getImage())
                .category(profile.getCategory().getType())
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<String> auth() {
        String token = User.createToken(String.valueOf(authUtil.getUser().getId()), null, null);

        return new ApiResponse<>("Authentication successful", token, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponse<String> auth(String channel) {
        Call call = callRepository.findById(channel).orElseThrow(() -> new CallException("Call not found", false));

        String token;
        if(call.isVoice()) {
            token = User.createToken(String.valueOf(authUtil.getUser().getId()), null, null);
        } else {
            var calendar = new GregorianCalendar();
            calendar.add(Calendar.MINUTE, CALL_TOKEN_EXPIRATION_TIME);

            token = User.createToken(String.valueOf(authUtil.getUser().getId()), calendar.getTime(), null);
        }

        return new ApiResponse<>("Authentication successful", token, HttpStatus.OK);
    }

    @Override
    @Transactional
    public ApiResponse<ActiveCallResponse> update(UpdateCallRequest request) {
        Call call = callRepository.findByChannelAndUserId(request.getChannel(), authUtil.getUser().getId())
                .orElseThrow(() -> new CallException("Call not found", true));

        call.checkIfActive();

        if(request.getStatus() == CallStatus.ON_CALL) {
            if(userIsOnCall(authUtil.getUser().getId())) {
                throw new CallException("You cannot pick a call when you are on another.", false);
            } else {
                if(call.getType() == CallType.T2F) {
                    walletService.processTip2FixCallPayment(call.getChannel(), call.getCaller().getId(), call.getCalled());
                }
            }
        } else if(request.getStatus() == CallStatus.DECLINED) {
            if(!call.getCalled().isSameAs(authUtil.getUser().getId())) {
                throw new CallException("You cannot decline calls that is not made to you", false);
            }
        } else if(request.getStatus() == CallStatus.CLOSED) {
            if(call.getCaller().isSameAs(authUtil.getUser().getId()) || call.getCalled().isSameAs(authUtil.getUser().getId())) {
                call.setDuration(request.getTime());
            } else {
                throw new CallException("You cannot cancel calls you don't belong to.", false);
            }
        }

        call.setStatus(request.getStatus());
        call.setUpdatedAt(TimeUtil.now());
        callRepository.save(call);

        return new ApiResponse<>(getCallResponse(call));
    }

    @Override
    public ApiResponse<ActiveCallResponse> checkSession(UpdateCallRequest request) {
        Call call = callRepository.findByChannelAndUserId(request.getChannel(), authUtil.getUser().getId())
                .orElseThrow(() -> new CallException("Call not found", true));

        call.checkIfActive();

        if (CallUtil.isSession(request.getDuration(), TIP2FIX_SESSION)) {
            try {
                walletService.checkBalanceForTip2Fix(call.getCaller().getId());
                walletService.processTip2FixCallPayment(call.getChannel(), call.getCaller().getId(), call.getCalled());
                call.setSession(call.getSession() + 1);
                call.setUpdatedAt(TimeUtil.now());
                callRepository.save(call);

                return new ApiResponse<>(getCallResponse(call));
            } catch (Exception e) {
                if(call.getRetries() == 3) {
                    call.setStatus(CallStatus.CLOSED);
                } else {
                    call.setRetries(call.getRetries() + 1);
                }
                call.setUpdatedAt(TimeUtil.now());
                callRepository.save(call);

                ActiveCallResponse response = getCallResponse(call);

                if(call.getRetries() == 3) {
                    response.setError("This call will automatically end due to insufficient funds to update call session.");
                    response.setErrorCode(ExceptionCodes.CALL_ERROR);
                } else {
                    response.setError(String.format(
                            "Couldn't process call session update due to insufficient funds. %s try.",
                            call.getRetries() == 1 ? "First" : "Second"
                    ));
                }

                return new ApiResponse<>(response);
            }
        } else {
            return new ApiResponse<>(getCallResponse(call));
        }
    }

    @Override
    public ApiResponse<ActiveCallResponse> fetch(String channel) {
        Call call = callRepository.findByChannelAndUserId(channel, authUtil.getUser().getId())
                .orElseThrow(() -> new CallException("Call not found", true));
        call.checkIfActive();

        return new ApiResponse<>(getCallResponse(call));
    }

    private ActiveCallResponse getCallResponse(Call call) {
        Profile profile = call.getCalled().isSameAs(authUtil.getUser().getId()) ? call.getCaller() : call.getCalled();

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
    public ApiResponse<List<CallResponse>> logs(Integer page, Integer size) {
        List<CallResponse> list = new ArrayList<>();

        Page<Call> items = getCalls(page, size);

        if(items != null && !items.getContent().isEmpty()) {
            items.getContent().stream().sorted(Comparator.comparing(Call::getCreatedAt))
                    .collect(Collectors.groupingBy(call -> call.getCaller().isSameAs(authUtil.getUser().getId())
                            ? call.getCalled().getId()
                            : call.getCaller().getId()
                    ))
                    .forEach((user, calls) -> {
                        CallResponse response = new CallResponse();

                        Call recent = calls.getLast();
                        response.setRecent(createCallInformation(recent));
                        response.setMember(recent.getCalled().isSameAs(authUtil.getUser().getId())
                                ? createCallMemberData(recent.getCaller())
                                : createCallMemberData(recent.getCalled())
                        );

                        List<CallInformation> history = calls.stream()
                                .sorted(Comparator.comparing(Call::getCreatedAt).reversed())
                                .skip(1) // Skip the most recent call
                                .map(this::createCallInformation)
                                .toList();
                        response.setHistory(history);

                        list.add(response);
                    });
        }

        return new ApiResponse<>(list);
    }

    private Page<Call> getCalls(Integer page, Integer size) {
        CallPeriodResponse period = CallUtil.getPeriod(authUtil.getUser().getTimezone());

        return callRepository.findByUserId(authUtil.getUser().getId(), period.getStart(), period.getEnd(), HelperUtil.getPageable(page, size));
    }

    private CallInformation createCallInformation(Call call) {
        CallInformation info = ConversationMapper.INSTANCE.info(call);
        info.setLabel(TimeUtil.formatDay(call.getCreatedAt(), authUtil.getUser().getTimezone()));
        info.setOutgoing(call.getCaller().isSameAs(authUtil.getUser().getId()));

        return info;
    }

    private CallMemberData createCallMemberData(Profile profile) {
        return ConversationMapper.INSTANCE.data(profile);
    }

    @Override
    @Transactional
    public void closeRingingCalls() {
        List<Call> calls  = callRepository.findAllRinging();
        if(calls != null && !calls.isEmpty()) {
            calls.stream().filter(call -> {
                Duration duration = Duration.between(call.getCreatedAt(), TimeUtil.now());
                return duration.getSeconds() > 65;
            }).forEach(call -> {
                call.setStatus(CallStatus.MISSED);
                call.setUpdatedAt(TimeUtil.now());
                callRepository.save(call);
            });
        }
    }
}

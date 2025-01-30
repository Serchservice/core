package com.serch.server.domains.transaction.services.implementations;

import com.serch.server.bases.ApiResponse;
import com.serch.server.domains.transaction.responses.AssociateTransactionData;
import com.serch.server.domains.transaction.responses.TransactionData;
import com.serch.server.domains.transaction.responses.TransactionGroupResponse;
import com.serch.server.domains.transaction.responses.TransactionResponse;
import com.serch.server.domains.transaction.services.TransactionResponseService;
import com.serch.server.enums.auth.Role;
import com.serch.server.mappers.TransactionMapper;
import com.serch.server.models.account.Profile;
import com.serch.server.models.conversation.Call;
import com.serch.server.models.schedule.Schedule;
import com.serch.server.models.transaction.Transaction;
import com.serch.server.models.trip.Trip;
import com.serch.server.repositories.account.ProfileRepository;
import com.serch.server.repositories.conversation.CallRepository;
import com.serch.server.repositories.schedule.ScheduleRepository;
import com.serch.server.repositories.transaction.TransactionRepository;
import com.serch.server.repositories.transaction.WalletRepository;
import com.serch.server.repositories.trip.TripRepository;
import com.serch.server.utils.HelperUtil;
import com.serch.server.utils.MoneyUtil;
import com.serch.server.utils.TimeUtil;
import com.serch.server.utils.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.serch.server.enums.transaction.TransactionStatus.SUCCESSFUL;

@Service
@RequiredArgsConstructor
public class TransactionResponseImplementation implements TransactionResponseService {
    private final TransactionRepository transactionRepository;
    private final AuthUtil authUtil;
    private final WalletRepository walletRepository;
    private final ScheduleRepository scheduleRepository;
    private final ProfileRepository profileRepository;
    private final CallRepository callRepository;
    private final TripRepository tripRepository;

    @Override
    public ApiResponse<List<TransactionGroupResponse>> transactions(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> transactions = transactionRepository.findBySenderOrReceiver(String.valueOf(authUtil.getUser().getId()), pageable);

        if(transactions == null || transactions.getTotalElements() == 0) {
            return new ApiResponse<>(List.of());
        } else {
            return response(transactions.getContent());
        }
    }

    @Override
    public ApiResponse<List<TransactionGroupResponse>> recent() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> page = transactionRepository.findRecentBySenderOrReceiver(String.valueOf(authUtil.getUser().getId()), pageable);

        if(page == null || page.getTotalElements() == 0) {
            return new ApiResponse<>(List.of());
        } else {
            return response(page.getContent());
        }
    }

    @Override
    public TransactionResponse response(String id) {
        return transactionRepository.findById(id)
                .map(this::response)
                .orElse(null);
    }

    private ApiResponse<List<TransactionGroupResponse>> response(List<Transaction> transactions) {
        List<TransactionGroupResponse> list = new ArrayList<>();
        Map<LocalDate, List<Transaction>> map = transactions.stream()
                .collect(Collectors.groupingBy(transaction -> transaction.getCreatedAt().toLocalDate()));

        map.forEach((date, transactionList) -> {
            TransactionGroupResponse group = new TransactionGroupResponse();
            group.setLabel(TimeUtil.formatChatLabel(LocalDateTime.of(date, transactionList.getFirst().getCreatedAt().toLocalTime()), authUtil.getUser().getTimezone()));

            List<TransactionResponse> response = transactionList.stream()
                    .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                    .map(this::response).toList();
            group.setTransactions(response);
            list.add(group);
        });

        return new ApiResponse<>(list);
    }

    protected TransactionResponse response(Transaction transaction) {
        return switch (transaction.getType()) {
            case FUNDING -> prepareFundingTransactionResponse(transaction);
            case WITHDRAW -> prepareWithdrawTransactionResponse(transaction);
            case SCHEDULE -> prepareScheduleTransactionResponse(transaction);
            case TIP2FIX -> prepareTip2FixTransactionResponse(transaction);
            case TRIP_CHARGE -> prepareTripChargeTransactionResponse(transaction);
            case SHOPPING -> new TransactionResponse();
            case TRIP_SHARE -> prepareTripShareTransactionResponse(transaction);
        };
    }

    protected TransactionResponse prepareFundingTransactionResponse(Transaction transaction) {
        TransactionResponse response = getResponse(transaction, true);

        response.setRecipient("Your %s".formatted(
                walletRepository.findById(transaction.getAccount())
                        .map(wallet -> HelperUtil.wallet(wallet.getId()))
                        .orElse("Wallet")
        ));

        TransactionData data = TransactionMapper.INSTANCE.data(transaction);
        data.setHeader("Transaction %s".formatted(transaction.getStatus().getType()));
        data.setDescription("Your deposit of %s to your Serch wallet %s".formatted(
                MoneyUtil.formatToNaira(transaction.getAmount()),
                transaction.getStatus().getSentence()
        ));
        data.setDate(TimeUtil.formatDay(transaction.getCreatedAt(), authUtil.getUser().getTimezone()));
        data.setUpdatedAt(TimeUtil.formatDay(transaction.getUpdatedAt(), authUtil.getUser().getTimezone()));
        response.setData(data);

        return response;
    }

    private TransactionResponse prepareWithdrawTransactionResponse(Transaction transaction) {
        TransactionResponse response = getResponse(transaction, false);
        response.setRecipient(transaction.getAccount());

        TransactionData data = TransactionMapper.INSTANCE.data(transaction);
        data.setHeader("Transaction %s".formatted(transaction.getStatus().getType()));
        data.setDescription("Your debit of %s from your Serch wallet %s".formatted(
                MoneyUtil.formatToNaira(transaction.getAmount()),
                transaction.getStatus().getSentence()
        ));
        data.setDate(TimeUtil.formatDay(transaction.getCreatedAt(), authUtil.getUser().getTimezone()));
        data.setUpdatedAt(TimeUtil.formatDay(transaction.getUpdatedAt(), authUtil.getUser().getTimezone()));
        response.setData(data);

        return response;
    }

    protected TransactionResponse prepareScheduleTransactionResponse(Transaction transaction) {
        boolean isIncoming = String.valueOf(authUtil.getUser().getId()).equals(transaction.getAccount());
        TransactionResponse response = getResponse(transaction, isIncoming);

        TransactionData data = getData(transaction);
        data.setDescription(prepareScheduleDescription(transaction, isIncoming));
        response.setData(data);

        if(authUtil.getUser().isBusiness()) {
            addAssociateData(scheduleRepository.findById(transaction.getEvent()).map(Schedule::getProvider), response);
        }
        return response;
    }

    private TransactionData getData(Transaction transaction) {
        TransactionData data = TransactionMapper.INSTANCE.data(transaction);
        data.setHeader("Transaction %s".formatted(transaction.getStatus().getType()));
        data.setDate(TimeUtil.formatDay(transaction.getCreatedAt(), authUtil.getUser().getTimezone()));
        data.setUpdatedAt(TimeUtil.formatDay(transaction.getUpdatedAt(), authUtil.getUser().getTimezone()));

        return data;
    }

    protected TransactionResponse getResponse(Transaction transaction, boolean isIncoming) {
        String account;

        try {
            account = walletRepository.findByUser_Id(UUID.fromString(transaction.getAccount()))
                    .map(wallet -> HelperUtil.wallet(wallet.getId()))
                    .orElse("Wallet");
        } catch (IllegalArgumentException e) {
            account = walletRepository.findById(transaction.getAccount())
                    .map(wallet -> HelperUtil.wallet(wallet.getId()))
                    .orElse("Wallet");
        }

        TransactionResponse response = new TransactionResponse();
        response.setIsIncoming(isIncoming);
        response.setLabel(TimeUtil.formatDay(transaction.getCreatedAt(), authUtil.getUser().getTimezone()));
        response.setType(transaction.getType());
        response.setStatus(transaction.getStatus());
        response.setAmount(MoneyUtil.formatToNaira(transaction.getAmount()));
        response.setRecipient(isIncoming ? String.format("Your %s", account) : String.format(account));

        return response;
    }

    protected String prepareScheduleDescription(Transaction transaction, boolean isIncoming) {
        if(isIncoming) {
            if(transaction.getVerified() && transaction.getStatus() == SUCCESSFUL) {
                return getIncomingScheduleTransactionDescription(transaction, "%s was sent to your wallet because %s closed schedule %s with %s for %s on %s, late");
            } else {
                return getIncomingScheduleTransactionDescription(transaction, "%s will be sent to your wallet because %s closed schedule %s with %s for %s on %s, late");
            }
        } else {
            if(transaction.getVerified() && transaction.getStatus() == SUCCESSFUL) {
                return getOutgoingScheduleTransactionDescription(transaction, "%s was debited from your wallet because %s closed schedule %s with %s for %s on %s, late");
            } else {
                return getOutgoingScheduleTransactionDescription(transaction, "%s will be debited from your wallet because %s closed schedule %s with %s for %s on %s, late");
            }
        }
    }

    private String getIncomingScheduleTransactionDescription(Transaction transaction, String format) {
        Optional<Schedule> optional = scheduleRepository.findById(transaction.getEvent());

        return String.format(
                format,
                MoneyUtil.formatToNaira(transaction.getAmount()),
                optional.map(schedule -> profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse("someone")).orElse("someone"),
                optional.map(Schedule::getId).orElse(""),
                getScheduleParticipant(transaction),
                optional.map(Schedule::getTime).orElse(""),
                optional.map(schedule -> TimeUtil.formatDay(schedule.getCreatedAt(), authUtil.getUser().getTimezone())).orElse("")
        );
    }

    private String getOutgoingScheduleTransactionDescription(Transaction transaction, String format) {
        Optional<Schedule> optional = scheduleRepository.findById(transaction.getEvent());

        return String.format(
                format,
                MoneyUtil.formatToNaira(transaction.getAmount()),
                getScheduleParticipant(transaction),
                optional.map(Schedule::getId).orElse(""),
                optional.map(schedule -> profileRepository.findById(schedule.getClosedBy()).map(Profile::getFullName).orElse("someone")).orElse("someone"),
                optional.map(Schedule::getTime).orElse(""),
                optional.map(schedule -> TimeUtil.formatDay(schedule.getCreatedAt(), authUtil.getUser().getTimezone())).orElse("")
        );
    }

    protected String getScheduleParticipant(Transaction transaction) {
        return scheduleRepository.findById(transaction.getEvent()).map(schedule -> {
            /// If user is the same person that closed the schedule
            if(schedule.getUser().isSameAs(schedule.getClosedBy()) ) {
                /// If the logged-in user is same as the provider
                if(schedule.getProvider().isSameAs(authUtil.getUser().getId())) {
                    return "you";
                } else {
                    return profileRepository.findById(schedule.getProvider().getId()).map(Profile::getFullName).orElse("someone");
                }
            } else {
                /// If the logged-in user is same as the user
                if(schedule.getUser().isSameAs(authUtil.getUser().getId())) {
                    return "you";
                } else {
                    return profileRepository.findById(schedule.getUser().getId()).map(Profile::getFullName).orElse("someone");
                }
            }
        }).orElse("someone");
    }

    private void addAssociateData(Optional<Profile> associate, TransactionResponse response) {
        associate.ifPresent(profile -> response.setAssociate(AssociateTransactionData.builder()
                .avatar(profile.getAvatar())
                .name(profile.getFullName())
                .category(profile.getCategory().getType())
                .rating(profile.getRating())
                .image(profile.getCategory().getImage())
                .build()
        ));
    }

    protected TransactionResponse prepareTip2FixTransactionResponse(Transaction transaction) {
        boolean isIncoming = authUtil.getUser().getRole() != Role.USER;
        TransactionResponse response = getResponse(transaction, isIncoming);

        TransactionData data = getData(transaction);
        data.setDescription(prepareCallDescription(transaction, isIncoming));
        response.setData(data);

        if(authUtil.getUser().isBusiness()) {
            addAssociateData(callRepository.findById(transaction.getEvent()).map(Call::getCalled), response);
        }
        return response;
    }

    protected String prepareCallDescription(Transaction transaction, boolean isIncoming) {
        if(isIncoming) {
            if(transaction.getVerified() && transaction.getStatus() == SUCCESSFUL) {
                return getIncomingCallTransactionDescription(transaction, "%s was sent to your wallet because %s had a Tip2Fix session %s with %s");
            } else {
                return getIncomingCallTransactionDescription(transaction, "%s will be sent to your wallet because %s had a Tip2Fix session %s with %s");
            }
        } else {
            if(transaction.getVerified() && transaction.getStatus() == SUCCESSFUL) {
                return getOutgoingCallTransactionDescription(transaction, "%s was debited from your wallet because you had a Tip2Fix session %s with %s");
            } else {
                return getOutgoingCallTransactionDescription(transaction, "%s will be debited from your wallet because you had a Tip2Fix session %s with %s");
            }
        }
    }

    private String getOutgoingCallTransactionDescription(Transaction transaction, String format) {
        Optional<Call> optional = callRepository.findById(transaction.getEvent());

        return String.format(
                format,
                MoneyUtil.formatToNaira(transaction.getAmount()),
                optional.map(Call::getChannel).orElse(""),
                optional.map(call -> call.getCalled().getFullName()).orElse("someone")
        );
    }

    private String getIncomingCallTransactionDescription(Transaction transaction, String format) {
        Optional<Call> optional = callRepository.findById(transaction.getEvent());

        return String.format(
                format,
                MoneyUtil.formatToNaira(transaction.getAmount()),
                optional.map(call -> call.getCaller().getFullName()).orElse("someone"),
                optional.map(Call::getChannel).orElse(""),
                optional.map(call -> call.getCalled().getFullName()).orElse("someone")
        );
    }

    protected TransactionResponse prepareTripShareTransactionResponse(Transaction transaction) {
        boolean isIncoming = String.valueOf(authUtil.getUser().getId()).equals(transaction.getAccount());
        TransactionResponse response = getResponse(transaction, isIncoming);

        TransactionData data = getData(transaction);
        data.setDescription(prepareTripShareDescription(transaction));
        response.setData(data);

        if(authUtil.getUser().isBusiness()) {
            addAssociateData(tripRepository.findById(transaction.getEvent()).map(Trip::getProvider), response);
        }
        return response;
    }

    protected String prepareTripShareDescription(Transaction transaction) {
        if(authUtil.getUser().isBusiness()) {
            return String.format(
                    "This is the amount sent to the user that shared %s based on the attended trip %s",
                    tripRepository.findById(transaction.getEvent()).map(trip -> trip.getProvider().getFullName()).orElse("your associate provider"),
                    transaction.getEvent()
            );
        } else if(authUtil.getUser().isUser()) {
            return String.format("This is your received percentage for sharing the link that made %s happen. Keep sharing!!!", transaction.getEvent());
        } else {
            return String.format("This is the trip charge for your attended trip %s", transaction.getEvent());
        }
    }

    protected TransactionResponse prepareTripChargeTransactionResponse(Transaction transaction) {
        TransactionResponse response = getResponse(transaction, false);
        response.setRecipient("Service Fee");

        TransactionData data = getData(transaction);
        data.setDescription(prepareTripChargeDescription(transaction));
        response.setData(data);

        if(authUtil.getUser().isBusiness()) {
            addAssociateData(tripRepository.findById(transaction.getEvent()).map(Trip::getProvider), response);
        }

        return response;
    }

    protected String prepareTripChargeDescription(Transaction transaction) {
        if(authUtil.getUser().isBusiness()) {
            return String.format(
                    "This is a trip charge for %s based on the attended trip %s",
                    tripRepository.findById(transaction.getEvent()).map(trip -> trip.getProvider().getFullName()).orElse("your associate provider"),
                    transaction.getEvent()
            );
        } else {
            return String.format("This is the trip charge for your attended trip %s", transaction.getEvent());
        }
    }
}
